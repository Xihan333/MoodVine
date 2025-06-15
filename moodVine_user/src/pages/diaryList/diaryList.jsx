import React, { useState, useEffect } from 'react';
import { View, Text, Image, ScrollView } from '@tarojs/components';
import Taro from '@tarojs/taro';
import request from '../../utils/request';
import './diaryList.scss'; // 样式文件
import img1 from '../../assets/paper1.jpg'
import { useSelector, useDispatch } from 'react-redux';
import { setIsClockIn } from '../../store/features/userSlice';

const DiaryList = () => {
  const dispatch = useDispatch();
  const isClockIn = useSelector((state) => state.user.isClockIn);
  const clockIns = useSelector((state) => state.activity.clockIns);

  // 初始日期设为当前月份
  const [currentDate, setCurrentDate] = useState(new Date());
  const [diaries, setDiaries] = useState([]);
  const [papers, setPapers] = useState([]);

  // 根据月份查询日记
  useEffect(() => {
    if(isClockIn){
      wx.setNavigationBarTitle({
        title: '打卡'
      });
      setDiaries(clockIns);
    }
    else{
      wx.setNavigationBarTitle({
        title: '日记'
      });
      // 模拟数据
      // const mockDiaries = getMockDiaries(currentDate);
      // setDiaries(mockDiaries);
      // 发送请求
      const fetchData = async () => {
        const year = currentDate.getFullYear();
        const month = String(currentDate.getMonth() + 1).padStart(2, '0'); // 月份从0开始，所以需要+1，并补零
        const res = await request.post('/user/diary/getDiaries',{
            date:`${year}-${month}`,
        });
        setDiaries(res.data.data.diaries);
        setPapers(res.data.data.papers);
      }
      fetchData();
    }

    return () => {
      dispatch(setIsClockIn(false));
    };
  }, [currentDate]);

  // 获取上个月
  const goToPreviousMonth = () => {
    const newDate = new Date(currentDate);
    newDate.setMonth(newDate.getMonth() - 1);
    setCurrentDate(newDate);
  };

  // 获取下个月
  const goToNextMonth = () => {
    const newDate = new Date(currentDate);
    newDate.setMonth(newDate.getMonth() + 1);
    setCurrentDate(newDate);
  };

  // 格式化日期显示
  const formatDateDisplay = (date) => {
    return `${date.getFullYear()}年${date.getMonth() + 1}月`;
  };

  // 模拟获取日记数据
  const getMockDiaries = (date) => {
    // 这里应该是API请求，返回当前月份的日记数据
    // 模拟一些数据
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    
    // 生成模拟数据
    const mockData = [];
    const daysInMonth = new Date(year, month, 0).getDate();
    
    for (let i = 1; i <= daysInMonth; i++) {
      // 随机生成一些日记数据
      if (Math.random() > 0.5) { // 50%概率有日记
        const day = i;
        const content = `这是${year}年${month}月${day}日的日记内容...`;
        const imageCount = Math.floor(Math.random() * 9) + 1; // 1-9张图片
        const pictures = Array.from({ length: imageCount }, (_, idx) => 
          `https://picsum.photos/200/200?random=${day}${idx}`
        );
        
        mockData.push({
          date: `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`,
          content,
          pictures,
          notepaper: img1
        });
      }
    }
    
    return mockData;
  };

  // 渲染图片网格
  const renderImages = (images) => {
    return (
      <View className='image-grid'>
        {images.map((img, idx) => (
          <View className='grid-item'>
            <Image 
                key={idx}
                src={img} 
                mode='aspectFill'
                className='image-item'
            />
          </View>
        ))}
      </View>
    );
  };

  return (
    <View className='diary-container'>
      {/* 顶部导航 */}
      { !isClockIn && 
      <View className='month-navigator'>
        <View className='month-content'>
            <Text 
            className='arrow' 
            onClick={goToPreviousMonth}
            >
            ◁
            </Text>
            <Text className='month-text'>{formatDateDisplay(currentDate)}</Text>
            <Text 
            className='arrow' 
            onClick={goToNextMonth}
            >
            ▷
            </Text>
        </View>
      </View>
      }
      
      {/* 日记列表 */}
      <ScrollView 
        scroll-y
        show-scrollbar='{{false}}'
        className='diary-list'
      >
        {diaries.map((diary, index) => (
          <View 
            key={index} 
            className='diary-card'
            style={{ 
                backgroundImage: `url(${papers[index]})`,
                backgroundSize: '100% 100%'
            }}
          >
            <Text className='diary-date'>{diary.date}</Text>
            <Text className='diary-content'>{diary.content}</Text>
            {diary.pictures && diary.pictures.length > 0 && renderImages(diary.pictures)}
          </View>
        ))}
      </ScrollView>
    </View>
  );
};

export default DiaryList;