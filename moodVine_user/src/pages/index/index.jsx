import { View, Text, Image } from '@tarojs/components'
import { Button } from '@taroify/core'
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useState } from 'react'
import { uploadFile } from '../../utils/r2Utils' // 确保路径正确
import 'normalize.css'
import './index.scss'

export default function Index() {
  const [imageUrl, setImageUrl] = useState('')
  const [loading, setLoading] = useState(false)

  useLoad(() => {
    console.log('Page loaded.')
  })

  const handleUpload = async () => {
    try {
      setLoading(true)
      // 选择图片
      const res = await Taro.chooseImage({
        count: 1,
        sourceType: ['album', 'camera'],
        sizeType: ['compressed']
      })

      if (res.tempFilePaths.length > 0) {
        Taro.showLoading({ title: '上传中...' })
        const url = await uploadFile(res.tempFilePaths[0])
        setImageUrl(url)
        Taro.showToast({ title: '上传成功', icon: 'success' })
      }
    } catch (error) {
      console.error('Upload error:', error)
      Taro.showToast({ 
        title: error instanceof Error ? error.message : '上传失败',
        icon: 'none'
      })
    } finally {
      setLoading(false)
      Taro.hideLoading()
    }
  }

  return (
    <View className='index'>
      <Text className='title'>R2文件上传测试</Text>
      
      <Button 
        color="primary" 
        block
        onClick={handleUpload}
        disabled={loading}
      >
        {loading ? '上传中...' : '选择并上传图片'}
      </Button>

      {imageUrl && (
        <View className="preview-area">
          <Text className="subtitle">上传结果：</Text>
          <Image
            src={imageUrl}
            mode="aspectFit"
            className="preview-image"
          />
          <View className="url-container">
            <Text className="url-label">访问地址：</Text>
            <Text 
              className="url-text"
              onClick={() => {
                Taro.setClipboardData({
                  data: imageUrl,
                  success: () => Taro.showToast({ title: '链接已复制' })
                })
              }}
            >
              {imageUrl}
            </Text>
          </View>
        </View>
      )}

      <Text className="footer" onClick={() => Taro.navigateTo({ url: '/pages/shop/shop' })}>
        跳转到商店页面
      </Text>
    </View>
  )
}