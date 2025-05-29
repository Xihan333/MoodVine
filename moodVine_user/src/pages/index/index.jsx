import { View, Text, Image } from '@tarojs/components'
import { Button } from '@taroify/core'
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useState } from 'react'
import MoodCalendar from '../../components/MoodCalendar'
import dataString from '../../components/temp'
import 'normalize.css'
import './index.scss'

export default function Index() {

  const [loading, setLoading] = useState(false)
  const [data, setData] = useState(null);

  useLoad(() => {
    const parsedData = JSON.parse(dataString);
    setData(parsedData); // 模拟异步加载
  });

  if (!data) return <View>Loading...</View>;

  return (
    <View className='index'>
      <MoodCalendar className="calendar" contributions={data}/>

      <Text className="footer" onClick={() => Taro.navigateTo({ url: '/pages/shop/shop' })}>
        跳转到商店页面
      </Text>
    </View>
  )
}