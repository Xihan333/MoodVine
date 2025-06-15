<template>
  <el-menu
    default-active="1"
    class="el-menu-vertical"
    @select="handleSelect"
  >
    <el-menu-item index="1">
      <span>活动管理</span>
    </el-menu-item>
  </el-menu>
</template>

<script>
export default {
  methods: {
    handleSelect(index) {
      if (index === '1') {
        this.$router.push('/activity');
      }
    }
  }
}
</script>

<style scoped>
.el-menu-vertical {
  height: 100vh;
  width: 200px;
}
</style>