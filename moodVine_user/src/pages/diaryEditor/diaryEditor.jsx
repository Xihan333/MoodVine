import React, { useState } from 'react'
import { View, Text, Input, Image, Button } from '@tarojs/components'
import { Textarea } from "@taroify/core"
import Taro from '@tarojs/taro'
import './diaryEditor.scss'
import request from '../../utils/request';
import img1 from '../../assets/paper1.jpg'
import img2 from '../../assets/paper2.jpg'

const DiaryEditor = () => {
  // const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const [images, setImages] = useState([])
  const [selectedPaper, setSelectedPaper] = useState(0)
  const imageMaxNum = 50; // 最大可上传图片数量

  // 信纸数组
  // TODO 更多信纸如何处理 默认信纸可以存储在前端本地
  const paperStyles = [
    img1,img2
  ]

  // 上传图片
  const handleImageUpload = () => {
    Taro.chooseImage({
      count: imageMaxNum - images.length,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera']
    }).then(res => {
      setImages([...images, ...res.tempFilePaths])
    })
  }

  // 删除图片
  const handleDeleteImage = (index) => {
    const newImages = [...images]
    newImages.splice(index, 1)
    setImages(newImages)
  }

  // 选择信纸
  const handlePaperSelect = (index) => {
    setSelectedPaper(index)
  }

  // 发布日记
  const handlePublish = () => {
    if (!content.trim()) {
      Taro.showToast({
        title: '请输入内容',
        icon: 'none'
      })
      return
    }

    const res = request.post('/diary/addDiary',{
      content:content,
      picture:[],
      notepaper:0
    });
    // TODO 反馈
    if(res.data){
      Taro.showToast({
        title: '发布成功',
        icon: 'success'
      })
      Taro.navigateBack({
        delta: 1
      });
    }
  }
  
  return (
    <View className='diary-editor-container'>
      {/* 主体部分 */}
      <View 
        className='diary-main'
        style={{ 
          backgroundImage: `url(${paperStyles[selectedPaper]})`,
          backgroundSize: '100% 100%'
        }}
      >
        {/* 标题输入
        <View className='title-section'>
          <Input
            className='title-input'
            placeholder='请输入标题'
            value={title}
            onInput={(e) => setTitle(e.detail.value)}
          />
        </View> */}

        {/* 正文输入 */}
        <View className='content-section'>
          <Textarea
            className='content-input'
            placeholder='记录此刻的想法...'
            value={content}
            onInput={(e) => setContent(e.detail.value)}
            autoHeight
            maxlength={2000}
          />
        </View>

        {/* 图片上传 */}
        <View className='image-upload-section'>
          {images.length > 0 && (
            <View className='uploaded-images'>
            {images.map((img, index) => (
              <View className='image-item' key={index}>
                <Image src={img} mode='aspectFill' className='uploaded-image' />
                <View 
                  className='delete-btn' 
                  onClick={() => handleDeleteImage(index)}
                >
                  ×
                </View>
              </View>
            ))}
            </View>
          )}
          
          {images.length < imageMaxNum && (
            <View className='upload-btn' onClick={handleImageUpload}>
              <Text className='icon-add'>+</Text>
            </View>
          )}
        </View>
      </View>
      

      {/* 底部操作栏 */}
      <View className='action-bar'>
        {/* 信纸选择 */}
        <View className='paper-selector'>
          {paperStyles.map((_, index) => (
            <View
              key={index}
              className={`paper-option ${selectedPaper === index ? 'active' : ''}`}
              onClick={() => handlePaperSelect(index)}
            >
              <Image src={img1} mode='aspectFill' className='paper-image' />
            </View>
          ))}
        </View>

        {/* 发布按钮 */}
        <Button className='publish-btn' onClick={handlePublish}>
          发布
        </Button>
      </View>
    </View>
  )
}

export default DiaryEditor