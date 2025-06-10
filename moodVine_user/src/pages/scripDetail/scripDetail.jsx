import { View, Text, Image } from "@tarojs/components"
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useState } from 'react'

import { useSelector, useDispatch } from 'react-redux';

import 'normalize.css'
import './scripDetail.scss'

import bgcard from '../../assets/moodpaper/bigpaper.png'

const MessageBubble = ({ msg, isUser }) => {
  // 头像路径（实际项目中替换为真实路径）
  const avatar = isUser 
    ? require('../../assets/reward0.jpg')
    : require('../../assets/reward1.jpg');

  return (
    <View className={`message ${isUser ? 'user' : 'other'}`}>
      {!isUser && <Image className="avatar" src={avatar} mode="aspectFill" />}
      
      <View className={`bubble ${msg.type === 'image' ? 'image-type' : ''}`}>
        {msg.type === 'text' ? (
          <Text>{msg.content}</Text>
          
        ) : (
          <Image src={msg.content} className="msg-image" mode="widthFix" />
        )}
      </View>
      
      {isUser && <Image className="avatar" src={avatar} mode="aspectFill" />}
    </View>
  );
};

const scripDetail = () => {
  const [loading, setLoading] = useState(false)

  const scripData = useSelector((state) => state.scrip);
  const [data, setData] = useState({
    'mood': scripData.mood,
    'sentance': scripData.sentance,
    'content': "[{\"sender\":\"user\",\"type\":\"text\",\"content\":\"Any good travel spots?\"},{\"sender\":\"other\",\"type\":\"text\",\"content\":\"Try Kyoto for temples!\"},{\"sender\":\"user\",\"type\":\"image\",\"content\":\"../../assets/reward0.jpg\"}]"
  })

  const [messages, setMessages] = useState([]);

  useLoad(() => {
    console.log("已加载Redux状态:", data); 
    try {
        // 安全解析JSON字符串
        const parsedContent = JSON.parse(data.content);
        console.log(parsedContent)
        setMessages(parsedContent);
    } catch (error) {
        console.error('JSON解析失败:', error);
        Taro.showToast({ title: '数据格式错误', icon: 'none' });
    }
  });

  if (!scripData) return <View>Loading...</View>;

    return (
        <View>
            <View 
              className="scrip-card"
              style={{ backgroundImage: `url(${bgcard})` }}
            >
                <Text className="scrip-content">{scripData.sentance}</Text>
            </View>
            <View className="title-card">
                <Text className="title">————聊愈回忆————</Text>
            </View>
            <View className="dialogue-area">
                {messages.map((msg, index) => (
                <MessageBubble 
                    key={index} 
                    msg={msg} 
                    isUser={msg.sender === 'user'} 
                />
                ))}
            </View>
        </View>
    );
};   

export default scripDetail;