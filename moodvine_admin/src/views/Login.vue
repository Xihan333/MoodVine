<!-- 登录页面 -->
<template>
  <div class="background">
      <div class="loginCard">
          <img class="poster" src="../assets/logo.jpg" alt="">
          <div class="account">
            <h1>MoodVine 后台管理</h1>
            <div class="loginBut">
              <el-form @keydown.enter="login">
                <el-input v-model="username" placeholder="请输入用户名" class="inp" id="username" clearable />
                <el-input v-model="password" type="password" placeholder="请输入密码" class="inp" id="password" show-password/>
                <el-button class="inp" style="background-color: #646EF7;border: 0;" type="primary" @click="login">登录</el-button>
              </el-form>
            </div>
          </div>
      </div>
  </div>
</template>

<script setup>
    import { ref } from 'vue'
    import { useRouter } from 'vue-router'
    import { useStore } from '@/stores/counter'
    import { ElMessage } from 'element-plus'
    import request from '../request/axios_config.js'
    
    const router = useRouter()
    // const route = useRoute()
    const store = useStore()
    const username = ref('')
    const password = ref('')
    const userInfo = ref({
      'userId': 0,
      'roles': "",
      'userName': ""
    })

    const login = async () => {
      if (!username.value || !password.value){
        ElMessage({
          message: '输入不能有空!',
          type: 'warning'
        })
        return
      }
      const res = await request.post('/user/adminLogin', {
        username: username.value,
        password: password.value
      })
      console.log("res = ", res)
      if (res.status === 200){
        console.log(res.data.data.token)
        store.setUserInfo(res.data.data)
        store.setToken(res.data.token)
        ElMessage({
          message: '登录成功',
          type: 'success'
        })
        console.log(res)
        await router.push('/activity')
      }else{
        ElMessage({
          message: '用户名或密码有误, 登录失败!',
          type: 'error'
        })
      }
    }
</script>

<style scoped lang="less">
.background{
  /* 那个19200 * 12240的两个多MB的登录壁纸有点大了, 改成渐变色背景吧 (( */
  background-image: linear-gradient(to right, #8cb6ff, #f9dc9e, #f6e9a8, #abbaf8, #f5e9ac, #aecff5);
  height: 100%;
  width: 100%;
  background-size: cover;
  position: absolute;
  top: 0;
  left: 0;
  opacity: 0.7;
}
.loginCard{
  width: 900px;
  height: 550px;
  background-color: white;
  border-radius: 30px;
  // margin: auto auto;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%,-50%);
  float: left;
}
.loginCard .poster{
  height: 100%;
  border-radius: 30px 0 0 30px;
  position: absolute;
}
.loginCard .account{
  position: relative;
  width: 55%;
  height: 100%;
  float: right;
  margin: auto auto;
  text-align: center;
  // margin-bottom: 20px;
}
.account h1{
  font-size: 36px;
  font-weight: 600;
  margin-top: 20%;
  color: black;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}
.account .inp{
  height: 40px;
  width: 100%;
  margin-top: 28px;
}
.loginBut{
  width: 50%;
  margin: 30px auto;
  .el-input{
    font-size: 14px;
  }
  .el-button{
    font-size: 16px;
  }
}
</style>
