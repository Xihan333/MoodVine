import { View, Text, Image, Input } from '@tarojs/components'
import { Button, Field, Radio, Cell } from '@taroify/core'
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useState } from 'react'
import { Popup, DatetimePicker } from '@taroify/core'

import 'normalize.css'
import './peopleDetail.scss'
import request from '../../utils/request'

const peopleDetail = () => {
  const [userInfo, setUserInfo] = useState({})
  const [avatar, setAvatar] = useState("")
  const [nickname, setNickName] = useState("")
  const [gender, setGender] = useState("0") // 默认设置为女性
  const [birthday, setBirthday] = useState("")
  const [showDatePicker, setShowDatePicker] = useState(false)

  const getUserInfo = async() => {
    const res = await request.get('/user/getUserInfo')
    console.log(res.data)
    if ( res.data.code == 200 ) {
      setUserInfo(res.data.data)
      setAvatar(res.data.data.avatar)
      setNickName(res.data.data.nickname)
      setGender(String(res.data.data.gender) || "0") // 确保转为字符串类型
      setBirthday(res.data.data.birthday || "")
    }
  }

  useLoad(() => {
    getUserInfo()
  })

  const uploadPic = async() => {
    try {
      // 1. 选择图片
      const res = await Taro.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera']
      })

      if (!res.tempFilePaths.length) return

      // 2. 显示加载状态
      Taro.showLoading({ title: '上传中...', mask: true })

      const uploadRes = await Taro.uploadFile({
        url: 'http://localhost:2025/file/upload',
        filePath: res.tempFilePaths[0],
        name: 'file',
        formData: { 
          timestamp: Date.now()
        },
      })

      Taro.hideLoading()
      const data = JSON.parse(uploadRes.data)

      if (data.msg) {
        setAvatar(data.msg)
        // 同时更新用户信息中的头像
        setAvatar(data.msg)
      } else {
        Taro.showToast({ icon: 'error', title: '图像上传失败' })
        return
      }

    } catch (error) {
      Taro.hideLoading()
      console.error('上传失败:', error)
      Taro.showToast({ icon: 'error', title: '上传失败' })
    }
  }

  const handleDateConfirm = (newDate) => {
    // 格式化为 YYYY-MM-DD
    const formattedDate = formatDate(newDate)
    setBirthday(formattedDate)
    setShowDatePicker(false)
  }

  function formatDate(date) {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0'); // 月份+1（0-indexed）并补零
    const day = String(d.getDate()).padStart(2, '0');        // 日期补零
    return `${year}-${month}-${day}`;
  }

  const handleSave = async () => {
    try {
      // 组装保存的数据
      const updateData = {
        avatar,
        nickname,
        gender: gender, 
        birthday
      }
      
      const res = await request.post('/user/updateUserInfo', updateData)
      
      if (res.data.code === 200) {
        Taro.showToast({
          title: '保存成功',
          icon: 'success'
        })
        // 刷新用户信息
        Taro.reLaunch({ url: '/pages/people/people'})
      } else {
        Taro.showToast({
          title: res.data.msg || '保存失败',
          icon: 'none'
        })
      }
    } catch (error) {
      Taro.showToast({
        title: '网络错误，请重试',
        icon: 'none'
      })
    }
  }

  return (
    <View className="people-detail-container">
      <Cell.Group inset>
        {/* 头像部分 */}
        <Cell
          title="头像"
          align="center"
          rightIcon={
            <Image
              className="avatar-image"
              src={avatar}
              onClick={uploadPic}
            />
          }
        />
        
        {/* 昵称部分 */}
        <Field 
          label="昵称" 
          align="center"
        >
          <Input
            placeholder="请输入昵称"
            value={nickname}
            onInput={(e) => setNickName(e.detail.value)}
          />
        </Field>
        
        {/* 性别部分 */}
        <Field label="性别">
          <Radio.Group 
            value={gender} 
            onChange={(value) => setGender(value)}
            direction="horizontal"
          >
            <Radio name="0">女</Radio>
            <Radio name="1">男</Radio>
          </Radio.Group>
        </Field>
        
        {/* 生日部分 */}
        <Field 
          label="生日"
          align="center"
          clickable
          onClick={() => setShowDatePicker(true)}
        >
          <Input
            placeholder="请选择生日"
            value={birthday}
            disabled
          />
        </Field>
      </Cell.Group>
      
      <Button 
        className='save-button'
        color="primary" 
        block
        onClick={handleSave}
      >
        保存修改
      </Button>
      
      {/* 日期选择器弹出层 */}
      <Popup
        open={showDatePicker}
        placement="bottom"
        onClose={() => setShowDatePicker(false)}
      >
        <DatetimePicker
          type="date"
          value={birthday ? new Date(birthday) : new Date()}
          min={new Date(1900, 0, 1)}
          max={new Date()}
          onConfirm={handleDateConfirm}
          onCancel={() => setShowDatePicker(false)}
        >
          <DatetimePicker.Toolbar>
            <DatetimePicker.Button onClick={() => setShowDatePicker(false)}>
              取消
            </DatetimePicker.Button>
            <DatetimePicker.Title>选择生日</DatetimePicker.Title>
            <DatetimePicker.Button onClick={handleDateConfirm}>
              确定
            </DatetimePicker.Button>
          </DatetimePicker.Toolbar>
        </DatetimePicker>
      </Popup>
    </View>
  )
}

export default peopleDetail