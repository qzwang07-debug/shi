<template>
  <div class="front-layout">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'FrontLayout'
}
</script>

<style scoped>
.front-layout {
  min-height: 100vh;
  background-color: #f5f5f5;
}
</style>