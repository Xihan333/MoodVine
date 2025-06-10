import { View } from "@tarojs/components"
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useState } from 'react'

import { useSelector, useDispatch } from 'react-redux';

import 'normalize.css'
import './scripDetail.scss'

const scripDetail = () => {
  const [loading, setLoading] = useState(false)

  const data = useSelector((state) => state.scrip);

  useLoad(() => {
    console.log("已加载Redux状态:", data); 
  });

  if (!data) return <View>Loading...</View>;

    return (
        <View>
            <View>心情指数: {data.mood}</View>
            <View>每日语录: {data.sentance}</View>
            <View>更新时间: {data.time}</View>
        </View>
    );
};   

export default scripDetail;