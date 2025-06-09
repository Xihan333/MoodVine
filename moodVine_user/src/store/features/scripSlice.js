import { createSlice } from '@reduxjs/toolkit';

const scripSlice = createSlice({
  name: 'scrip', // slice 的名称
  // 初始状态
  iniialState: { 
     'mood': 1, 
     'sentance': '明天会更好' 
  },
  reducers: {
    setScrip: (state,action) => {
      Object.assign(state, action.payload);
    },
  },
});

// 导出 action creators
export const { setScrip } = scripSlice.actions;

// 导出 reducer
export default scripSlice.reducer;