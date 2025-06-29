import { createSlice } from '@reduxjs/toolkit';

const userSlice = createSlice({
  name: 'user',
  initialState: {
    avatar: "",
    email: null,
    gender: null,
    id: null,
    nickName: "",
    openId: null,
    password: null,
    score: 0,
    signature: null,
    userType: null,
    isClockIn: false
  },
  reducers: {
    setScore: (state, action) => {
      state.score = action.payload;
    },
    minusScore: (state, action) => {
      state.score -= action.payload;
    },
    addScore: (state, action) => {
      state.score += action.payload;
    },
    setUserInfo: (state, action) => {
      Object.assign(state, action.payload);
    },
    setIsClockIn: (state, action) => {
      state.isClockIn = action.payload;
    },
  },
});
 
// 导出 action creators
export const { setScore, minusScore, addScore, setUserInfo, setIsClockIn } = userSlice.actions;

// 导出 reducer
export default userSlice.reducer;