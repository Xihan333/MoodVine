import { createSlice } from '@reduxjs/toolkit';

const testSlice = createSlice({
  name: 'test', // slice 的名称
  initialState: { value: 0 }, // 初始状态
  reducers: {
    increment: (state) => {
      state.value += 1; // 直接修改 state（内置了 Immer，无需手动返回新状态）
    },
    decrement: (state) => {
      state.value -= 1;
    },
    incrementByAmount: (state, action) => {
      state.value += action.payload; // action.payload 是传递的参数
    },
  },
});

// 导出 action creators
export const { increment, decrement, incrementByAmount } = testSlice.actions;

// 导出 reducer
export default testSlice.reducer;