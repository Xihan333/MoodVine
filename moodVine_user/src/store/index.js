import { configureStore } from '@reduxjs/toolkit';
import testReducer from './features/testSlice';
import userReducer from './features/userSlice';
import activityReducer from './features/activitySlice';
import scripReducer from './features/scripSlice';

const store = configureStore({
  reducer: {
    counter: testReducer, // 将 reducer 添加到 store 中,
    user: userReducer,
    activity: activityReducer,
    scrip: scripReducer,
  },
});

export default store;