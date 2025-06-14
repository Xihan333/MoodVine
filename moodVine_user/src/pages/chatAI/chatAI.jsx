import { View, Text, Image, Textarea } from '@tarojs/components'
import { Button } from '@taroify/core'
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import React, { useState, useEffect, useRef } from 'react'
import { Close } from '@taroify/icons'

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
    const [audioUrl,setAudioUrl] = useState('')
    const [showModal, setShowModal] = useState(false);
    const [chatText,setChatText] = useState('')
    const audioRef = useRef < Taro.InnerAudioContext | null > (null);

    // 简单直接的音频播放函数
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

    const sendText = async() => {
        console.log('文字')
        setShowModal(false)
        const textChat = await request.post('/chat/ttsChat',{
                'sessionId': sessionId,
                'userId': Taro.getStorageSync('userInfo').id,
                'message': chatText
            })

            console.log(textChat.data)
            if ( textChat.data.code == 200 ) {
                setText(textChat.data.data.answer)
                setSessionId(textChat.data.data.sessionId)

                console.log(textChat.data.data.text)
                setAudioUrl(textChat.data.data.text)

                console.log(audioUrl)

                const newUrl = replaceDomain(
                    textChat.data.data.text,
                    "pub-a3b9222a444c40648c0a11b32ecb2287.r2.dev"
                );
                console.log(newUrl)
                
                delay(5000)
                    .then(() => {
                        handleAudioToggle(newUrl); // 3秒后执行目标函数
                        console.log("3秒后成功触发音频切换");
                    })
                    .catch(error => {
                        console.error("延迟执行失败:", error);
                    });
            }
        setChatText('')
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
                // Taro.previewImage({ urls: [data.msg] }); // 预览图片
            } else {
                Taro.showToast({ icon: 'error', title: '图像上传失败' });
                return;
            }

            const picChat = await request.post('/chat/ttsImageChat',{
                'sessionId': sessionId,
                'userId': Taro.getStorageSync('userInfo').id,
                'imageUrl': data.msg 
            })

            console.log(picChat.data)
            if ( picChat.data.code == 200 ) {
                setText(picChat.data.data.answer)
                setSessionId(picChat.data.data.sessionId)

                console.log(sessionId)

                console.log(picChat.data.data.text)
                setAudioUrl(picChat.data.data.text)

                console.log(audioUrl)

                const newUrl = replaceDomain(
                    picChat.data.data.text,
                    "pub-a3b9222a444c40648c0a11b32ecb2287.r2.dev"
                );
                console.log(newUrl)
                
                delay(5000)
                    .then(() => {
                        handleAudioToggle(newUrl); // 3秒后执行目标函数
                        console.log("3秒后成功触发音频切换");
                    })
                    .catch(error => {
                        console.error("延迟执行失败:", error);
                    });
            }

        } catch (error) {
            Taro.hideLoading();
            console.error('上传失败:', error);
            Taro.showToast({ icon: 'error', title: '上传失败' });
        }
    };

    const sendAudio = () => {
        console.log('语音')
        handleAudioToggle(audioUrl)
    }

    function replaceDomain(url, newDomain) {
        const regex = /^(https?:\/\/)[^\/]+/;
        return url.replace(regex, `$1${newDomain}`);
    }

    function delay(timeout) {
    return new Promise(resolve => {
        setTimeout(() => {
        resolve(); // 3秒后解析Promise
        }, timeout);
    });
    }

    return (
        <View>
            <Text>{outputText}</Text>
            <Image className='role' src={role} />
            <View className='user-input'>
                <View className='text-input'>
                    <Image className='text-icon' src={textIcon} onClick={() => setShowModal(true)} />
                </View>
                <View className='audio-input'>
                    <Image className='audio-icon' src={audioIcon} onClick={sendAudio} />
                </View>
                <View className='pic-input'>
                    <Image className='pic-icon' src={picIcon} onClick={sendPic} />
                </View>
            </View>

            {showModal && (
                <View className='modal-overlay'>
                <View className='modal-content'>
                    <Close 
                    className='close-icon' 
                    onClick={() => setShowModal(false)} 
                    />
                    <View className='modal-header'>
                        <Text className='modal-title'>编辑文本</Text>
                    </View>
                    
                    <View className='form-group'>
                    <Textarea 
                        className='form-textarea'
                        value={chatText}
                        onInput={(e) => setChatText(e.detail.value)}
                        autoHeight
                        placeholder='记录此刻的心情'
                        maxlength={200}
                    />
                    </View>
                    
                    <Button 
                        className='save-button'
                        color="primary" 
                        block
                        onClick={sendText}
                        >
                    聊一聊吧
                    </Button>
                </View>
                </View>
            )}
        </View>
    );
};

export default chatAI;