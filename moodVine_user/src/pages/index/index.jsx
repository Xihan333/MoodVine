import { View, Text, Image } from '@tarojs/components'
import { Button, Empty } from '@taroify/core'
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useState } from 'react'
import { setUserInfo,addScore } from '../../store/features/userSlice';

import MoodCalendar from '../../components/MoodCalendar'
import dataString from '../../components/temp'
import jar from '../../assets/jar.png'
import add_btn from '../../assets/add_btn.png'
import bg1 from '../../assets/moodpaper/blue.png'     // 开心背景
import bg2 from '../../assets/moodpaper/green.png'     // 难过背景
import bg3 from '../../assets/moodpaper/pink.png'     // 生气背景
import bg4 from '../../assets/moodpaper/purple.png' 
import cartoon from '../../assets/cute.png'
import diary from '../../assets/diary-icon.png'

import 'normalize.css'
import './index.scss'
import request from '../../utils/request'

import { setScrip } from '../../store/features/scripSlice';
import { useSelector, useDispatch } from 'react-redux';

const Scrips = ( { data } ) => {

   // 安全渲染处理
  if (!Array.isArray(data) || data.length === 0) {
    return (
      <View className="empty-scrips">
        <Empty className="empty-result">
          <Empty.Image src="search" />
          <Empty.Description>暂无纸条，快去找小漫聊天吧~</Empty.Description>
        </Empty>
      </View>
    );
  }

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
        <Text className="sentence">{item.sentence}</Text>
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
const getWxCode = async () => {
  const { code } = await Taro.login();
  return code; 
};

export default function Index() {

  const [loading, setLoading] = useState(false)
  const [calendarData, setCalendarData] = useState(null);
  const [moodTag,setTag] = useState('')
  const [scripData,setScripData] = useState(null)

  const handleLogin = async () => {
    // try {
      const code = await getWxCode();
      console.log(code)
      const res = await request.post('/user/wxlogin', { code });
      console.log(res.data)

      if (res.data.code === 200) {
        const { token, user } = res.data.data; // 假设返回 token 和用户数据
        Taro.setStorageSync('score', user.score); // 存储用户信息
        Taro.setStorageSync('token', token); // 存储 token
        Taro.setStorageSync('userInfo', user); // 存储用户信息        
        Taro.showToast({ 
          title: '登录成功', 
          icon: 'success',
        });
        fetchAllData()
      } else {
        Taro.showToast({ title: '登录失败', icon: 'none' });
      }
    // } catch (error) {
    //   Taro.showToast({ title: '登录失败', icon: 'none' });
    // }
  };

  const fetchAllData = async () => {
    try {
      setLoading(true);
      
      // 并行发起所有请求
      const [calendarRes, moodRes, scripRes] = await Promise.all([
        request.get('/user/getMoodCalendar'),
        request.get('/user/tab/getTabs'),
        request.get('/user/scrip/getIndexScrip')
      ]);
      
      // 处理心情日历数据
      if (calendarRes.data?.code === 200) {
        setCalendarData(calendarRes.data.data || []);
      } else {
        setCalendarData([]);
        console.warn('心情日历数据异常:', calendarRes);
      }
      
      // 处理心情标签
      let tag = '情感细腻的观察者\n文思泉涌的麻花';
      // let tag = '';
      if (moodRes.data?.code === 200 && Array.isArray(moodRes.data.data)) {
        tag = moodRes.data.data
          .filter(item => item.content)
          .map(item => item.content)
          .join('\n');
      }
      setTag(tag);
      
      // 处理纸条数据
      if (scripRes.data?.code === 200) {
        setScripData(Array.isArray(scripRes.data.data) 
          ? scripRes.data.data 
          : []);
      } else {
        setScripData([]);
        console.warn('纸条数据异常:', scripRes);
      }
      
    } catch (err) {
      console.error('数据加载失败:', err);
      setCalendarData([]);
      setScripData([]);
    } finally {
      setLoading(false);
    }
  };

  useLoad(() => {
    fetchAllData();
    console.log(calendarData)
    console.log(scripData)
  });

  // if (!Taro.getStorageSync('token') ) return (
  if (!calendarData || !scripData || !Taro.getStorageSync('token')) return (
    <View>
      Loading...
      <Button onClick={handleLogin}>微信一键登录</Button>
    </View>
  );

  return (
    <View className='index'>
      <View className='header'>
          <View className='left-container'>
            <Image className='jar-button' src={jar} onClick={() => Taro.navigateTo({ url: '/pages/score/score'})}/>
            <Image className='diary-list' src={diary} onClick={() => Taro.navigateTo({ url: '/pages/diaryList/diaryList'})}/> 
          </View>
          <Text className='mood-tag'>
            { moodTag } 
          </Text>
      </View>
      <MoodCalendar className="calendar" contributions={calendarData}/>
      <Image className='add_btn' src={add_btn} onClick={() => Taro.navigateTo({ url: '/pages/diaryEditor/diaryEditor'})}/>
      <View className='Note'>
        <Text className='title'>纸条集</Text>
          <Text className='more' onClick={() => Taro.navigateTo({url: '/pages/moreScrips/moreScrips'})}> {'查看更多>'}</Text>
      </View>
      { scripData && (<Scrips data={scripData} />)}
      <Image className='cute' src={cartoon} onClick={() => Taro.navigateTo({url: '/pages/chatAI/chatAI'})} />
    </View>
  )
}