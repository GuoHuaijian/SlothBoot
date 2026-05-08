<template>
  <div class="system-demo">
    <!-- 登录面板 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <span class="section-title">登录面板</span>
      </template>
      <el-form :inline="true" :model="loginForm" label-width="80px">
        <el-form-item label="用户ID">
          <el-input-number v-model="loginForm.userId" :min="1" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loginLoading" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div v-if="loginResult" class="result-area">
        <el-tag type="success" size="large">Token: {{ loginResult.token }}</el-tag>
      </div>
    </el-card>

    <!-- 当前用户 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <span class="section-title">当前用户</span>
      </template>
      <el-button type="primary" :loading="userLoading" @click="fetchCurrentUser">
        获取信息
      </el-button>
      <el-descriptions v-if="currentUser" :column="2" border class="mt-16">
        <el-descriptions-item label="ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="身份证">{{ currentUser.idCard }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag v-for="role in currentUser.roles" :key="role" class="mr-8" type="primary">
            {{ role }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <span class="section-title">用户列表</span>
      </template>
      <el-button type="primary" :loading="usersLoading" @click="fetchUsers" class="mb-16">
        加载用户列表
      </el-button>
      <el-table :data="users" border stripe v-loading="usersLoading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="idCard" label="身份证" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column label="角色">
          <template #default="{ row }">
            <el-tag v-for="role in row.roles" :key="role" size="small" class="mr-8">
              {{ role }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 权限测试 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <span class="section-title">权限测试</span>
      </template>
      <el-space wrap>
        <el-button type="warning" :loading="permLoading" @click="testPermission">
          测试 user:view 权限
        </el-button>
        <el-button type="danger" :loading="logoutLoading" @click="handleLogout">
          登出
        </el-button>
      </el-space>
      <el-alert
        v-if="permResult"
        :title="permResult"
        :type="permResultType"
        show-icon
        :closable="false"
        class="mt-16"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { systemApi } from '@/api/system'
import { useAuthStore } from '@/stores/auth'
import type { UserVO, LoginResponse } from '@/api/types'

const authStore = useAuthStore()

// 登录面板
const loginForm = reactive({ userId: 1, username: 'admin' })
const loginLoading = ref(false)
const loginResult = ref<LoginResponse | null>(null)

async function handleLogin() {
  loginLoading.value = true
  try {
    const res = await systemApi.login(loginForm.userId, loginForm.username)
    loginResult.value = res
    authStore.setAuth({ token: res.token, userId: res.userId, username: res.username })
    ElMessage.success('登录成功')
  } catch (e: any) {
    ElMessage.error('登录失败: ' + (e.message || e))
  } finally {
    loginLoading.value = false
  }
}

// 当前用户
const userLoading = ref(false)
const currentUser = ref<UserVO | null>(null)

async function fetchCurrentUser() {
  userLoading.value = true
  try {
    currentUser.value = await systemApi.getCurrentUser()
  } catch (e: any) {
    ElMessage.error('获取用户信息失败: ' + (e.message || e))
  } finally {
    userLoading.value = false
  }
}

// 用户列表
const usersLoading = ref(false)
const users = ref<UserVO[]>([])

async function fetchUsers() {
  usersLoading.value = true
  try {
    users.value = await systemApi.getUsers()
  } catch (e: any) {
    ElMessage.error('获取用户列表失败: ' + (e.message || e))
  } finally {
    usersLoading.value = false
  }
}

// 权限测试
const permLoading = ref(false)
const logoutLoading = ref(false)
const permResult = ref('')
const permResultType = ref<'success' | 'warning' | 'info' | 'error'>('info')

async function testPermission() {
  permLoading.value = true
  try {
    const res = await systemApi.getPermissions()
    permResult.value = res
    permResultType.value = 'success'
  } catch (e: any) {
    permResult.value = '权限测试失败: ' + (e.message || e)
    permResultType.value = 'error'
  } finally {
    permLoading.value = false
  }
}

async function handleLogout() {
  logoutLoading.value = true
  try {
    await systemApi.logout()
    authStore.clearAuth()
    permResult.value = '已登出'
    permResultType.value = 'warning'
    loginResult.value = null
    currentUser.value = null
    users.value = []
    ElMessage.success('登出成功')
  } catch (e: any) {
    ElMessage.error('登出失败: ' + (e.message || e))
  } finally {
    logoutLoading.value = false
  }
}
</script>

<style scoped>
.system-demo {
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
.result-area {
  margin-top: 12px;
  padding: 12px 16px;
  background: var(--accent-bg);
  border: 1px solid var(--accent-border);
  border-radius: var(--radius-md);
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
</style>
