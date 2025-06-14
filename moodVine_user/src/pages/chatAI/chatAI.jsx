import { View, Text, Image } from '@tarojs/components'
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useState, useEffect, useRef } from 'react'

import role from '../../assets/111.png'
import audioIcon from '../../assets/audio-icon.png'
import picIcon from '../../assets/pic-icon.png'
import textIcon from '../../assets/text-icon.png'

import 'normalize.css'
import './chatAI.scss'
import request from '../../utils/request'

const chatAI = () => {

    const [outputText, setText] = useState('')
    const [sessionId, setSessionId] = useState('')
    const [picUrl, setPicUrl] = useState('')
    const audioRef = useRef < Taro.InnerAudioContext | null > (null);

    // 简单直接的音频播放函数
    const handleAudioToggle = (audioSrc) => {
        try {
            // 直接创建并播放音频
            const audio = Taro.createInnerAudioContext();

            // 设置音频源
            audio.src = 'https://img.rainnn.top/audio_1749890593486_5792533b.mp3';
            audio.autoplay = true;

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

    const sendText = () => {
        console, log('文字')
    }

    const sendPic = async () => {
        try {
            // 1. 选择图片
            const res = await Taro.chooseImage({
                count: 1,
                sizeType: ['compressed'],
                sourceType: ['album', 'camera']
            });

            if (!res.tempFilePaths.length) return;

            // 2. 显示加载状态
            Taro.showLoading({ title: '上传中...', mask: true });

            // 3. 使用 Taro.uploadFile 上传（关键修改点）[2,5](@ref)
            const uploadRes = await Taro.uploadFile({
                url: 'http://localhost:2025/file/upload', // 你的上传接口
                filePath: res.tempFilePaths[0],
                name: 'file', // 对应后端接收文件的字段名
                formData: { // 普通对象形式传递附加参数
                    timestamp: Date.now()
                },
            });

            console.log(uploadRes)


            // 4. 处理响应
            Taro.hideLoading();
            const data = JSON.parse(uploadRes.data); // 需要手动解析

            if (data.msg) {
                console.log(`上传成功！URL: ${data.msg}`);
                Taro.previewImage({ urls: [data.msg] }); // 预览图片
            } else {
                console.log('上传失败：未获取到URL');
            }
        } catch (error) {
            Taro.hideLoading();
            console.error('上传失败:', error);
            console.log(`上传失败: ${error.errMsg || '网络错误'}`);
            Taro.showToast({ icon: 'error', title: '上传失败' });
        }
    };

    const sendAudio = () => {
        console.log('语音')
    }


    return (
        <View>
            <Text>{outputText}</Text>
            <Image className='role' src={role} />
            <View className='user-input'>
                <View className='text-input'>
                    <Image className='text-icon' src={textIcon} onClick={sendText} />
                </View>
                <View className='audio-input'>
                    <Image className='audio-icon' src={audioIcon} onClick={sendAudio} />
                </View>
                <View className='pic-input'>
                    <Image className='pic-icon' src={picIcon} onClick={sendPic} />
                </View>
            </View>
        </View>
    );
};

export default chatAI;