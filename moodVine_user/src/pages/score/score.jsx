import Taro from '@tarojs/taro';
import { React, useState, useEffect } from 'react';
import { View, Text, Button, Image } from '@tarojs/components';
import { useSelector, useDispatch } from 'react-redux';
import { increment, decrement, incrementByAmount } from '../../store/features/testSlice';
import request from '../../utils/request';
import { Progress, ConfigProvider } from "@taroify/core"
import potImg from '../../assets/pot.png'
import './score.scss'

export default function Score() {
  // 获取当前用户的积分值
  const score = useSelector((state) => state.user.score);
  const totalScore = 100;

  // TODO 点击蜜罐触发可爱特效
  
  const handleClick = () => {
    Taro.navigateTo({
      url: '/pages/shop/shop'
    });
  };

  return (
    <ConfigProvider
      theme={{
        progressHeight:'7vw' 
      }}
    >
      <View className='main'>
        <View className='progress-group'>
          <Progress className='progress' percent={score/totalScore*100} color='F8ECDF'/>
          <Text className='progress-text'>{score}/{totalScore}</Text>
        </View>
        <Image className='pot-img' src={potImg} mode='aspectFit'></Image>
        <View className='floating-button' onClick={handleClick}>
          <Text className='button-text'>商店</Text>
        </View>
      </View>
    </ConfigProvider>
  );
}