import { View, Text } from "@tarojs/components"
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useState } from 'react'

import { useSelector, useDispatch } from 'react-redux';

import 'normalize.css'
import './scripDetail.scss'

import bgcard from '../../assets/moodpaper/bigpaper.png'

const scripDetail = () => {
  const [loading, setLoading] = useState(false)

  const data = useSelector((state) => state.scrip);

  useLoad(() => {
    console.log("已加载Redux状态:", data); 
  });

  if (!data) return <View>Loading...</View>;

    return (
        <View>
            <View 
              className="scrip-card"
              style={{ backgroundImage: `url(${bgcard})` }}
            >
                <Text className="scrip-content">{data.sentance}</Text>
            </View>
            
        </View>
    );
};   

export default scripDetail;