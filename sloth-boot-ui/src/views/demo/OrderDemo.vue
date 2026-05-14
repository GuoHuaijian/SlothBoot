<template>
  <div class="order-demo">
    <!-- 创建订单 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <span class="section-title">创建订单</span>
      </template>
      <el-form :model="createForm" label-width="80px" style="max-width: 400px">
        <el-form-item label="商品">
          <el-select v-model="createForm.productId" placeholder="请选择商品" style="width: 100%">
            <el-option
              v-for="p in products"
              :key="p.id"
              :label="`${p.name} (¥${p.price})`"
              :value="p.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="createForm.quantity" :min="1" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="createLoading" @click="handleCreate">创建订单</el-button>
          <el-button @click="fetchProducts">加载商品</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 订单列表 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <span class="section-title">订单列表</span>
      </template>
      <el-button type="primary" :loading="listLoading" @click="fetchOrders" class="mb-16">
        刷新订单
      </el-button>
      <el-skeleton :rows="5" animated :loading="listLoading">
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
            <el-table-column label="操作" width="160">
              <template #default="{ row }">
                <el-button
                  v-if="row.status === 'CREATED'"
                  type="success"
                  link
                  @click="handlePay(row.id)"
                >
                  支付
                </el-button>
                <el-button
                  v-if="row.status === 'CREATED'"
                  type="danger"
                  link
                  @click="handleCancel(row.id)"
                >
                  取消
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

    <!-- 限流测试 -->
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

    <!-- 实时事件 -->
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { orderApi } from '@/api/order'
import { productApi } from '@/api/product'
import type { OrderDTO, ProductDTO, OrderStatusEvent, OrderCreateRequest } from '@/api/types'

// 创建订单
const createLoading = ref(false)
const createForm = reactive<OrderCreateRequest>({ productId: 0, quantity: 1 })
const products = ref<ProductDTO[]>([])

async function fetchProducts() {
  try {
    products.value = await productApi.listProducts()
  } catch (e: any) {
    ElMessage.error('加载商品失败: ' + (e.message || e))
  }
}

async function handleCreate() {
  if (!createForm.productId) {
    ElMessage.warning('请选择商品')
    return
  }
  createLoading.value = true
  try {
    await orderApi.createOrder(createForm)
    ElMessage.success('订单创建成功')
    fetchOrders()
  } catch (e: any) {
    ElMessage.error('创建订单失败: ' + (e.message || e))
  } finally {
    createLoading.value = false
  }
}

// 订单列表
const listLoading = ref(false)
const orders = ref<OrderDTO[]>([])

const orderPage = ref(1)
const paginatedOrders = computed(() => {
  const start = (orderPage.value - 1) * 10
  return orders.value.slice(start, start + 10)
})

async function fetchOrders() {
  listLoading.value = true
  try {
    orders.value = await orderApi.listOrders()
  } catch (e: any) {
    ElMessage.error('获取订单列表失败: ' + (e.message || e))
  } finally {
    listLoading.value = false
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
    await orderApi.payOrder(id)
    ElMessage.success('支付成功')
    fetchOrders()
  } catch (e: any) {
    ElMessage.error('支付失败: ' + (e.message || e))
  }
}

async function handleCancel(id: number) {
  try {
    await orderApi.cancelOrder(id)
    ElMessage.success('取消成功')
    fetchOrders()
  } catch (e: any) {
    ElMessage.error('取消订单失败: ' + (e.message || e))
  }
}

// 限流测试
const rateLimitRunning = ref(false)
const rateLimitStats = reactive({ success: 0, fail: 0, total: 0 })

async function runRateLimitTest() {
  rateLimitRunning.value = true
  rateLimitStats.success = 0
  rateLimitStats.fail = 0
  rateLimitStats.total = 0

  for (let i = 0; i < 10; i++) {
    try {
      await orderApi.rateLimitTest()
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

// 实时事件
const eventsLoading = ref(false)
const events = ref<OrderStatusEvent[]>([])

async function fetchEvents() {
  eventsLoading.value = true
  try {
    events.value = await orderApi.getEvents()
  } catch (e: any) {
    ElMessage.error('获取事件失败: ' + (e.message || e))
  } finally {
    eventsLoading.value = false
  }
}

onMounted(() => {
  fetchProducts()
  fetchOrders()
})
</script>

<style scoped>
.order-demo {
  padding: 0;
  font-family: var(--font-body);
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
.section-title {
  font-family: var(--font-display);
  font-weight: 700;
  font-size: 16px;
  color: var(--text-primary);
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
:deep(.el-pagination) {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
