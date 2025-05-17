import { createSlice } from '@reduxjs/toolkit';

const userSlice = createSlice({
  name: 'user', // slice 的名称
  // 初始状态
  initialState: { 
    name: '',
    score: 50 
  },
  reducers: {
    minusScore: (state,action) => {
      state.score -= action.payload; // 直接修改 state（内置了 Immer，无需手动返回新状态）
    },
    // decrement: (state) => {
    //   state.value -= 1;
    // },
    // incrementByAmount: (state, action) => {
    //   state.value += action.payload; // action.payload 是传递的参数
    // },
  },
});

// 导出 action creators
export const { minusScore } = userSlice.actions;

// 导出 reducer
export default userSlice.reducer;