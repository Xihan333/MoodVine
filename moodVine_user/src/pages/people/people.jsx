import { View, Text, Image } from '@tarojs/components'
import { Button } from '@taroify/core'
import Taro from '@tarojs/taro'
import { useState } from 'react'
import { Close } from '@taroify/icons'

import 'normalize.css'
import './people.scss'

const People = () => {
  const [showModal, setShowModal] = useState(false);
  const [userInfo, setUserInfo] = useState({
    avatar: require('../../assets/reward0.jpg'),
    nickname: '笨蛋鱼鱼',
    bio: '个人简介...'
  });

  const handleSave = () => {
    // 这里可以添加保存逻辑
    Taro.showToast({
      title: '保存成功',
      icon: 'success'
    });
    setShowModal(false);
  };

  const handleChange = (field, value) => {
    setUserInfo(prev => ({...prev, [field]: value}));
  };

  return (
    <View>
      <View className='header'>
        <Image className="avatar" src={ userInfo.avatar } mode="aspectFill" />
        <View className='middle'>
          <Text className='nickname'>{ userInfo.nickname }</Text>
          <Text className='unknown'>{userInfo.bio}</Text>
        </View>
        <Text 
          className='seemore' 
          onClick={() => setShowModal(true)}
        >
          {'查看更多>'}
        </Text>
      </View>

      {/* 用户信息编辑弹窗 */}
      {showModal && (
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
              <input 
                className='form-input'
                value={userInfo.nickname}
                onChange={e => handleChange('nickname', e.target.value)}
                placeholder='请输入昵称'
              />
            </View>
            
            <View className='form-group'>
              <Text className='form-label'>简介</Text>
              <textarea 
                className='form-textarea'
                value={userInfo.bio}
                onChange={e => handleChange('bio', e.target.value)}
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
      )}
    </View>
  );
};

export default People;