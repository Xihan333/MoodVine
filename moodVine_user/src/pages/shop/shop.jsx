import Taro from '@tarojs/taro'
import { React, useState, useEffect } from 'react';
import { View, Text, Button, Image } from '@tarojs/components';
import { useSelector, useDispatch } from 'react-redux';
import { minusScore } from '../../store/features/userSlice';
import request from '../../utils/request';
import './shop.scss'; // 引入样式文件
import reward0Img from '../../assets/reward0.jpg'

const Shop = () => {
  const dispatch = useDispatch();
  // 获取奖励列表
  const [rewards, setRewards] = useState([
    { id: 1, name: '奖励1', content: reward0Img, point: 5, isHad: 1 },
    { id: 2, name: '奖励2', content: reward0Img, point: 5, isHad: 0 },
    { id: 3, name: '奖励3', content: reward0Img, point: 5, isHad: 1 },
  ]);
  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await request.get('/user/reward/getAllRewards');
        if (res.data.code === 200) {
          setRewards(res.data.data.rewards);
        }
      } catch (error) {
        console.error('Error fetching rewards:', error);
      }
    };
    fetchData();
  }, []); // 空依赖数组确保仅执行一次

  const handleRedeem = async (item) => {
    const res = await request.post('/user/reward/redeemReward',{
      id:item.id
    });
    if(res.data.code===200){
      Taro.showToast({ title: '兑换成功', icon: 'success' });
      // 更新兑换状态
      setRewards(prevRewards => 
        prevRewards.map(reward =>
          reward.id === item.id ? { ...reward, isHad: 1 } : reward
        )
      );
      // 扣除score
      dispatch(minusScore(item.point));
      Taro.setStorageSync('score', Taro.getStorageSync('score')-item.point); // 存储用户信息
    }
    else if(res.data.code===400){
      Taro.showToast({ title: res.data.msg, icon: 'error' });
    }
  };

  const listItems = rewards.map((item) => (
    <View key={item.id} className='reward-card'>
      <View className='reward-info'>
        <Image src={item.content} className='reward-image'/>
        <Text className='reward-name'>{item.name}</Text>
        <Text className='reward-price'>￥{item.point}</Text>
      </View>
      <View className='reward-status'>
        <Text 
          className={item.isHad ? 'item-redeemed' : 'item-available'}
          onClick={() => !item.isHad && handleRedeem(item)}
        >
          {item.isHad ? '已兑换' : '兑换'}
        </Text>
      </View>
    </View>
  ));

  return (
    <View className='reward-list'>
      {listItems}
    </View>
  );
};

export default Shop;

