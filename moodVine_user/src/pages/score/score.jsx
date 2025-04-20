import React from 'react';
import { View, Text, Button } from '@tarojs/components';
import { useSelector, useDispatch } from 'react-redux';
import { increment, decrement, incrementByAmount } from '../../store/features/testSlice';

export default function Score() {
  // 获取状态
  const count = useSelector((state) => state.counter.value);
  // 获取 dispatch 函数
  const dispatch = useDispatch();

  return (
    <View>
      <Text>Count: {count}</Text>
      <Button onClick={() => dispatch(increment())}>Increment</Button>
      <Button onClick={() => dispatch(decrement())}>Decrement</Button>
      <Button onClick={() => dispatch(incrementByAmount(5))}>Increment by 5</Button>
    </View>
  );
}