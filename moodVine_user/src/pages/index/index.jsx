import { View, Text, Image } from '@tarojs/components'
import { Button } from '@taroify/core'
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useState } from 'react'

import MoodCalendar from '../../components/MoodCalendar'
import dataString from '../../components/temp'
import jar from '../../assets/jar.png'
import add_btn from '../../assets/add_btn.png'
import bg1 from '../../assets/moodpaper/blue.png'     // 开心背景
import bg2 from '../../assets/moodpaper/green.png'     // 难过背景
import bg3 from '../../assets/moodpaper/pink.png'     // 生气背景
import bg4 from '../../assets/moodpaper/purple.png' 

import 'normalize.css'
import './index.scss'
import { setScrip } from '../../store/features/scripSlice';
import { useSelector, useDispatch } from 'react-redux';

const Scrips = () => {
   // 定义固定顺序的四张背景图
  const backgroundImages = [bg1, bg2, bg3, bg4];
  
  // 心情图标的映射关系（保持不变）
  const moodIcons = {
    1: require('../../assets/moodpaper/power.png'),
    2: require('../../assets/moodpaper/peace.png'),
    3: require('../../assets/moodpaper/sad.png'),
    4: require('../../assets/moodpaper/scared.png'),
    5: require('../../assets/moodpaper/mad.png'),
  };

  const [data,setData] = useState([
    { 'mood': 1, 'sentance': '明天会更好', 'time': '5-29' },
    { 'mood': 2, 'sentance': '明天会更好明天会更好明天会更好明天会更好', 'time': '5-29' },
    { 'mood': 3, 'sentance': '明天会更好', 'time': '5-29' },
    { 'mood': 4, 'sentance': '明天会更好', 'time': '5-29' },
    { 'mood': 5, 'sentance': '明天会更好', 'time': '5-29' },
  ])

  const dispatch = useDispatch();
  const handleDetail = (item) => {
      dispatch(setScrip(item));
      Taro.navigateTo({
        url: '/pages/scripDetail/scripDetail',
      });
    };

  const itemList = data.map((item,index) => {
    const bgIndex = index % 4;
    const background = backgroundImages[bgIndex];

    // 获取对应心情图标
    const moodIcon = moodIcons[item.mood];

    return (
      <View 
        key={index} 
        className="scrip-card"
        style={{ backgroundImage: `url(${background})` }}
        onClick={()=>handleDetail(item)}
      >
        <Image className='mood' src={moodIcon} />
        <Text className="sentence">{item.sentance}</Text>
        <Text className='time'>{ item.time }</Text>
      </View>
    );
});

  return (
      <View className='scrip-list'>
        {itemList}
      </View>
    );
}

export default function Index() {

  const [loading, setLoading] = useState(false)
  const [calendarData, setCalendarData] = useState(null);
  const [moodTag,setTag] = useState('焰光的夜行者111\n桂枝冠冕')

  useLoad(() => {
    const parsedData = JSON.parse(dataString);
    setCalendarData(parsedData); // 模拟异步加载
  });

  if (!calendarData) return <View>Loading...</View>;

  return (
    <View className='index'>
      <View className='header'>
          <Image className='jar-button' src={jar} onClick={() => Taro.switchTab({ url: '/pages/score/score'})}/>
          <Text className='mood-tag'>
            { moodTag } 
          </Text>
      </View>
      <MoodCalendar className="calendar" contributions={calendarData}/>
      <Image className='add_btn' src={add_btn} onClick={() => Taro.switchTab({ url: '/pages/diaryEditor/diaryEditor'})}/>
      <View className='Note'>
        <Text className='title'>纸条集</Text>
          <Text className='more' onClick={() => Taro.navigateTo({url: '/pages/moreScrips/moreScrips'})}> {'查看更多>'}</Text>
      </View>
      <Scrips/>
    </View>
  )
}