import { configureStore } from '@reduxjs/toolkit';
import testReducer from './features/testSlice'; // 假设有一个 counterSlice

const store = configureStore({
  reducer: {
    counter: testReducer, // 将 reducer 添加到 store 中
  },
});

export default store;