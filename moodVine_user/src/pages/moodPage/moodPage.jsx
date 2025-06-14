import Taro from '@tarojs/taro';
import React, { useState, useEffect, useRef } from 'react';
import { View, Text, Image, Button } from '@tarojs/components';
import request from '../../utils/request';
import { useDidShow } from '@tarojs/taro';
import './moodPage.scss'; // 引入样式文件
import img1 from '../../assets/power.png'

const MoodPage = () => {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [moods, setMoods] = useState([{date:'2023-03-23',mood:0}]);
  const [position, setPosition] = useState(0)
  const [touchStartX, setTouchStartX] = useState(0)
  const [touchEndX, setTouchEndX] = useState(0)
  const containerRef = useRef(null)
  const moodImages=[img1] //TODO 拿到心情编号后，完善
 
  // 处理滑动事件
  const handleTouchStart = (e) => {
    setTouchStartX(e.touches[0].clientX)
  }
  const handleTouchMove = (e) => {
    setTouchEndX(e.touches[0].clientX)
    const dx = touchEndX - touchStartX
    setPosition(dx)
  }
  const handleTouchEnd = () => {
    const dx = touchEndX - touchStartX
    const containerWidth = containerRef.current?.offsetWidth || 0
    // 根据滑动距离决定是否切换月份
    if (Math.abs(dx) > containerWidth * 0.2) {
      if (dx > 0) {
        changeMonth(-1);
      } else {
        changeMonth(1);
      }
    }
    setPosition(0)
  }

  // 初始化心情数据
  useEffect(() => {
    // const year = currentDate.getFullYear();
    // const month = String(currentDate.getMonth() + 1).padStart(2, '0'); // 月份从0开始，所以需要+1，并补零
    // const res = request.post('/getMoods',{
    //   date:`${year}-${month}`,
    // });
    // // TODO 反馈
    // if(res.data){
    //   setMoods(res.data.data);
    // }
  }, [currentDate]);

  // 切换月份
  const changeMonth = (offset) => {
    const newDate = new Date(currentDate);
    newDate.setMonth(newDate.getMonth() + offset);
    setCurrentDate(newDate);
  };

  // 渲染日历
  const renderCalendar = () => {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth() + 1;
    const daysInMonth = new Date(year, month, 0).getDate();
    const firstDay = new Date(year, month - 1, 1).getDay(); // 获取当月第一天是星期几

    const calendarDays = [];
    for (let i = 0; i < firstDay; i++) {
      calendarDays.push(<View key={`empty-${i}`} />); // 空白格子
    }

    function renderMoodImage(i, j) {
      if (j < moods.length && String(i).padStart(2, '0') === moods[j].date.substring(8)) {
        const currentMood = moods[j].mood;
        j++; // 增加 j 的值
        return <Image src={moodImages[currentMood]} className="mood-image" />;
      }
      return null;
    }

    let j=0;
    for (let i = 1; i <= daysInMonth; i++) {
      calendarDays.push(
        <View key={i} className="day-container">
          <Text>{i}</Text>
          {renderMoodImage(i,j)}
        </View>
      );
    }

    return (
      <View className="calendar-grid"
      style={{ transform: `translateX(${position}px)` }}>
        {['日', '一', '二', '三', '四', '五', '六'].map((day) => (
          <Text key={day} className="weekday-header">
            {day}
          </Text>
        ))}
        {calendarDays}
      </View>
    );
  };

  return (
    <View className="calendar-page" 
      onTouchStart={handleTouchStart}
      onTouchMove={handleTouchMove}
      onTouchEnd={handleTouchEnd}
      ref={containerRef}
    >
      <View className="month-header">
        <Text>{`${currentDate.getFullYear()}年${currentDate.getMonth() + 1}月`}</Text>
      </View>
      {renderCalendar()}
      {/* 悬浮按钮 */}
      <View className='floating-button' onClick={() => wx.switchTab({ url: '/pages/vinePage/vinePage' })}>
        <Image className='floating-button-icon' src={require('../../assets/leaf-icon.png')} mode='aspectFit' />
      </View>
    </View>
  );
};

export default MoodPage;