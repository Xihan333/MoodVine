import { React, useState, useEffect } from 'react';
import { View, Text, Button, Image } from '@tarojs/components';
import { useSelector, useDispatch } from 'react-redux';
import { increment, decrement, incrementByAmount } from '../../store/features/testSlice';
import request from '../../utils/request';
import './shop.scss'; // 引入样式文件
import reward0Img from '../../assets/reward0.jpg'

const Shop = () => {
  // 获取奖励列表
  // const [rewards, setrewards] = useState([]);
  // const [loading, setLoading] = useState(true);
  // useEffect(() => {
  //   const res = request.get('/reward/getAllRewards');
  //   console.log(res.data);
  //   // setRewards(res.data);
  // }, []); // 空依赖数组确保仅执行一次

  const rewards = [
    { id: 1, name: '奖励1', content: reward0Img, isHad: 1 },
    { id: 2, name: '奖励2', content: reward0Img, isHad: 0 },
    { id: 3, name: '奖励3', content: reward0Img, isHad: 1 },
  ];

  const handleRedeem = (itemId) => {
    const res = request.post('/reward/redeemRewrad',{
      id:itemId
    });
    console.log(res.data);
  };

  const listItems = rewards.map((item) => (
    <View key={item.id} className='reward-card'>
      <View className='reward-info'>
        <Image src={item.content} className='reward-image' mode='aspectFill' />
        <Text className='reward-name'>{item.name}</Text>
      </View>
      <View className='reward-status'>
        <Text 
          className={item.isHad ? 'item-redeemed' : 'item-available'}
          onClick={() => !item.isHad && handleRedeem(item.id)}
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