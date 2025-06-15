import { View, Text, Image, Input } from '@tarojs/components'
import { Button } from '@taroify/core'
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useState } from 'react'
import { Close } from '@taroify/icons'

import 'normalize.css'
import './people.scss'
import request from '../../utils/request'

const People = () => {
  const [userInfo, setUserInfo] = useState({})

  const getUserInfo = async() => {
    const res = await request.get('/user/getUserInfo')
    console.log(res.data)
    if ( res.data.code == 200 ) {
      setUserInfo(res.data.data)
    }
  }

  useLoad(() => {
    getUserInfo()
  });


  return (
    <View>
      <View className='header'>
        <Image className="avatar" src={ userInfo.avatar } mode="aspectFill" />
        <View className='middle'>
          <Text className='nickname'>{ userInfo.nickname }</Text>
          <Text className='unknown'>破壳日：{userInfo.birthday}</Text>
        </View>
        <Text 
          className='seemore' 
          onClick={() => Taro.navigateTo({ url: '/pages/peopleDetail/peopleDetail'})}
        >
          {'查看更多>'}
        </Text>
      </View>

      {/* 用户信息编辑弹窗 */}
      {/* {showModal && (
        <View className='modal-overlay'>
          <View className='modal-content'>
            <Close 
              className='close-icon' 
              onClick={() => setShowModal(false)} 
            />
            
            <View className='modal-header'>
              <Text className='modal-title'>编辑个人信息</Text>
            </View>
            
            <View className='form-group'>
              <Text className='form-label'>昵称</Text>
              <Input 
                className='form-input'
                value={userInfo.nickname}
                onChange={(e) => setNickName(e.detail.value)}
                placeholder='请输入昵称'
              />
            </View>
            
            <View className='form-group'>
              <Text className='form-label'>简介</Text>
              <textarea 
                className='form-textarea'
                value={userInfo.bio}
                onChange={(e) => setBio(e.detail.value)}
                placeholder='请输入个人简介'
              />
            </View>
            
            <Button 
              className='save-button'
              color="primary" 
              block
              onClick={handleSave}
            >
              保存修改
            </Button>
          </View>
        </View>
      )} */}
    </View>
  );
};

export default People;