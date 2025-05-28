import { React, useState, useEffect } from 'react';
import { View, Text, Button, Image } from '@tarojs/components';
import { useSelector, useDispatch } from 'react-redux';
import request from '../../utils/request';
import './activityDetail.module.scss';
import img0 from '../../assets/activity0.jpg'
import img1 from '../../assets/activity1.png'
import { signUp, clockIn } from '../../store/features/activitySlice';

const ActivityDetail = () => {
  const activity = useSelector((state) => state.activity);

  // 获取打卡列表
  const [period, setPeriod] = useState(7);
  const [clockIns, setClockIns] = useState([
    { id: 1, date: '2024-12-21', content: '今天吃饭了', pictures: [img0,img1] },
    { id: 2, date: '2024-12-22', content: '今天吃饭了', pictures: [] },
    { id: 3, date: '2024-12-23', content: '今天吃饭了', pictures: [] },
  ]);
  const [loading, setLoading] = useState(true);
  // useEffect(() => {
  //   const res = request.post('/activity/getClockIns',{
  //     activityId: activity.id
  //   });
  //   console.log(res.data);
  //   setPeriod();
  //   setClockIns(res.data);
  //   setLoading();

  //   // 获取当前日期
  //   const today = new Date();
  //   const formattedDate = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
  //   if(clockIns.length!=0 && clockIns[clockIns.length-1]==formattedDate){
  //     dispatch(clockIn());
  //   }
  // }, []); // 空依赖数组确保仅执行一次

  const dispatch = useDispatch();
  // TODO 处理报名逻辑
  const handleSignup = (activityId) => {
    const res = request.post('/activity/signUp',{
      "activityId":activityId
    });
    console.log(res.data);
    // 修改按钮样式
    dispatch(signUp());
  };

  // TODO 处理打卡逻辑
  const handleClockin = (activityId) => {
    // 跳转到写日记页面，maybe需要传参 isClockIn activityId
    // 今日已打卡
  };

  // TODO 处理查看逻辑
  const handleCheck = (activity) => {
    // 跳转到看日记页面，maybe需要传参
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
                  className={`circle ${index < clockIns.length ? 'clocked' : ''}`}
                  onClick={()=>handleCheck(activity)}
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