import React from 'react';
import { View, Text, Button } from '@tarojs/components';
import { useSelector, useDispatch } from 'react-redux';
import { increment, decrement, incrementByAmount } from '../../store/features/testSlice';
import request from '../../utils/request';

export default function Score() {
  // 获取状态
  const count = useSelector((state) => state.counter.value);
  // 获取 dispatch 函数
  const dispatch = useDispatch();

  const hhh = () => {
    const res = request.post('/api/auth/login', {
        studentNum: 1,
        password: 1
      })
  };

  return (
    <View>
      <Text onClick={hhh}>Count: {count}</Text>
      <Button onClick={() => dispatch(incrementByAmount(5))}>Increment</Button>
      <Button onClick={() => dispatch(decrement())}>Decrement</Button>
    </View>
  );
}