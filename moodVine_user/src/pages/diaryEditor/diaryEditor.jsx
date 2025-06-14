import React, { useState, useEffect } from 'react';
import { View, Text, Input, Image, Button } from '@tarojs/components'
import { Textarea } from "@taroify/core"
import Taro from '@tarojs/taro'
import './diaryEditor.scss'
import request from '../../utils/request';
import img1 from '../../assets/paper1.jpg'

const DiaryEditor = () => {
  // const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const [images, setImages] = useState([])
  const [paperStyles, setPaperStyles] = useState([{content:'',id:''}])
  const [selectedPaper, setSelectedPaper] = useState(0)
  const imageMaxNum = 1; // 最大可上传图片数量

  useEffect(async () => {
    const res = await request.get('/user/reward/getAllRewards');
    if(res.data.code===200){
      setPaperStyles(res.data.data.rewards.filter(reward => reward.isHad));
    }
  }, []); // 空依赖数组确保仅执行一次

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
  const handlePublish = async () => {
    if (!content.trim()) {
      Taro.showToast({
        title: '请输入内容',
        icon: 'none'
      })
      return
    }

    try {
      const uploadRes = await Taro.uploadFile({
        url: 'http://localhost:2025/file/upload', // 上传接口
        filePath: images[0],
        name: 'file', // 对应后端接收文件的字段名
        formData: { // 普通对象形式传递附加参数
            timestamp: Date.now()
        },
      });

      const data = JSON.parse(uploadRes.data); // 需要手动解析
      if (data.msg) {
        console.log(`上传成功！URL: ${data.msg}`);
        const res = await request.post('/user/diary/addDiary',{
          content:content,
          pictures:[data.msg],
          notepaper:paperStyles[selectedPaper].id
        });
        // 反馈
        if(res.data.code===200){
          const res2 = await request.post('/user/addScore',{
            addScore:5
          });
          Taro.navigateBack({
            delta: 1
          });
          Taro.showToast({
            title: '发布成功',
            icon: 'success'
          })
        }
        else{
          Taro.showToast({
            title: '发布失败',
            icon: 'error'
          })
        }
      } else {
        console.log('上传失败：未获取到URL');
      }
    } catch (error) {
      Taro.hideLoading();
      console.error('上传失败:', error);
      console.log(`上传失败: ${error.errMsg || '网络错误'}`);
      Taro.showToast({ icon: 'error', title: '上传失败' });
    }
  }
  
  return (
    <View className='diary-editor-container'>
      {/* 主体部分 */}
      <View 
        className='diary-main'
        style={{ 
          backgroundImage: `url(${paperStyles[selectedPaper].content})`,
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
              <Image src={paperStyles[index].content} mode='aspectFill' className='paper-image' />
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