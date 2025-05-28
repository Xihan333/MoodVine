import { configureStore } from '@reduxjs/toolkit';
import testReducer from './features/testSlice';
import userReducer from './features/userSlice';
import activityReducer from './features/activitySlice';

const store = configureStore({
  reducer: {
    counter: testReducer, // 将 reducer 添加到 store 中,
    user: userReducer,
    activity: activityReducer
  },
});

export default store;