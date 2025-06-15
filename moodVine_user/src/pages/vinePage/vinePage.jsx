import Taro from '@tarojs/taro';
import React, { useState, useEffect, useRef } from 'react';
import request from '../../utils/request';
import { View, Text, Image, Button } from '@tarojs/components';
import './vinePage.scss';
import branchImg from '../../assets/树枝.png'
import fruitImg1 from '../../assets/果实1.png'
import fruitImg2 from '../../assets/果实2.png'
import fruitImg3 from '../../assets/果实3.png'

const VinePage = () => {
  const [currentDate, setCurrentDate] = useState(new Date());
  // const [moods, setMoods] = useState([{date:'2023-03-01',mood:0},{date:'2023-03-02',mood:0},{date:'2023-03-03',mood:0},{date:'2023-03-04',mood:0},{date:'2023-03-05',mood:0},{date:'2023-03-06',mood:0},{date:'2023-03-07',mood:0},{date:'2023-03-08',mood:0},{date:'2023-03-09',mood:0},{date:'2023-03-10',mood:0},{date:'2023-03-11',mood:0},{date:'2023-03-12',mood:0}]);
  // const [moods, setMoods] = useState([]);
  let moods=[];
  const [items, setItems] = useState([]);
  const [direction, setDirection] = useState(0); // 每个月由五个树枝组成。这个变量用于控制第一个树枝的方向，便于两个月之间的衔接自然
  const horiOffsets=[[0,25,65,60,80,60,45],[0,30,0,20,0,25,50]] // 果实在藤蔓上的水平排版，分为开口向左和向右的版本

  function renderVine() {
    const newItems = [];
    let moodIndex=0;
    for (let i = 0; i < 5; i++) {
      const rotationAngle = -i*180-30-180*direction; // 每个树枝的旋转角度
      //
      const fruitOffsets=[];
      if(moodIndex<moods.length){
        for(;moodIndex<moods.length;moodIndex++){
          const day=parseInt(moods[moodIndex].date.split('-')[2],10);
          if(day>(i+1)*6){
            break;
          }
          fruitOffsets.push(day-(i*6+1)+1); //从1开始
        }
      }
      // 创建一个树枝的容器
      const branch = (
        // 这个style很复杂的条件渲染是用来判断开口是否向左
        <View className='image-container' style={ (i % 2 == 0) === !direction ? {left:'30px',marginTop: '-28px'} : {right: '30px',marginTop: '10px'} }>
          <Image className='background-image' src={branchImg} style={{transform: `rotate(${rotationAngle}deg)`}}></Image>
          {fruitOffsets.map((item) => {
            // 随机选择果实图片
            const fruitImages = [fruitImg1,fruitImg2,fruitImg3];
            const randomImage = fruitImages[Math.floor(Math.random() * fruitImages.length)];
            return (
              <Image
                key={`fruit-${i}-${item}`}
                className='overlay-image'
                src={randomImage}
                style={(i % 2 == 0) === !direction ? 
                  {
                    position: 'absolute',
                    top: `${(item-1)*18}%`,
                    left: `${horiOffsets[0][item]}%`,
                  }:
                  {
                    position: 'absolute',
                    top: `${(item-1)*18}%`,
                    left: `${horiOffsets[1][item]}%`,
                  }
                }
              />
            );
          })}
        </View>
      );
      newItems.push(branch);
    }
    if(direction) setDirection(0)
    else setDirection(1)
    setItems([...items, ...newItems]);
  }

  // 初始化心情数据
  useEffect(async () => {
    const fetchMoods = async () => {
      const year = currentDate.getFullYear();
      const month = String(currentDate.getMonth() + 1).padStart(2, '0'); // 月份从0开始，所以需要+1，并补零
      const res = await request.post('/user/getMoods',{
        date:`${year}-${month}`,
      });
      if(res.data.code===200){
        if(res.data.data.length===0){
          Taro.showToast({ title: '暂无数据',icon:'none' })
        }
        else{
          moods=res.data.data;
          renderVine();
        }
      }
    }
    fetchMoods();
  }, [currentDate]);



  // 触底加载
  const loadMore = () => {
    const newDate = new Date(currentDate);
    newDate.setMonth(newDate.getMonth() + 1);
    setCurrentDate(newDate);
  };

  return (
    <View className='main-container'>
      {items.map((item, index) => (
      <View key={index}>
        {item}
      </View>
      ))}
      <Button className='circle-button' onClick={loadMore}>
        <Text className='arrow-down'>↓</Text>
      </Button>
      {/* 悬浮按钮 */}
      <View className='floating-button' onClick={() => Taro.navigateTo({ url: '/pages/moodPage/moodPage' })}>
        <Image className='floating-button-icon' src={require('../../assets/calendar-icon.png')} mode='aspectFit' />
      </View>
    </View>
  );
};

export default VinePage;