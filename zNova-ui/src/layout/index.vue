<template>
  <div :class="classObj" class="app-wrapper" :style="{ '--current-color': theme }">
    <div v-if="device === 'mobile' && sidebar.opened" class="drawer-bg" @click="handleClickOutside"/>
    <sidebar v-if="!sidebar.hide" class="sidebar-container" />
    <div :class="{ hasTagsView: needTagsView, sidebarHide: sidebar.hide }" class="main-container">
      <div :class="{ 'fixed-header': fixedHeader }">
        <navbar @setLayout="setLayout" />
        <tags-view v-if="needTagsView" />
      </div>
      <app-main />
      <settings ref="settingRef" />
    </div>
  </div>
</template>

<script setup>
import { useWindowSize } from '@vueuse/core'
import Sidebar from './components/Sidebar/index.vue'
import { AppMain, Navbar, Settings, TagsView } from './components'
import defaultSettings from '@/settings'

import useAppStore from '@/store/modules/app'
import useSettingsStore from '@/store/modules/settings'
import useUserStore from '@/store/modules/user'
import { getStockWarningProducts } from '@/api/merchant/stock'
import { ElNotification } from 'element-plus'

const settingsStore = useSettingsStore()
const userStore = useUserStore()
const theme = computed(() => settingsStore.theme);
const sideTheme = computed(() => settingsStore.sideTheme);
const sidebar = computed(() => useAppStore().sidebar);
const device = computed(() => useAppStore().device);
const needTagsView = computed(() => settingsStore.tagsView);
const fixedHeader = computed(() => settingsStore.fixedHeader);

const classObj = computed(() => ({
  hideSidebar: !sidebar.value.opened,
  openSidebar: sidebar.value.opened,
  withoutAnimation: sidebar.value.withoutAnimation,
  mobile: device.value === 'mobile'
}))

const { width, height } = useWindowSize();
const WIDTH = 992; // refer to Bootstrap's responsive design

watchEffect(() => {
  if (device.value === 'mobile' && sidebar.value.opened) {
    useAppStore().closeSideBar({ withoutAnimation: false })
  }
  if (width.value - 1 < WIDTH) {
    useAppStore().toggleDevice('mobile')
    useAppStore().closeSideBar({ withoutAnimation: true })
  } else {
    useAppStore().toggleDevice('desktop')
  }
})

function handleClickOutside() {
  useAppStore().closeSideBar({ withoutAnimation: false })
}

const settingRef = ref(null);
function setLayout() {
  settingRef.value.openSetting();
}

onMounted(() => {
  // 延时执行以确保数据加载
  setTimeout(() => {
    checkStockWarning()
  }, 500)
})

function checkStockWarning() {
  const isMerchant = userStore.roles && !userStore.roles.includes('admin') && userStore.deptId
  
  if (isMerchant) {
    getStockWarningProducts().then(res => {
      const products = res.data || res.rows || []
      
      if (products.length > 0) {
        // 构建表格样式的HTML字符串
        const tableRows = products.map(item => 
          `<tr style="border-bottom: 1px solid #EBEEF5;">
             <td style="padding: 8px 0; color: #606266; font-size: 13px; max-width: 180px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" title="${item.productName}">${item.productName}</td>
             <td style="padding: 8px 0 8px 15px; text-align: right; color: #F56C6C; font-weight: bold; font-size: 13px;">${item.stockQuantity}</td>
           </tr>`
        ).join('')

        const contentHtml = `
          <div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;">
            <p style="margin: 0 0 12px 0; color: #303133; font-size: 14px; line-height: 1.5;">当前店铺有 <b style="color: #E6A23C; font-size: 16px;">${products.length}</b> 款商品库存不足：</p>
            <div style="max-height: 250px; overflow-y: auto; margin-bottom: 8px; scrollbar-width: thin;">
              <table style="width: 100%; border-collapse: collapse; table-layout: fixed;">
                <thead>
                  <tr style="background-color: #F5F7FA; color: #909399; font-size: 12px;">
                    <th style="padding: 4px; text-align: left; font-weight: normal;">商品名称</th>
                    <th style="padding: 4px; text-align: right; font-weight: normal; width: 60px;">剩余</th>
                  </tr>
                </thead>
                <tbody>
                  ${tableRows}
                </tbody>
              </table>
            </div>
            <div style="padding-top: 8px; border-top: 1px dashed #DCDFE6; text-align: right;">
              <span style="color: #909399; font-size: 12px;">请前往商品管理页面补货</span>
            </div>
          </div>
        `

        ElNotification({
          title: '库存预警提醒',
          message: contentHtml,
          type: 'warning',
          dangerouslyUseHTMLString: true,
          duration: 0, // 不自动关闭
          position: 'bottom-right',
          offset: 40,
        })
      }
    }).catch(e => {
      console.error('库存预警检查失败', e)
    })
  }
}
</script>

<style lang="scss" scoped>
  @import "@/assets/styles/mixin.scss";
  @import "@/assets/styles/variables.module.scss";

.app-wrapper {
  @include clearfix;
  position: relative;
  height: 100%;
  width: 100%;

  &.mobile.openSidebar {
    position: fixed;
    top: 0;
  }
}

.drawer-bg {
  background: #000;
  opacity: 0.3;
  width: 100%;
  top: 0;
  height: 100%;
  position: absolute;
  z-index: 999;
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9;
  width: calc(100% - #{$base-sidebar-width});
  transition: width 0.28s;
}

.hideSidebar .fixed-header {
  width: calc(100% - 54px);
}

.sidebarHide .fixed-header {
  width: 100%;
}

.mobile .fixed-header {
  width: 100%;
}
</style>