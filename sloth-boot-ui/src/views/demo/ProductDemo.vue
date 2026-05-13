<template>
  <div class="product-demo">
    <!-- 商品列表 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <span class="section-title">商品列表</span>
      </template>
      <el-button type="primary" :loading="listLoading" @click="fetchProducts" class="mb-16">
        刷新列表
      </el-button>
      <el-skeleton :rows="5" animated :loading="listLoading">
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

    <!-- 创建商品 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <span class="section-title">创建商品</span>
      </template>
      <el-form :model="createForm" label-width="80px" style="max-width: 500px">
        <el-form-item label="名称">
          <el-input v-model="createForm.name" placeholder="商品名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="createForm.description" type="textarea" :rows="2" placeholder="商品描述" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="createForm.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="createForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="createForm.category" placeholder="分类" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="createLoading" @click="handleCreate">创建</el-button>
        </el-form-item>
      </el-form>
    </el-card>

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

    <!-- 排行榜 -->
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { productApi } from '@/api/product'
import type { ProductDTO, ProductCreateRequest } from '@/api/types'

// 商品列表
const listLoading = ref(false)
const products = ref<ProductDTO[]>([])

const productPage = ref(1)
const paginatedProducts = computed(() => {
  const start = (productPage.value - 1) * 10
  return products.value.slice(start, start + 10)
})

async function fetchProducts() {
  listLoading.value = true
  try {
    products.value = await productApi.listProducts()
  } catch (e: any) {
    ElMessage.error('获取商品列表失败: ' + (e.message || e))
  } finally {
    listLoading.value = false
  }
}

// 查看商品详情
const detailVisible = ref(false)
const productDetail = ref<ProductDTO | null>(null)

async function viewProduct(id: number) {
  try {
    productDetail.value = await productApi.getProduct(id)
    detailVisible.value = true
  } catch (e: any) {
    ElMessage.error('获取商品详情失败: ' + (e.message || e))
  }
}

// 删除商品
async function deleteProduct(id: number) {
  try {
    await productApi.deleteProduct(id)
    ElMessage.success('删除成功')
    fetchProducts()
  } catch (e: any) {
    ElMessage.error('删除失败: ' + (e.message || e))
  }
}

// 创建商品
const createLoading = ref(false)
const createForm = reactive<ProductCreateRequest>({
  name: '',
  description: '',
  price: 0,
  stock: 0,
  category: ''
})

async function handleCreate() {
  if (!createForm.name.trim()) {
    ElMessage.warning('请输入商品名称')
    return
  }
  createLoading.value = true
  try {
    await productApi.createProduct(createForm)
    ElMessage.success('创建成功')
    fetchProducts()
  } catch (e: any) {
    ElMessage.error('创建失败: ' + (e.message || e))
  } finally {
    createLoading.value = false
  }
}

// 缓存策略对比
const cacheLoading = ref(false)
const cacheResults = ref<Record<string, { hit: boolean; costMs: number }>>({})

async function runCacheDemo() {
  cacheLoading.value = true
  try {
    cacheResults.value = await productApi.demoCacheStrategies()
  } catch (e: any) {
    ElMessage.error('缓存对比失败: ' + (e.message || e))
  } finally {
    cacheLoading.value = false
  }
}

// 布隆过滤器
const bloomLoading = ref(false)
const bloomResetLoading = ref(false)
const bloomStats = ref<Record<string, any> | null>(null)

async function fetchBloomStats() {
  bloomLoading.value = true
  try {
    bloomStats.value = await productApi.getBloomStats()
  } catch (e: any) {
    ElMessage.error('获取布隆过滤器统计失败: ' + (e.message || e))
  } finally {
    bloomLoading.value = false
  }
}

async function handleResetBloom() {
  bloomResetLoading.value = true
  try {
    await productApi.resetBloom()
    ElMessage.success('重置成功')
    fetchBloomStats()
  } catch (e: any) {
    ElMessage.error('重置失败: ' + (e.message || e))
  } finally {
    bloomResetLoading.value = false
  }
}

// 排行榜
const rankLoading = ref(false)
const voteLoading = ref(false)
const rankList = ref<any[]>([])

async function fetchRank() {
  rankLoading.value = true
  try {
    rankList.value = await productApi.getRank()
  } catch (e: any) {
    ElMessage.error('获取排行榜失败: ' + (e.message || e))
  } finally {
    rankLoading.value = false
  }
}

async function handleVote(productId: number) {
  voteLoading.value = true
  try {
    await productApi.voteProduct(productId)
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
.product-demo {
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
.mt-16 {
  margin-top: 16px;
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
