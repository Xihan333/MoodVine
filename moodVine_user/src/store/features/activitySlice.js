import { createSlice } from '@reduxjs/toolkit';

const activitySlice = createSlice({
  name: 'actiivty', // slice 的名称
  // 初始状态
  initialState: { 
    id: 0,
    name: "早餐周活动",
    description: "但年置。二路细音步代。声这道样油位林离自空。证的前转技了样为济因。那角长。",
    picture: "https://loremflickr.com/400/400?lock=3457343226020504",
    startTime: "2025-11-28",
    finishTime: "2024-12-31",
    isSignUp: false,
    isClockIn: false,
    clockIns: [{
      "id":0,
      "date": "",
      "content": "",
      "pictures": [""]
    }]
  },
  reducers: {
    setActivity: (state,action) => {
      Object.assign(state, action.payload);
    },
    signUp: (state) => {
      state.isSignUp = true;
    },
    clockIn: (state, action) => {
      state.isClockIn = true;
      state.clockIns.push(action.payload);
    },
    setId: (state,action) => {
      state.id = action.payload;
    },
    setIsClockIn: (state, action) => {
      state.isClockIn = action.payload;
    },
    setClockIns: (state, action) => {
      state.clockIns = action.payload;
    },
  },
});

// 导出 action creators
export const { setActivity, signUp, clockIn, setId, setIsClockIn, setClockIns } = activitySlice.actions;

// 导出 reducer
export default activitySlice.reducer;