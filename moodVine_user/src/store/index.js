import { configureStore } from '@reduxjs/toolkit';
import testReducer from './features/testSlice';
import userReducer from './features/userSlice';

const store = configureStore({
  reducer: {
    counter: testReducer, // 将 reducer 添加到 store 中,
    user: userReducer
  },
});

export default store;