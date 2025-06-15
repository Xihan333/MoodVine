import Taro from '@tarojs/taro';
import { React, useState, useEffect } from 'react';
import { View, Text, Button, Image } from '@tarojs/components';
import { useSelector, useDispatch } from 'react-redux';
import request from '../../utils/request';
import './activityDetail.module.scss';
import img0 from '../../assets/activity0.jpg'
import img1 from '../../assets/activity1.png'
import { signUp, clockIn, setId, setIsClockIn as setActivityIsClockIn, setClockIns as setActivityClockIns } from '../../store/features/activitySlice';
import { setIsClockIn as setUserIsClockIn } from '../../store/features/userSlice';

const ActivityDetail = () => {
  const activity = useSelector((state) => state.activity);

  // 获取打卡列表
  const [period, setPeriod] = useState(7);
  // const [clockIns, setClockIns] = useState([
  //   { id: 1, date: '2024-12-21', content: '今天吃饭了', pictures: [img0,img1] },
  //   { id: 2, date: '2024-12-22', content: '今天吃饭了', pictures: [] },
  //   { id: 3, date: '2024-12-23', content: '今天吃饭了', pictures: [] },
  // ]);  
  // const [clockIns, setClockIns] = useState([]);

  const dispatch = useDispatch();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await request.post('/user/activity/getClockIns', {
          activityId: activity.id
        });
        if (res.data.code === 200) {
          setPeriod(res.data.data.period);
          dispatch(setActivityClockIns(res.data.data.clockIns));
          // 获取当前日期
          const today = new Date();
          const todayFormatted = today.toISOString().split('T')[0]; // 格式为 "YYYY-MM-DD"
          // 获取数组的最后一个元素
          const lastClockIn = res.data.data.clockIns[res.data.data.clockIns.length - 1];  
          // 检查最后一个元素的 date 是否是今天
          if (lastClockIn && lastClockIn.date === todayFormatted) {
            dispatch(setActivityIsClockIn(true))
          } else {
            dispatch(setActivityIsClockIn(false))
          }
        }
      } catch (error) {
        console.error('Error fetching clock-ins:', error);
      }
    };
    fetchData();

    return () => {
      Taro.reLaunch({
        url: '/pages/activityList/activityList'
      });
    };
  }, []); // 空依赖数组确保仅执行一次

  // 处理报名逻辑
  const handleSignup = async (activityId) => {
    const res = await request.post('/user/activity/signUp',{
      "activityId":activityId
    });
    if(res.data.code===200){
      Taro.showToast({ title: '报名成功', icon: 'none' });
      dispatch(signUp());
    }
  };

  // 处理打卡逻辑
  const handleClockin = (activityId) => {
    dispatch(setId(activityId));
    dispatch(setUserIsClockIn(true));
    Taro.navigateTo({
      url: '/pages/diaryEditor/diaryEditor',
    });
  };

  // 处理查看逻辑
  const handleCheck = () => {
    dispatch(setUserIsClockIn(true));
    Taro.navigateTo({
      url: '/pages/diaryList/diaryList',
    });
  };

  return (
    <View className="activity-page">
      <Image className="activity-image" src={activity.picture} mode="aspectFill" />
      <View className="activity-detail-card">
        <Text className="activity-title">{activity.name}</Text>
        <Text className="activity-date">活动日期：{activity.startTime} - {activity.finishTime}</Text>
        <Text className="activity-description">活动详情：{activity.description}</Text>
      </View>
      {activity.isSignUp?
        <View className="activity-clockin-card">
          <Text className="card-title">活动打卡</Text>
          <View className='clockin-container'>
            <View className="circles-container">
              {Array.from({ length: period }).map((_, index) => (
                <View
                  key={index}
                  className={`circle ${index < activity.clockIns.length ? 'clocked' : ''}`}
                  onClick={handleCheck}
                />
              ))}
            </View>
            {activity.isClockIn?
              <Button className="clockin-button clockined-button">已打卡</Button>
              :<Button className="clockin-button" onClick={()=>handleClockin(activity.id)}>打卡</Button>      
            }
          </View>
        </View>
        :null
      }
      <View className="fixed-bottom-bar">
        {activity.isSignUp?
          <Button className="signup-button signuped-button">已报名</Button>
          :<Button className="signup-button" onClick={()=>handleSignup(activity.id)}>我要报名</Button>         
        }
      </View>
    </View>
  );
};

export default ActivityDetail;