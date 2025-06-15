import { View, Text, Image, Input } from '@tarojs/components'
import { Button } from '@taroify/core'
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useState } from 'react'
import { Close } from '@taroify/icons'

import bgweek from '../../assets/week-btn.png'
import bgactivity from '../../assets/activity-btn.png'

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
      Taro.setStorageSync('userInfo',res.data.data)
    }
  }

  useLoad(() => {
    getUserInfo()
  });

  const handleLog = () => {
    try {
      // 1. 清除 localStorage 中的 token
      Taro.removeStorageSync('token');
      
      // 2. 可选：清除其他相关存储数据
      Taro.removeStorageSync('userInfo');
      
      // 3. 跳转到首页
      Taro.reLaunch({
        url: '/pages/index/index',
        success: () => {
          console.log('跳转到首页成功');
        },
        fail: (err) => {
          console.error('跳转失败:', err);
          Taro.showToast({
            title: '跳转失败，请手动返回首页',
            icon: 'none'
          });
        }
      });
      
      // 4. 可选：显示退出提示
      Taro.showToast({
        title: '已退出登录',
        icon: 'success',
        duration: 1000
      });
      
    } catch (error) {
      console.error('退出登录失败:', error);
      Taro.showToast({
        title: '退出登录失败',
        icon: 'none'
      });
    }
  };


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
      <View classname='btn'>
        <View 
          className="week-analysis"
          style={{ backgroundImage: `url(${bgweek})` }}
        >
            <Text className="week-content">周报</Text>
        </View>
        <View 
          className='activity'
          style={{ backgroundImage: `url(${bgactivity})` }}
          >
            <Text className="activity-content" onClick={() => { Taro.switchTab({ url: '/pages/activityList/activityList' })}}>活动</Text>
        </View>
      </View>

      <Button 
        className='log-button'
        color="primary" 
        block
        onClick={handleLog}
      >
        退出登录
      </Button>
      
    </View>
  );
};

export default People;