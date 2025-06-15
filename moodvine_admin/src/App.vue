<template>
  <el-container>
    <div class="el-side">
      <Sidebar />
    </div>
    <div class="el-main">
      <router-view />
    </div>
  </el-container>
</template>

<script>
import Sidebar from './components/Sidebar.vue'

export default {
  components: {
    Sidebar
  }
}
</script>

<style>
.el-container {
  height: 100vh;
  width: 100vw;
}
.el-aside {
  background-color: #545c64;
  color: #fff;
  width:20%;
}
.el-main{
  width:80%;
}
</style>