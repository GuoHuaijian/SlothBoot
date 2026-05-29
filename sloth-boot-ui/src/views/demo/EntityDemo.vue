<template>
  <div class="entity-demo">
    <el-tabs v-model="activeTab" class="entity-tabs">
      <!-- 用户管理 -->
      <el-tab-pane label="用户管理" name="user">
        <!-- 创建用户 -->
        <el-card class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">创建用户</span>
          </template>
          <el-form :model="userCreateForm" label-width="80px" style="max-width: 460px">
            <el-form-item label="用户名">
              <el-input v-model="userCreateForm.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userCreateForm.phone" placeholder="请输入手机号（可选）" />
            </el-form-item>
            <el-form-item label="身份证">
              <el-input v-model="userCreateForm.idCard" placeholder="请输入身份证号（可选）" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userCreateForm.email" placeholder="请输入邮箱（可选）" />
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="userCreateForm.gender">
                <el-radio :value="0">未知</el-radio>
                <el-radio :value="1">男</el-radio>
                <el-radio :value="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="部门ID">
              <el-input-number v-model="userCreateForm.deptId" :min="0" placeholder="部门ID" />
            </el-form-item>
            <el-form-item label="状态">
              <el-radio-group v-model="userCreateForm.status">
                <el-radio :value="0">正常</el-radio>
                <el-radio :value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="userCreateLoading" @click="handleCreateUser">创建用户</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 用户列表 -->
        <el-card class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">用户列表</span>
          </template>
          <el-form :inline="true" :model="userQueryParams" class="mb-16">
            <el-form-item label="用户名">
              <el-input v-model="userQueryParams.username" placeholder="模糊搜索" clearable />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userQueryParams.phone" placeholder="手机号" clearable />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="userQueryParams.status" placeholder="全部" clearable style="width: 100px">
                <el-option label="正常" :value="0" />
                <el-option label="停用" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="userListLoading" @click="fetchUsers">查询</el-button>
              <el-button @click="resetUserQuery">重置</el-button>
            </el-form-item>
          </el-form>

          <el-skeleton :rows="5" animated :loading="userListLoading">
            <template #default>
              <el-table :data="users" border stripe empty-text="暂无用户数据">
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="username" label="用户名" />
                <el-table-column prop="phone" label="手机号" />
                <el-table-column prop="email" label="邮箱" />
                <el-table-column label="性别" width="70">
                  <template #default="{ row }">
                    {{ genderLabel(row.gender) }}
                  </template>
                </el-table-column>
                <el-table-column label="状态" width="70">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
                      {{ row.status === 0 ? '正常' : '停用' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="170" />
                <el-table-column label="操作" width="160">
                  <template #default="{ row }">
                    <el-button type="primary" link size="small" @click="handleDesensitize(row.id)">
                      脱敏查看
                    </el-button>
                    <el-button type="danger" link size="small" @click="handleDeleteUser(row.id, row.username)">
                      删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-pagination
                v-if="userTotal > userPageSize"
                v-model:current-page="userPageNum"
                :page-size="userPageSize"
                :total="userTotal"
                layout="total, prev, pager, next"
                class="mt-16"
                @current-change="fetchUsers"
              />
            </template>
          </el-skeleton>
        </el-card>

        <!-- 脱敏查看结果 -->
        <el-card v-if="desensitizeResult" class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">脱敏查看结果</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="ID">{{ desensitizeResult.id }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ desensitizeResult.username }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ desensitizeResult.phone }}</el-descriptions-item>
            <el-descriptions-item label="身份证">{{ desensitizeResult.idCard }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ desensitizeResult.email }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ genderLabel(desensitizeResult.gender) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 数据权限演示 -->
        <el-card class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">数据权限演示</span>
          </template>
          <el-button type="warning" :loading="scopeLoading" @click="handleScopeQuery">
            查询数据权限范围
          </el-button>
          <el-skeleton :rows="3" animated :loading="scopeLoading" v-if="scopeFetched">
            <template #default>
              <el-table v-if="scopeUsers.length > 0" :data="scopeUsers" border stripe class="mt-16" empty-text="无数据">
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="username" label="用户名" />
                <el-table-column prop="phone" label="手机号" />
                <el-table-column label="状态" width="80">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
                      {{ row.status === 0 ? '正常' : '停用' }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-else description="无可见用户（数据权限限制）" />
            </template>
          </el-skeleton>
        </el-card>

        <!-- 批量导入 -->
        <el-card class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">批量导入</span>
          </template>
          <el-space>
            <el-button type="primary" :loading="userImportLoading" @click="handleImportUsers">
              导入示例用户
            </el-button>
          </el-space>
          <div v-if="userImportResult !== null" class="result-area mt-16">
            <el-tag type="success" size="large">导入成功: {{ userImportResult }} 条记录</el-tag>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 部门管理 -->
      <el-tab-pane label="部门管理" name="dept">
        <!-- 创建部门 -->
        <el-card class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">创建部门</span>
          </template>
          <el-form :model="deptCreateForm" label-width="80px" style="max-width: 460px">
            <el-form-item label="部门名称">
              <el-input v-model="deptCreateForm.name" placeholder="请输入部门名称" />
            </el-form-item>
            <el-form-item label="上级部门">
              <el-tree-select
                v-model="deptCreateForm.parentId"
                :data="deptTreeOptions"
                :props="{ label: 'name', value: 'id', children: 'children' }"
                placeholder="选择上级部门（可选）"
                clearable
                check-strictly
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="负责人">
              <el-input v-model="deptCreateForm.leader" placeholder="请输入负责人（可选）" />
            </el-form-item>
            <el-form-item label="排序">
              <el-input-number v-model="deptCreateForm.sort" :min="0" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="deptCreateLoading" @click="handleCreateDept">创建部门</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 部门树 -->
        <el-card class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">部门树</span>
          </template>
          <el-skeleton :rows="5" animated :loading="deptTreeLoading">
            <template #default>
              <el-tree
                v-if="deptTree.length > 0"
                :data="deptTree"
                node-key="id"
                default-expand-all
                :props="{ label: 'name', children: 'children' }"
              >
                <template #default="{ node, data }">
                  <div class="dept-tree-node">
                    <span>{{ node.label }}</span>
                    <span class="dept-tree-actions">
                      <el-tag v-if="data.leader" size="small" class="mr-8">{{ data.leader }}</el-tag>
                      <el-tag v-if="data.status === 0" type="success" size="small" class="mr-8">正常</el-tag>
                      <el-tag v-else type="danger" size="small" class="mr-8">停用</el-tag>
                      <el-button type="primary" link size="small" @click.stop="handleEditDept(data)">编辑</el-button>
                      <el-button type="danger" link size="small" @click.stop="handleDeleteDept(data.id, data.name)">删除</el-button>
                    </span>
                  </div>
                </template>
              </el-tree>
              <el-empty v-else description="暂无部门数据" />
            </template>
          </el-skeleton>
        </el-card>

        <!-- 部门编辑 -->
        <el-card v-if="deptEditForm" class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">编辑部门</span>
          </template>
          <el-form :model="deptEditForm" label-width="80px" style="max-width: 460px">
            <el-form-item label="部门名称">
              <el-input v-model="deptEditForm.name" placeholder="请输入部门名称" />
            </el-form-item>
            <el-form-item label="上级部门">
              <el-tree-select
                v-model="deptEditForm.parentId"
                :data="deptTreeOptions"
                :props="{ label: 'name', value: 'id', children: 'children' }"
                placeholder="选择上级部门"
                clearable
                check-strictly
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="负责人">
              <el-input v-model="deptEditForm.leader" placeholder="请输入负责人" />
            </el-form-item>
            <el-form-item label="排序">
              <el-input-number v-model="deptEditForm.sort" :min="0" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="deptUpdateLoading" @click="handleUpdateDept">保存</el-button>
              <el-button @click="deptEditForm = null">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 批量导入 -->
        <el-card class="section-card" shadow="hover">
          <template #header>
            <span class="section-title">批量导入</span>
          </template>
          <el-space>
            <el-button type="primary" :loading="deptImportLoading" @click="handleImportDepts">
              导入示例部门
            </el-button>
          </el-space>
          <div v-if="deptImportResult !== null" class="result-area mt-16">
            <el-tag type="success" size="large">导入成功: {{ deptImportResult }} 条记录</el-tag>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createUser, pageUsers, deleteUser, getUserVO, pageUsersWithScope, importUsers,
  createDept, getDeptTree, updateDept, deleteDept, importDepts,
} from '@/api/entity'
import type { SysUser, SysUserVO, UserCreateRequest, UserQuery, SysDept, DeptCreateRequest, DeptVO } from '@/api/types'

const activeTab = ref('user')

// ==================== 用户管理 ====================

const userCreateLoading = ref(false)
const userCreateForm = reactive<UserCreateRequest>({
  username: '',
  phone: '',
  idCard: '',
  email: '',
  gender: 0,
  status: 0,
  deptId: undefined,
})

async function handleCreateUser() {
  if (!userCreateForm.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }
  userCreateLoading.value = true
  try {
    await createUser(userCreateForm)
    ElMessage.success('用户创建成功')
    userCreateForm.username = ''
    userCreateForm.phone = ''
    userCreateForm.idCard = ''
    userCreateForm.email = ''
    userCreateForm.gender = 0
    userCreateForm.status = 0
    userCreateForm.deptId = undefined
    fetchUsers()
  } catch (e: any) {
    ElMessage.error('创建用户失败: ' + (e.message || e))
  } finally {
    userCreateLoading.value = false
  }
}

const userListLoading = ref(false)
const users = ref<SysUser[]>([])
const userPageNum = ref(1)
const userPageSize = ref(10)
const userTotal = ref(0)

const userQueryParams = reactive<UserQuery>({
  username: '',
  phone: '',
  deptId: undefined,
  status: undefined,
})

async function fetchUsers() {
  userListLoading.value = true
  try {
    const params: UserQuery = {
      pageNum: userPageNum.value,
      pageSize: userPageSize.value,
    }
    if (userQueryParams.username) params.username = userQueryParams.username
    if (userQueryParams.phone) params.phone = userQueryParams.phone
    if (userQueryParams.status !== undefined && userQueryParams.status !== null) params.status = userQueryParams.status

    const res = await pageUsers(params)
    users.value = res.list
    userTotal.value = res.total
  } catch (e: any) {
    ElMessage.error('获取用户列表失败: ' + (e.message || e))
  } finally {
    userListLoading.value = false
  }
}

function resetUserQuery() {
  userQueryParams.username = ''
  userQueryParams.phone = ''
  userQueryParams.deptId = undefined
  userQueryParams.status = undefined
  userPageNum.value = 1
  fetchUsers()
}

function genderLabel(gender: number) {
  if (gender === 1) return '男'
  if (gender === 2) return '女'
  return '未知'
}

async function handleDeleteUser(id: number, username: string) {
  try {
    await ElMessageBox.confirm(`确认删除用户「${username}」？`, '确认删除', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    })
    await deleteUser(id)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch (e: any) {
    if (e !== 'cancel' && e?.toString() !== 'cancel') {
      ElMessage.error('删除失败: ' + (e.message || e))
    }
  }
}

const desensitizeResult = ref<SysUserVO | null>(null)

async function handleDesensitize(id: number) {
  try {
    desensitizeResult.value = await getUserVO(id)
  } catch (e: any) {
    ElMessage.error('脱敏查询失败: ' + (e.message || e))
  }
}

const scopeLoading = ref(false)
const scopeFetched = ref(false)
const scopeUsers = ref<SysUser[]>([])

async function handleScopeQuery() {
  scopeLoading.value = true
  scopeFetched.value = true
  try {
    const res = await pageUsersWithScope({ pageNum: 1, pageSize: 100 })
    scopeUsers.value = res.list
  } catch (e: any) {
    ElMessage.error('数据权限查询失败: ' + (e.message || e))
    scopeUsers.value = []
  } finally {
    scopeLoading.value = false
  }
}

const userImportLoading = ref(false)
const userImportResult = ref<number | null>(null)

async function handleImportUsers() {
  userImportLoading.value = true
  userImportResult.value = null
  try {
    const sampleUsers: SysUser[] = [
      { id: 0, username: 'demo_user1', phone: '13800000001', email: 'user1@example.com', gender: 1, status: 0 },
      { id: 0, username: 'demo_user2', phone: '13800000002', email: 'user2@example.com', gender: 2, status: 0 },
      { id: 0, username: 'demo_user3', phone: '13800000003', gender: 0, status: 1 },
    ]
    const count = await importUsers(sampleUsers)
    userImportResult.value = count
    ElMessage.success(`导入成功，共 ${count} 条`)
    fetchUsers()
  } catch (e: any) {
    ElMessage.error('导入失败: ' + (e.message || e))
  } finally {
    userImportLoading.value = false
  }
}

// ==================== 部门管理 ====================

const deptCreateLoading = ref(false)
const deptCreateForm = reactive<DeptCreateRequest>({
  name: '',
  parentId: undefined,
  leader: '',
  sort: 0,
})

async function handleCreateDept() {
  if (!deptCreateForm.name.trim()) {
    ElMessage.warning('请输入部门名称')
    return
  }
  deptCreateLoading.value = true
  try {
    await createDept(deptCreateForm)
    ElMessage.success('部门创建成功')
    deptCreateForm.name = ''
    deptCreateForm.parentId = undefined
    deptCreateForm.leader = ''
    deptCreateForm.sort = 0
    fetchDeptTree()
  } catch (e: any) {
    ElMessage.error('创建部门失败: ' + (e.message || e))
  } finally {
    deptCreateLoading.value = false
  }
}

const deptTreeLoading = ref(false)
const deptTree = ref<DeptVO[]>([])
const deptTreeOptions = ref<DeptVO[]>([])

async function fetchDeptTree() {
  deptTreeLoading.value = true
  try {
    const tree = await getDeptTree()
    deptTree.value = tree
    deptTreeOptions.value = tree
  } catch (e: any) {
    ElMessage.error('获取部门树失败: ' + (e.message || e))
  } finally {
    deptTreeLoading.value = false
  }
}

const deptEditForm = ref<SysDept | null>(null)
const deptUpdateLoading = ref(false)

function handleEditDept(data: SysDept) {
  deptEditForm.value = { ...data }
}

async function handleUpdateDept() {
  if (!deptEditForm.value) return
  deptUpdateLoading.value = true
  try {
    await updateDept(deptEditForm.value)
    ElMessage.success('更新成功')
    deptEditForm.value = null
    fetchDeptTree()
  } catch (e: any) {
    ElMessage.error('更新失败: ' + (e.message || e))
  } finally {
    deptUpdateLoading.value = false
  }
}

async function handleDeleteDept(id: number, name: string) {
  try {
    await ElMessageBox.confirm(`确认删除部门「${name}」？`, '确认删除', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    })
    await deleteDept(id)
    ElMessage.success('删除成功')
    fetchDeptTree()
  } catch (e: any) {
    if (e !== 'cancel' && e?.toString() !== 'cancel') {
      ElMessage.error('删除失败: ' + (e.message || e))
    }
  }
}

const deptImportLoading = ref(false)
const deptImportResult = ref<number | null>(null)

async function handleImportDepts() {
  deptImportLoading.value = true
  deptImportResult.value = null
  try {
    const sampleDepts: SysDept[] = [
      { id: 0, name: '研发部', parentId: 0, sort: 1, status: 0, ancestors: '0' },
      { id: 0, name: '市场部', parentId: 0, sort: 2, status: 0, ancestors: '0' },
      { id: 0, name: '前端组', parentId: 0, sort: 1, status: 0, ancestors: '0' },
    ]
    const count = await importDepts(sampleDepts)
    deptImportResult.value = count
    ElMessage.success(`导入成功，共 ${count} 条`)
    fetchDeptTree()
  } catch (e: any) {
    ElMessage.error('导入失败: ' + (e.message || e))
  } finally {
    deptImportLoading.value = false
  }
}

onMounted(() => {
  fetchUsers()
  fetchDeptTree()
})
</script>

<style scoped>
.entity-demo {
  padding: 0;
  font-family: var(--font-body);
}
.entity-tabs {
  margin-bottom: 8px;
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
.result-area {
  padding: 12px 16px;
  background: var(--accent-bg);
  border: 1px solid var(--accent-border);
  border-radius: var(--radius-md);
}
.dept-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
.dept-tree-actions {
  display: flex;
  align-items: center;
  gap: 4px;
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
