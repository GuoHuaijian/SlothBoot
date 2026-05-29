<template>
  <div class="redis-demo">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- Tab 1: 缓存策略 -->
      <el-tab-pane label="缓存策略" name="cache">
        <!-- 缓存策略对比 -->
        <el-card class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">缓存策略对比</span>
          </template>
          <el-button type="primary" :loading="cacheLoading" @click="runCacheDemo" class="mb-16">
            运行缓存对比
          </el-button>
          <el-row :gutter="16">
            <el-col v-for="(value, key) in cacheResults" :key="key" :span="8">
              <el-card shadow="never">
                <template #header>
                  <el-tag>{{ key }}</el-tag>
                </template>
                <p>命中: <el-tag :type="value.hit ? 'success' : 'info'" size="small">{{ value.hit ? '是' : '否' }}</el-tag></p>
                <p>耗时: <strong>{{ value.costMs }}ms</strong></p>
              </el-card>
            </el-col>
          </el-row>
        </el-card>

        <!-- 布隆过滤器 -->
        <el-card class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">布隆过滤器</span>
          </template>
          <el-button type="primary" :loading="bloomLoading" @click="fetchBloomStats" class="mr-8">
            刷新统计
          </el-button>
          <el-button type="warning" :loading="bloomResetLoading" @click="handleResetBloom">
            重置
          </el-button>
          <el-descriptions v-if="bloomStats" :column="3" border class="mt-16">
            <el-descriptions-item label="已插入数量">{{ bloomStats.count }}</el-descriptions-item>
            <el-descriptions-item label="预期插入量">{{ bloomStats.expectedInsertions }}</el-descriptions-item>
            <el-descriptions-item label="误判率(FPP)">{{ bloomStats.fpp }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-tab-pane>

      <!-- Tab 2: 分布式锁与幂等 -->
      <el-tab-pane label="分布式锁与幂等" name="lock">
        <!-- 商品列表 -->
        <el-card class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">商品列表</span>
          </template>
          <el-button type="primary" :loading="productListLoading" @click="fetchProducts" class="mb-16">
            刷新列表
          </el-button>
          <el-skeleton :rows="5" animated :loading="productListLoading">
            <template #default>
              <el-table :data="paginatedProducts" border stripe empty-text="暂无商品数据，点击上方按钮加载">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="name" label="名称" />
                <el-table-column prop="price" label="价格" width="100">
                  <template #default="{ row }">¥{{ row.price }}</template>
                </el-table-column>
                <el-table-column prop="stock" label="库存" width="80" />
                <el-table-column prop="category" label="分类" width="120" />
                <el-table-column label="操作" width="160">
                  <template #default="{ row }">
                    <el-button type="primary" link @click="viewProduct(row.id)">查看</el-button>
                    <el-popconfirm title="确认删除该商品？" @confirm="deleteProduct(row.id)">
                      <template #reference>
                        <el-button type="danger" link>删除</el-button>
                      </template>
                    </el-popconfirm>
                  </template>
                </el-table-column>
              </el-table>
              <el-pagination
                v-if="products.length > 10"
                v-model:current-page="productPage"
                :page-size="10"
                :total="products.length"
                layout="prev, pager, next"
                class="mt-16"
              />
            </template>
          </el-skeleton>
        </el-card>

        <!-- 商品详情对话框 -->
        <el-dialog v-model="detailVisible" title="商品详情" width="500px">
          <el-descriptions v-if="productDetail" :column="1" border>
            <el-descriptions-item label="ID">{{ productDetail.id }}</el-descriptions-item>
            <el-descriptions-item label="名称">{{ productDetail.name }}</el-descriptions-item>
            <el-descriptions-item label="描述">{{ productDetail.description }}</el-descriptions-item>
            <el-descriptions-item label="价格">¥{{ productDetail.price }}</el-descriptions-item>
            <el-descriptions-item label="库存">{{ productDetail.stock }}</el-descriptions-item>
            <el-descriptions-item label="分类">{{ productDetail.category }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ productDetail.createTime }}</el-descriptions-item>
          </el-descriptions>
        </el-dialog>

        <!-- 创建订单 -->
        <el-card class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">创建订单</span>
          </template>
          <el-form :model="orderForm" label-width="80px" style="max-width: 400px">
            <el-form-item label="商品">
              <el-select v-model="orderForm.productId" placeholder="请选择商品" style="width: 100%">
                <el-option
                  v-for="p in products"
                  :key="p.id"
                  :label="`${p.name} (¥${p.price})`"
                  :value="p.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="数量">
              <el-input-number v-model="orderForm.quantity" :min="1" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="createOrderLoading" @click="handleCreateOrder">创建订单</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 订单列表 -->
        <el-card class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">订单列表</span>
          </template>
          <el-button type="primary" :loading="orderListLoading" @click="fetchOrders" class="mb-16">
            刷新订单
          </el-button>
          <el-skeleton :rows="5" animated :loading="orderListLoading">
            <template #default>
              <el-table :data="paginatedOrders" border stripe empty-text="暂无订单数据">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="productName" label="商品" />
                <el-table-column prop="amount" label="金额" width="100">
                  <template #default="{ row }">¥{{ row.amount }}</template>
                </el-table-column>
                <el-table-column prop="quantity" label="数量" width="80" />
                <el-table-column label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="statusTagType(row.status)">{{ row.status }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="120">
                  <template #default="{ row }">
                    <el-button
                      v-if="row.status === 'CREATED'"
                      type="success"
                      link
                      @click="handlePay(row.id)"
                    >
                      支付
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-pagination
                v-if="orders.length > 10"
                v-model:current-page="orderPage"
                :page-size="10"
                :total="orders.length"
                layout="prev, pager, next"
                class="mt-16"
              />
            </template>
          </el-skeleton>
        </el-card>
      </el-tab-pane>

      <!-- Tab 3: 限流测试 -->
      <el-tab-pane label="限流测试" name="rateLimit">
        <el-card class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">限流测试</span>
          </template>
          <el-space>
            <el-button type="primary" :loading="rateLimitRunning" @click="runRateLimitTest">
              发送请求 (10次)
            </el-button>
          </el-space>
          <div v-if="rateLimitStats.total > 0" class="mt-16">
            <el-space wrap>
              <el-tag type="success">成功: {{ rateLimitStats.success }}</el-tag>
              <el-tag type="danger">失败: {{ rateLimitStats.fail }}</el-tag>
              <el-tag>总计: {{ rateLimitStats.total }}</el-tag>
            </el-space>
            <el-progress
              :percentage="Math.round((rateLimitStats.success / rateLimitStats.total) * 100)"
              :status="rateLimitStats.fail > 0 ? 'warning' : 'success'"
              class="mt-8"
            />
          </div>
        </el-card>
      </el-tab-pane>

      <!-- Tab 4: Pub/Sub 事件 -->
      <el-tab-pane label="Pub/Sub 事件" name="pubsub">
        <el-card class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">实时事件</span>
          </template>
          <el-button type="primary" :loading="eventsLoading" @click="fetchEvents" class="mb-16">
            刷新事件
          </el-button>
          <el-timeline v-if="events.length > 0">
            <el-timeline-item
              v-for="event in events"
              :key="event.orderId + event.eventTime"
              :timestamp="event.eventTime"
              placement="top"
              :type="statusTagType(event.status) as any"
            >
              <el-card shadow="never">
                <p><strong>订单 #{{ event.orderId }}</strong> - {{ event.status }}</p>
                <p>{{ event.message }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无事件" />
        </el-card>
      </el-tab-pane>

      <!-- Tab 5: ZSet 排行榜 -->
      <el-tab-pane label="ZSet 排行榜" name="rank">
        <el-card class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">排行榜</span>
          </template>
          <el-button type="primary" :loading="rankLoading" @click="fetchRank" class="mb-16">
            刷新排行榜
          </el-button>
          <el-skeleton :rows="3" animated :loading="rankLoading">
            <template #default>
              <el-table :data="rankList" border stripe empty-text="暂无排行数据">
                <el-table-column prop="productName" label="商品名称" />
                <el-table-column prop="score" label="票数" width="100" />
                <el-table-column label="操作" width="120">
                  <template #default="{ row }">
                    <el-button type="primary" size="small" :loading="voteLoading" @click="handleVote(row.productId)">
                      投票
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </template>
          </el-skeleton>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { redisApi } from '@/api/redis'
import type { ProductDTO, OrderDTO, OrderStatusEvent, ProductCreateRequest, OrderCreateRequest } from '@/api/types'

const activeTab = ref('cache')

// ==================== Cache ====================
const cacheLoading = ref(false)
const cacheResults = ref<Record<string, { hit: boolean; costMs: number }>>({})

async function runCacheDemo() {
  cacheLoading.value = true
  try {
    cacheResults.value = await redisApi.demoCacheStrategies()
  } catch (e: any) {
    ElMessage.error('缓存对比失败: ' + (e.message || e))
  } finally {
    cacheLoading.value = false
  }
}

// Bloom filter
const bloomLoading = ref(false)
const bloomResetLoading = ref(false)
const bloomStats = ref<Record<string, any> | null>(null)

async function fetchBloomStats() {
  bloomLoading.value = true
  try {
    bloomStats.value = await redisApi.getBloomStats()
  } catch (e: any) {
    ElMessage.error('获取布隆过滤器统计失败: ' + (e.message || e))
  } finally {
    bloomLoading.value = false
  }
}

async function handleResetBloom() {
  bloomResetLoading.value = true
  try {
    await redisApi.resetBloom()
    ElMessage.success('重置成功')
    fetchBloomStats()
  } catch (e: any) {
    ElMessage.error('重置失败: ' + (e.message || e))
  } finally {
    bloomResetLoading.value = false
  }
}

// ==================== Products & Orders ====================
const productListLoading = ref(false)
const products = ref<ProductDTO[]>([])
const productPage = ref(1)
const paginatedProducts = computed(() => {
  const start = (productPage.value - 1) * 10
  return products.value.slice(start, start + 10)
})

async function fetchProducts() {
  productListLoading.value = true
  try {
    products.value = await redisApi.listProducts()
  } catch (e: any) {
    ElMessage.error('获取商品列表失败: ' + (e.message || e))
  } finally {
    productListLoading.value = false
  }
}

// Product detail
const detailVisible = ref(false)
const productDetail = ref<ProductDTO | null>(null)

async function viewProduct(id: number) {
  try {
    productDetail.value = await redisApi.getProduct(id)
    detailVisible.value = true
  } catch (e: any) {
    ElMessage.error('获取商品详情失败: ' + (e.message || e))
  }
}

async function deleteProduct(id: number) {
  try {
    await redisApi.deleteProduct(id)
    ElMessage.success('删除成功')
    fetchProducts()
  } catch (e: any) {
    ElMessage.error('删除失败: ' + (e.message || e))
  }
}

// Create order
const createOrderLoading = ref(false)
const orderForm = reactive<OrderCreateRequest>({ productId: 0, quantity: 1 })

async function handleCreateOrder() {
  if (!orderForm.productId) {
    ElMessage.warning('请选择商品')
    return
  }
  createOrderLoading.value = true
  try {
    await redisApi.createOrder(orderForm)
    ElMessage.success('订单创建成功')
    fetchOrders()
  } catch (e: any) {
    ElMessage.error('创建订单失败: ' + (e.message || e))
  } finally {
    createOrderLoading.value = false
  }
}

// Order list
const orderListLoading = ref(false)
const orders = ref<OrderDTO[]>([])
const orderPage = ref(1)
const paginatedOrders = computed(() => {
  const start = (orderPage.value - 1) * 10
  return orders.value.slice(start, start + 10)
})

async function fetchOrders() {
  orderListLoading.value = true
  try {
    orders.value = await redisApi.listOrders()
  } catch (e: any) {
    ElMessage.error('获取订单列表失败: ' + (e.message || e))
  } finally {
    orderListLoading.value = false
  }
}

function statusTagType(status: string) {
  switch (status) {
    case 'CREATED': return 'info'
    case 'PAID': return 'success'
    case 'CANCELLED': return 'danger'
    default: return 'info'
  }
}

async function handlePay(id: number) {
  try {
    await redisApi.payOrder(id)
    ElMessage.success('支付成功')
    fetchOrders()
  } catch (e: any) {
    ElMessage.error('支付失败: ' + (e.message || e))
  }
}

// ==================== Rate Limit ====================
const rateLimitRunning = ref(false)
const rateLimitStats = reactive({ success: 0, fail: 0, total: 0 })

async function runRateLimitTest() {
  rateLimitRunning.value = true
  rateLimitStats.success = 0
  rateLimitStats.fail = 0
  rateLimitStats.total = 0

  for (let i = 0; i < 10; i++) {
    try {
      await redisApi.rateLimitTest()
      rateLimitStats.success++
    } catch {
      rateLimitStats.fail++
    }
    rateLimitStats.total++
    if (i < 9) {
      await new Promise(r => setTimeout(r, 200))
    }
  }
  rateLimitRunning.value = false
}

// ==================== Pub/Sub Events ====================
const eventsLoading = ref(false)
const events = ref<OrderStatusEvent[]>([])

async function fetchEvents() {
  eventsLoading.value = true
  try {
    events.value = await redisApi.getEvents()
  } catch (e: any) {
    ElMessage.error('获取事件失败: ' + (e.message || e))
  } finally {
    eventsLoading.value = false
  }
}

// ==================== Ranking ====================
const rankLoading = ref(false)
const voteLoading = ref(false)
const rankList = ref<any[]>([])

async function fetchRank() {
  rankLoading.value = true
  try {
    rankList.value = await redisApi.getRank()
  } catch (e: any) {
    ElMessage.error('获取排行榜失败: ' + (e.message || e))
  } finally {
    rankLoading.value = false
  }
}

async function handleVote(productId: number) {
  voteLoading.value = true
  try {
    await redisApi.voteProduct(productId)
    ElMessage.success('投票成功')
    fetchRank()
  } catch (e: any) {
    ElMessage.error('投票失败: ' + (e.message || e))
  } finally {
    voteLoading.value = false
  }
}
</script>

<style scoped>
.redis-demo {
  padding: 0;
  font-family: var(--font-body);
}
.redis-demo :deep(.el-tabs) {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  backdrop-filter: blur(8px);
}
.redis-demo :deep(.el-tabs__header) {
  background: var(--bg-raised);
  border-bottom: 1px solid var(--border);
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
  margin: 0;
}
.redis-demo :deep(.el-tabs__item) {
  color: var(--text-secondary);
  font-family: var(--font-display);
  font-weight: 600;
}
.redis-demo :deep(.el-tabs__item.is-active) {
  color: var(--accent);
}
.redis-demo :deep(.el-tabs__content) {
  padding: 24px;
}
.section-card {
  margin-bottom: 24px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  backdrop-filter: blur(8px);
  transition: all var(--transition-normal);
}
.section-card:hover {
  border-color: var(--accent-border);
  box-shadow: var(--shadow-glow);
}
.section-card :deep(.el-card) {
  background: var(--bg-elevated);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  backdrop-filter: blur(8px);
}
.section-title {
  font-family: var(--font-display);
  font-weight: 700;
  font-size: 16px;
  color: var(--text-primary);
}
.section-card :deep(.el-descriptions) {
  --el-descriptions-item-bordered-label-background: var(--bg-raised);
}
.section-card :deep(.el-descriptions-item__label) {
  color: var(--text-secondary);
}
.section-card :deep(.el-descriptions-item__content) {
  color: var(--text-primary);
  font-weight: 500;
}
.section-card :deep(.el-progress) {
  --el-color-primary: var(--accent);
}
.section-card :deep(.el-timeline-item__node--primary) {
  background-color: var(--accent);
  border-color: var(--accent);
}
.section-card :deep(.el-timeline-item__tail) {
  border-left-color: var(--border);
}
.section-card :deep(.el-timeline-item__wrapper) {
  padding-left: 20px;
}
.section-card :deep(.el-timeline-item__timestamp) {
  color: var(--text-muted);
  font-family: var(--font-mono);
  font-size: 12px;
}
.mt-16 {
  margin-top: 16px;
}
.mt-8 {
  margin-top: 8px;
}
.mb-16 {
  margin-bottom: 16px;
}
.mr-8 {
  margin-right: 8px;
}
:deep(.el-pagination) {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
