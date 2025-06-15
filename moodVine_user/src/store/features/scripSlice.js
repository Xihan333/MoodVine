import { createSlice } from '@reduxjs/toolkit';

const scripSlice = createSlice({
  name: 'scrip', // slice 的名称
  // 初始状态
  initialState: { 
     'id': 1,
     'mood': 1, 
     'sentence': '明天会更好' ,
     'time': '6-1'
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