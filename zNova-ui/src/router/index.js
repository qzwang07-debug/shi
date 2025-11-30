import { createWebHistory, createRouter } from 'vue-router'
/* Layout */
import Layout from '@/layout'

/**
 * Note: 路由配置项
 *
 * hidden: true                     // 当设置 true 的时候该路由不会再侧边栏出现
 * alwaysShow: true                 // 强制显示根路由
 * redirect: noRedirect             // 面包屑不可点击
 * name:'router-name'               // 路由名称（必须填写）
 * meta : {
    noCache: true                   // 不缓存
    title: 'title'                  // 侧边栏和面包屑名称
    icon: 'svg-name'                // 图标路径
    breadcrumb: false               // 不在面包屑显示
    activeMenu: '/system/user'      // 高亮对应侧边栏
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index.vue')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: '/index',
    children: [
      {
        path: '/index',
        component: () => import('@/views/index'),
        name: 'Index',
        meta: {
          title: '首页',
          icon: 'dashboard',
          affix: true
        }
      }
    ]
  },
  {
    path: '/computer-market', // 访问路径
    name: 'ComputerMarket',
    component: () => import('@/views/computerMarket/mallHome.vue'), // 直接指向当前组件
    hidden: true, // 不在若依侧边栏显示
    meta: {
      title: '电脑租售与硬件性能智能评估平台', // 浏览器标签页标题
      noCache: true
    }
  },
  // 修改购物车路由配置
{
  path: '/computer-market/shopping-cart',
  name: 'ShoppingCart',
component: () => import('@/views/computerMarket/cart/index.vue'), 
  hidden: true,
  meta: {
    title: '购物车',
    noCache: true
  }
},
 // 新增：租赁页面路由（rental.vue）
  {
    path: '/computer-market/rental',
    name: 'ComputerRental',
    component: () => import('@/views/computerMarket/shop/rental.vue'), // 注意匹配rental.vue的实际路径
    hidden: true,
    meta: {
      title: '电脑租赁服务',
      noCache: true
    }
  },
// 找到出售页面的路由配置，修改 component 路径
{
  path: '/computer-market/sale',
  name: 'ComputerSale',
  // 修正路径：添加 shop/ 层级，匹配实际文件位置
  component: () => import('@/views/computerMarket/shop/sale.vue'), 
  hidden: true,
  meta: {
    title: '电脑出售服务',
    noCache: true
  }
},
 {
  path: '/computer-market/build',
  name: 'ComputerBuild',
  // 修正路径：添加 shop/ 层级，匹配实际文件位置
  component: () => import('@/views/computerMarket/shop/build.vue'),
  hidden: true,
  meta: {
    title: '电脑装机服务',
    noCache: true
  }
},
{
  path: '/user',
  component: Layout,
  hidden: true,
  redirect: 'noredirect',
  children: [
    {
      path: 'profile',
      component: () => import('@/views/system/user/profile/index'),
      name: 'Profile',
      meta: { title: '个人中心', icon: 'user' }
    }
  ]
},
// C端portal路由配置
{
  path: '/portal/login',
  component: () => import('@/views/portal/login.vue'),
  name: 'PortalLogin',
  hidden: true,
  meta: {
    title: 'C端登录',
    noAuth: true,
    noCache: true
  }
},
{
  path: '/portal/register',
  component: () => import('@/views/portal/register.vue'),
  name: 'PortalRegister',
  hidden: true,
  meta: {
    title: 'C端注册',
    noAuth: true,
    noCache: true
  }
},
{
  path: '/portal/user/profile',
  component: () => import('@/views/portal/user/profile.vue'),
  name: 'PortalProfile',
  hidden: true,
  meta: {
    title: '个人中心',
    noCache: true
  }
},
// 地址管理页面路由
{
  path: '/portal/user/address',
  name: 'UserAddress',
  component: () => import('@/views/portal/user/address.vue'),
  hidden: true,
  meta: {
    title: '地址管理',
    noCache: true
  }
},
{
  path: '/computer-market/product-detail/:id',
  name: 'ProductDetail',
  component: () => import('@/views/computerMarket/shop/detail.vue'),
  hidden: true,
  meta: {
    title: '商品详情',
    noCache: true
  }
},
// 订单结算页面路由
{
  path: '/computer-market/checkout',
  name: 'Checkout',
  component: () => import('@/views/portal/trade/checkout.vue'),
  hidden: true,
  meta: {
    title: '订单结算',
    noCache: true
  }
},
// 支付页面路由
{
  path: '/portal/trade/pay',
  name: 'Pay',
  component: () => import('@/views/portal/trade/pay.vue'),
  hidden: true,
  meta: {
    title: '订单支付',
    noCache: true
  }
},
// 订单列表页面路由
{
  path: '/portal/user/order',
  name: 'UserOrder',
  component: () => import('@/views/portal/user/order.vue'),
  hidden: true,
  meta: {
    title: '我的订单',
    noCache: true
  }
},
  // 404页面必须放在最后
  {
    path: "/:pathMatch(.*)*",
    component: () => import('@/views/error/404'),
    hidden: true
  }
]

// 动态路由（基于权限加载）
export const dynamicRoutes = [
  {
    path: '/system/user-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:user:edit'],
    children: [
      {
        path: 'role/:userId(\\d+)',
        component: () => import('@/views/system/user/authRole'),
        name: 'AuthRole',
        meta: { title: '分配角色', activeMenu: '/system/user' }
      }
    ]
  },
  {
    path: '/system/role-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:role:edit'],
    children: [
      {
        path: 'user/:roleId(\\d+)',
        component: () => import('@/views/system/role/authUser'),
        name: 'AuthUser',
        meta: { title: '分配用户', activeMenu: '/system/role' }
      }
    ]
  },
  {
    path: '/system/dict-data',
    component: Layout,
    hidden: true,
    permissions: ['system:dict:list'],
    children: [
      {
        path: 'index/:dictId(\\d+)',
        component: () => import('@/views/system/dict/data'),
        name: 'Data',
        meta: { title: '字典数据', activeMenu: '/system/dict' }
      }
    ]
  },
  {
    path: '/monitor/job-log',
    component: Layout,
    hidden: true,
    permissions: ['monitor:job:list'],
    children: [
      {
        path: 'index/:jobId(\\d+)',
        component: () => import('@/views/monitor/job/log'),
        name: 'JobLog',
        meta: { title: '调度日志', activeMenu: '/monitor/job' }
      }
    ]
  },
  {
    path: '/tool/gen-edit',
    component: Layout,
    hidden: true,
    permissions: ['tool:gen:edit'],
    children: [
      {
        path: 'index/:tableId(\\d+)',
        component: () => import('@/views/tool/gen/editTable'),
        name: 'GenEdit',
        meta: { title: '修改生成配置', activeMenu: '/tool/gen' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  },
});

export default router;