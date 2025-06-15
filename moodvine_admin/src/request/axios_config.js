import axios from 'axios'
import { useStore } from '@/stores/counter';

// 创建axios实例, 将来基于创建出的实例进行配置和请求
// 这样做不会污染原始axios实例
const instance = axios.create({
  baseURL: 'http://localhost:2025',
  timeout: 10000
}) 

const commonStore = useStore();

// 添加请求拦截器
instance.interceptors.request.use(function (config) {
  commonStore.updateLoading = true;
  // 在发送请求之前携带token(如果有的话)
  config.headers.Authorization = 'Bearer ' + localStorage.getItem("token")
  console.log('发起了请求, 请求信息: ', config.data)
  return config
}, function (error) {
  // 对请求错误做些什么
  commonStore.updateLoading = false;
  return Promise.reject(error)
})

// 添加响应拦截器
instance.interceptors.response.use(function (response) {
  commonStore.updateLoading = false;
  // 2xx 范围内的状态码都会触发该函数。
  // axios会把响应数据多包装一层
  return response
}, function (error) {
  commonStore.updateLoading = false;
  // 超出 2xx 范围的状态码都会触发该函数。
  // 处理响应错误
  console.error('请求出错! url: ', error.config.url, '; 详细信息: ', error)
  // 请求错误的话会返回一个AxiosError对象, 根据其中的response.status状态码判断即可
  // 省得一堆报错和try catch了
  return error
})

// 导出配置好的实例
// 此后这个instance就可当做axios的一个包含特别配置的分身来用
export default instance


/* 在组件内使用
 * import request(别名) from 本文件
 */