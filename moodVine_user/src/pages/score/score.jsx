import Taro from '@tarojs/taro';
import { React } from 'react';
import { View, Text, Image } from '@tarojs/components';
import { useSelector,useDispatch } from 'react-redux';
import { setScore } from '../../store/features/userSlice';
import { Progress, ConfigProvider } from "@taroify/core"
import potImg from '../../assets/pot.png'
import './score.scss'

export default function Score() {
  dispatch(setScore(Taro.getStorageSync('score')))
  const dispatch = useDispatch();
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
          <Progress className='progress' percent={score*100/totalScore} color='F8ECDF'/>
          <Text className='progress-text'>{score}/{totalScore}</Text>
        </View>
        <Image className='pot-img' src={potImg} mode='aspectFit'></Image>
        <Text className='jiyu'>你的感受化作蜜，永远储存在蜜罐里</Text>
        <View className='floating-button' onClick={handleClick}>
          <Text className='button-text'>商店</Text>
        </View>
      </View>
    </ConfigProvider>
  );
}