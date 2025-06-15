import { React, useState, useEffect } from 'react';
import Taro from '@tarojs/taro';
import { View, Text, Button, Image } from '@tarojs/components';
import { useSelector, useDispatch } from 'react-redux';
import request from '../../utils/request';
import './activityList.scss';
import img0 from '../../assets/activity0.jpg'
import img1 from '../../assets/activity1.png'
import { setActivity } from '../../store/features/activitySlice';

const Activity = () => {
  // 获取活动列表
  const [activities, setActivities] = useState([
    { id: 1, name: '早餐周挑战(〃｀ 3′〃)', description: '铁科院公司称，公司负责运营12306网站，而非客运合同承运主体，不具有开具铁路票据的主体身份。李先生有权领取报销凭证，但需要根据铁路部门相关规定提供购票时所使用的有效身份证件。铁路运输企业作为个人信息处理者，有义务保护该信息不被披露。在现行法规规定铁路票据为报销凭证的情况下，铁路运输企业要求旅客提供身份证件换取报销凭证并非加重旅客负担，具有合理性。', picture: img0, startTime: '2024-12-21', finishTime: "2024-12-31", isSignUp: 1 },
    { id: 2, name: '午餐周挑战(〃｀ 3′〃)', description: '', picture: img1, startTime: '2024-11-21', finishTime: "2024-12-31", isSignUp: 0 },
    { id: 3, name: '晚餐周挑战(〃｀ 3′〃)', description: '', picture: img0, startTime: '2024-11-21', finishTime: "2024-12-31", isSignUp: 1 }
  ]);

  useEffect(() => {
    const fetchData = async () => {
      const res = await request.get('/user/activity/getAllActivities');
      if (res.data.code === 200) {
        setActivities(res.data.data.activities);
      }
    }
    fetchData();
  }, []); // 空依赖数组确保仅执行一次

  const dispatch = useDispatch();
  const handleDetail = (item) => {
    dispatch(setActivity(item));
    Taro.navigateTo({
      url: '/pages/activityDetail/activityDetail',
    });
  };

  const handleClockin = (itemId) => {
    // TODO 处理打卡逻辑
  };

  const handleSignup = async (itemId) => {  
    const res = await request.post('/user/activity/signUp',{
      activityId:itemId
    });
    if(res.data.code===200){
      setActivities(activities.map(item => {
        if (item.id === itemId) {
          return {
            ...item,
            isSignUp: true
          };
        }
        return item;
      }));
      Taro.showToast({ title: '报名成功', icon: 'none' });
    }
  };

  const listItems = activities.map((item) => (
    <View 
      key={item.id} 
      className="activity-card"
      style={{ backgroundImage: `url(${item.picture})` }}
      onClick={()=>handleDetail(item)}
    >
      <Text className="activity-name">{item.name}</Text>
      <View className="activity-status">
        {item.isSignUp ? (
          <Text className="item-clockin" onClick={(e)=>{
            e.stopPropagation(); // 阻止事件冒泡
            handleClockin(item.id);
          }}>已报名</Text>
        ) : (
          <Text className="item-signup" onClick={(e)=>{
            e.stopPropagation(); // 阻止事件冒泡
            handleSignup(item.id);
          }}>报名</Text>
        )}
      </View>
    </View>
  ));

  return (
    <View className='activity-list'>
      {listItems}
    </View>
  );
};

export default Activity;