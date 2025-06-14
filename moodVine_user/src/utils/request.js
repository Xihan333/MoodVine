import axios from 'taro-axios'
import Taro from '@tarojs/taro'
import { hideLoading, showLoading } from '@tarojs/taro'

const instance = axios.create({
  baseURL: 'http://localhost:2025',
})

// 请求拦截器
instance.interceptors.request.use(config => {

  // 如果有的话, 请求时携带token
  config.headers.Authorization = 'Bearer ' + Taro.getStorageSync('token')
  console.log('发起请求, 请求地址: ', config.url, ', 详细信息: ', config)
  showLoading({
    title: '加载中...',
    mask: true
  }).then(() => {})
  return config
}, error => {
  hideLoading()
  return Promise.reject(error)
})

// 响应拦截器
instance.interceptors.response.use(response => {
  hideLoading()
  return response
}, error => {
  console.error('请求出错, 请求地址: ', error.config.url, ', 详细信息: ', error)
  hideLoading()
  return error 
})

export default instance