import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('@/layouts/DefaultLayout.vue'),
      children: [
        { path: '', name: 'Home', component: () => import('@/views/LandingPage.vue') },
        { path: 'modules', name: 'Modules', component: () => import('@/views/modules/ModuleExplorer.vue') },
      ],
    },
    {
      path: '/demo',
      component: () => import('@/layouts/DemoLayout.vue'),
      children: [
        { path: 'system', name: 'DemoSystem', component: () => import('@/views/demo/SystemDemo.vue'), meta: { title: '系统管理' } },
        { path: 'product', name: 'DemoProduct', component: () => import('@/views/demo/ProductDemo.vue'), meta: { title: '商品管理' } },
        { path: 'order', name: 'DemoOrder', component: () => import('@/views/demo/OrderDemo.vue'), meta: { title: '订单管理' } },
        { path: 'ai', name: 'DemoAi', component: () => import('@/views/demo/AiDemo.vue'), meta: { title: 'AI 助手' } },
        { path: 'security', name: 'DemoSecurity', component: () => import('@/views/demo/SecurityDemo.vue'), meta: { title: '安全工具' } },
        { path: 'monitor', name: 'DemoMonitor', component: () => import('@/views/demo/MonitorDemo.vue'), meta: { title: '系统监控' } },
      ],
    },
    {
      path: '/docs',
      component: () => import('@/layouts/DocLayout.vue'),
      children: [
        { path: '', redirect: '/docs/introduction' },
        { path: 'introduction', name: 'DocIntroduction', component: () => import('@/views/docs/IntroductionDoc.vue'), meta: { title: '项目简介' } },
        { path: 'getting-started', name: 'DocGettingStarted', component: () => import('@/views/docs/GettingStartedDoc.vue'), meta: { title: '快速开始' } },
        { path: 'architecture', name: 'DocArchitecture', component: () => import('@/views/docs/ArchitectureDoc.vue'), meta: { title: '架构文档' } },
        { path: 'configuration', name: 'DocConfigRef', component: () => import('@/views/docs/ConfigRefDoc.vue'), meta: { title: '配置参考' } },
        { path: 'error-codes', name: 'DocErrorCodes', component: () => import('@/views/docs/ErrorCodeDoc.vue'), meta: { title: '错误码' } },
        { path: 'testing', name: 'DocTesting', component: () => import('@/views/docs/TestingDoc.vue'), meta: { title: '测试指南' } },
        { path: 'faq', name: 'DocFaq', component: () => import('@/views/docs/FaqDoc.vue'), meta: { title: '常见问题' } },
        { path: 'changelog', name: 'DocChangelog', component: () => import('@/views/docs/ChangelogDoc.vue'), meta: { title: '更新日志' } },
      ],
    },
    { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('@/views/NotFound.vue') },
  ],
})

router.beforeEach((to) => {
  document.title = `${to.meta.title || 'Sloth Boot'} - Sloth Boot`
})

export default router
