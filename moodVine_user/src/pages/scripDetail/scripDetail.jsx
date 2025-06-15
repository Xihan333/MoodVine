import { View, Text, Image } from "@tarojs/components"
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useState } from 'react'

import { useSelector, useDispatch } from 'react-redux';

import 'normalize.css'
import './scripDetail.scss'
import request from '../../utils/request'

import bgcard from '../../assets/moodpaper/bigpaper.png'

const handleAudioToggle = ( audioSrc ) => {
        try {
            // 直接创建并播放音频
            const audio = Taro.createInnerAudioContext();

            // 设置音频源
            audio.src = audioSrc;
            audio.autoplay = true;

            console.log(`开始播放 ${audioSrc}`)

            // 开始播放
            audio.play();

            // 错误处理
            audio.onError((error) => {
                console.error('音频播放失败:', error);
                console.log('音频播放失败，请重试');
                audio.destroy(); // 销毁失败的音频实例
            });

            // 播放结束时销毁实例
            audio.onEnded(() => {
                audio.destroy();
            });

        } catch (error) {
            console.error('音频处理错误:', error);
            console.log('播放出错了，请检查网络');
        }
    };


const MessageBubble = ({ msg, isUser }) => {
  // 头像路径（实际项目中替换为真实路径）
  const avatar = isUser 
    ? Taro.getStorageSync('userInfo').avatar
    : require('../../assets/man_avatar.png');

    console.log(avatar)
  return (
    <View className={`message ${isUser ? 'user' : 'other'}`}>
      {!isUser && <Image className="avatar" src={avatar} mode="aspectFill" />}
      
      <View 
        className={`bubble ${msg.type} ${msg.type === 'image' ? 'image-type' : ''}`}
        onClick={() => {
          if (msg.type === 'audio') {
            handleAudioToggle(msg.content); // 添加语音播放功能
          }
        }}
      >
        {/* 文本消息 */}
        {msg.type === 'text' && (
          <Text className="text-content">{msg.content}</Text>
        )}
        
        {/* 图片消息 */}
        {msg.type === 'image' && (
          <Image 
            src={msg.content} 
            className="msg-image" 
            mode="aspectFill"
            onClick={() => {
              Taro.previewImage({
                urls: [msg.content],
                current: msg.content
              });
            }}
          />
        )}
        
        {/* 语音消息 */}
        {msg.type === 'audio' && (
          <View className="audio-bubble">
            {/* 语音波形动画 */}
            <View className="audio-wave">
              <View className="wave-bar" />
              <View className="wave-bar" />
              <View className="wave-bar" />
              <View className="wave-bar" />
              <View className="wave-bar" />
            </View>
            
          </View>
        )}
      </View>
      
      {isUser && <Image className="avatar" src={avatar} mode="aspectFill" />}
    </View>
  );
};

const scripDetail = () => {
  const [loading, setLoading] = useState(false)

  const scripData = useSelector((state) => state.scrip);
  console.log(scripData)
  const [data, setData] = useState({
    'id': scripData.id,
    'mood': scripData.mood,
    'sentence': scripData.sentence,
    'content': "[{\"sender\":\"user\",\"type\":\"text\",\"content\":\"Any good travel spots?\"},{\"sender\":\"other\",\"type\":\"text\",\"content\":\"Try Kyoto for temples!\"},{\"sender\":\"user\",\"type\":\"image\",\"content\":\"../../assets/reward0.jpg\"}]"
  })

  const [messages, setMessages] = useState([]);

  const getInitData = async() => {
    const res = await request.post('/user/scrip/getScripDetail',{ 'id': scripData.id })
    console.log(res.data)

    if ( res.data.code == 200 ) {
      setData(res.data.data)
      const parsedContent = JSON.parse(res.data.data.content);
      console.log(parsedContent)
      setMessages(parsedContent);
    }
  }

  useLoad(() => {
    getInitData()
  });

  if (!scripData) return <View>Loading...</View>;

    return (
        <View>
            <View 
              className="scrip-card"
              style={{ backgroundImage: `url(${bgcard})` }}
            >
                <Text className="scrip-content">{scripData.sentence}</Text>
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