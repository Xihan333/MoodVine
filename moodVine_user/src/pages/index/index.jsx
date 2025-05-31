import { View, Text, Image } from '@tarojs/components'
import { Button } from '@taroify/core'
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useState } from 'react'
import MoodCalendar from '../../components/MoodCalendar'
import dataString from '../../components/temp'
import jar from '../../assets/jar.png'
import 'normalize.css'
import './index.scss'
// import '../assets/fonts/pinbo.css'

export default function Index() {

  const [loading, setLoading] = useState(false)
  const [data, setData] = useState(null);
  const [moodTag,setTag] = useState('焰光的夜行者\n桂枝冠冕')

  useLoad(() => {
    const parsedData = JSON.parse(dataString);
    setData(parsedData); // 模拟异步加载
  });

  if (!data) return <View>Loading...</View>;

  return (
    <View className='index'>
      <View className='header'>
          <Image className='jar-button' src={jar} onClick={() => Taro.switchTab({ url: '/pages/score/score'})}/>
          <Text className='mood-tag'>
            { moodTag } 
          </Text>
      </View>
      <MoodCalendar className="calendar" contributions={data}/>

    </View>
  )
}