import { View, Text, Image, Textarea, Video, ScrollView } from '@tarojs/components'
import { Button, Dialog } from '@taroify/core'
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import React, { useState, useEffect, useRef } from 'react'
import { Close } from '@taroify/icons'

import audioIcon from '../../assets/audio-icon.png'
import picIcon from '../../assets/pic-icon.png'
import textIcon from '../../assets/text-icon.png'
import dialog from '../../assets/dialog.png'
import exitbtn from '../../assets/exit.png'

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
    const [isRecording, setIsRecording] = useState(false)
    const [dialogs, setDialogs] = useState([]);  //对话记录状态
    const recorderManager = useRef(Taro.getRecorderManager())
    const animationRef = useRef(null)
    const audioRef = useRef < Taro.InnerAudioContext | null > (null);

    const [exitDialogVisible, setExitDialogVisible] = useState(false); // 退出确认对话框状态


    //添加对话辅助函数
    const addDialog = (sender, type, content) => {
        const newDialog = {
            sender,
            type,
            content,
        };
        
        setDialogs(prev => [...prev, newDialog]);

        console.log(dialogs)
        return newDialog;
    };
    

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
                setText('')
            });

        } catch (error) {
            console.error('音频处理错误:', error);
            console.log('播放出错了，请检查网络');
        }
    };

    const sendText = async() => {
        console.log('文字')
        setShowModal(false)
        addDialog('user', 'text', chatText);

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

                addDialog('other', 'text', textChat.data.data.answer);
                
                smartResourceLoader(newUrl)
                    .then(() => console.log('音频切换成功'))
                    .catch(err => console.error('最终失败:', err.message));
                
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
                addDialog('user', 'image', data.msg);
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

                addDialog('other', 'text', picChat.data.data.answer);
                
                smartResourceLoader(newUrl)
                    .then(() => console.log('音频切换成功'))
                    .catch(err => console.error('最终失败:', err.message));
                
            }

        } catch (error) {
            Taro.hideLoading();
            console.error('上传失败:', error);
            Taro.showToast({ icon: 'error', title: '上传失败' });
        }
    };

    // 处理录音动画效果
    useEffect(() => {
        if (!isRecording) return;
        
        const createAnimation = () => {
        const animation = Taro.createAnimation({
            duration: 800,
            timingFunction: 'ease-in-out'
        })
        
        animation.scale(1.1).step()
        animation.scale(1.0).step()
        
        return animation;
        }
        
        const animate = () => {
        if (!isRecording) return;
        const animation = createAnimation()
        animationRef.current = animation
        requestAnimationFrame(() => {
            animation.export()
        })
        
        setTimeout(animate, 1600)
        }
        
        animate();
        
        return () => {
        // 清理动画定时器
        animationRef.current = null;
        }
    }, [isRecording])

    // 安全的动画获取函数
    const getAnimationScale = () => {
        if (!animationRef.current) return 1;
        try {
        return animationRef.current.actions?.[0]?.args?.[0] || 1;
        } catch (e) {
        return 1;
        }
    };

    // 录音参数配置
    const recordOptions = {
    duration: 60000,       // 录音时长（毫秒）
    sampleRate: 44100,     // 采样率
    numberOfChannels: 1,   // 声道数
    encodeBitRate: 192000, // 编码码率
    format: 'mp3',         // 音频格式
    };

    function startRecord() {
        setIsRecording(true)
        // 设置录音事件监听
        recorderManager.current.onStart(() => {
            console.log('录音开始');
        });
        
        recorderManager.current.onStop((res) => {
            console.log('录音结束', res.tempFilePath);
            // setAudioUrl(res.tempFilePath); // 保存录音文件路径
            uploadAudioFile(res.tempFilePath)
            
        });
        
        recorderManager.current.onError((err) => {
            console.error('录音错误:', err);
            setIsRecording(false);
        });
  
        // 开始录音
        recorderManager.current.start(recordOptions);
    }

    // 停止录音并播放
    const stopAndPlay = async() => {
        setIsRecording(false)
        recorderManager.current.stop(); // 停止录音
        
    }

    const handleRecord = () => {
        if (isRecording) {
            // recorderManager.current.stop();
            stopAndPlay();
            return;
        }
        startRecord()
    }

    function replaceDomain(url, newDomain) {
        const regex = /^(https?:\/\/)[^\/]+/;
        return url.replace(regex, `$1${newDomain}`);
    }

    function smartResourceLoader(url, maxAttempts = 10, initialDelay = 1000) {
        return new Promise(async (resolve, reject) => {
            let attempt = 0;
            let delayTime = initialDelay;
            
            const attemptLoad = async () => {
            attempt++;
            try {
                console.log(`尝试加载资源 (第 ${attempt} 次): ${url}`);
                
                // 先检查资源是否存在（使用HEAD请求减少带宽消耗）
                const headRes = await Taro.request({
                url,
                method: 'HEAD',
                timeout: 3000
                });
                
                if (headRes.statusCode === 404) {
                throw new Error('资源尚未就绪 (404)');
                }
                
                // 资源存在时才执行完整操作
                handleAudioToggle(url);
                console.log(`资源加载成功: ${url}`);
                resolve();
            } catch (error) {
                console.warn(`资源加载失败: ${error.message}`);
                
                // 指数退避策略：每次失败后等待时间加倍
                // delayTime *= 2;
                
                if (attempt >= maxAttempts) {
                reject(new Error(`资源加载失败，超过最大尝试次数 (${maxAttempts}次)`));
                return;
                }
                
                // 智能等待：根据错误类型调整策略
                const nextDelay = error.message.includes('404') ? delayTime : 1000;
                console.log(`等待 ${nextDelay}ms 后重试...`);
                
                await new Promise(res => setTimeout(res, nextDelay));
                attemptLoad();
            }
            };
            
            attemptLoad();
        });
    }
    // 上传音频文件
    const uploadAudioFile = async (filePath) => {
        if (!filePath) {
        Taro.showToast({ title: '没有录音文件', icon: 'error' });
        return;
        }
        
        Taro.showLoading({ title: '上传中...' });
        
        try {
        const uploadRes = await Taro.uploadFile({
            url: 'http://localhost:2025/file/upload',
            filePath,
            name: 'file',
            formData: { 
            type: 'audio',
            timestamp: Date.now()
            },
        });

        Taro.hideLoading();
        
        if (uploadRes.statusCode !== 200) {
            throw new Error(`上传失败，状态码: ${uploadRes.statusCode}`);
        }
        
        const data = JSON.parse(uploadRes.data || '{}');
        
        if (data.msg) {       
            addDialog('user', 'audio', data.msg);

            const picChat = await request.post('/chat/ttsVoiceChat',{
                'sessionId': sessionId,
                'userId': Taro.getStorageSync('userInfo').id,
                'voiceUrl': data.msg 
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

                addDialog('other', 'text', picChat.data.data.answer);
                
                smartResourceLoader(newUrl)
                    .then(() => console.log('音频切换成功'))
                    .catch(err => console.error('最终失败:', err.message));
                }
            
        } else {
            throw new Error('上传失败: 无返回链接');
        }
        } catch (error) {
        Taro.hideLoading();
        Taro.showToast({ icon: 'error', title: '上传失败' });
        console.error('音频上传失败:', error);
        }
    }
    
    // 处理保存对话
    const saveDialogs = async () => {
        try {
            if (!dialogs || dialogs.length === 0) {
                Taro.showToast({ title: '没有对话内容可保存', icon: 'none' });
                return;
            }
            
            // 将dialogs数组转为字符串
            const dialogsString = JSON.stringify(dialogs);
            console.log(dialogsString)
            
            Taro.showLoading({ title: '保存对话中...' });
            
            // 调用保存接口
            const saveRes = await request.post('/user/scrip/saveScrip', {
                content: dialogsString
            });
            
            Taro.hideLoading();
            
            if (saveRes.data.code === 200) {
                Taro.showToast({ title: '对话保存成功', icon: 'success' });
                return true;
            } else {
                throw new Error('保存失败: ' + saveRes.data.message);
            }
        } catch (error) {
            Taro.hideLoading();
            Taro.showToast({ title: '保存失败', icon: 'none' });
            console.error('对话保存失败:', error);
            return false;
        }
    };
    
    // 处理退出前的保存
    const handleSaveBeforeExit = async () => {
        // 只有在有对话内容时才提示保存
        if (dialogs.length > 0) {
            // 弹出保存提示
            const confirmRes = await new Promise(resolve => {
                Taro.showModal({
                    title: '提示',
                    content: '您的对话内容尚未保存，是否保存后再退出？',
                    confirmText: '保存',
                    cancelText: '退出',
                    success: resolve
                });
            });
            
            if (confirmRes.confirm) {
                // 用户选择保存退出
                const saveSuccess = await saveDialogs();
                
                if (saveSuccess) {
                    // 保存成功后实际退出
                    Taro.reLaunch({ url: '/pages/index/index'});
                }
                return;
            } else {
                Taro.navigateBack();
            }
        } else {
            Taro.navigateBack();
        }
        
    };
    
    // 确认对话框的保存并退出操作
    // const handleConfirmExit = async () => {
    //     setExitDialogVisible(false);
    //     const saveSuccess = await saveDialogs();
        
    //     if (saveSuccess) {
    //         Taro.navigateBack();
    //     }
    // };
    
    // 确认对话框的不保存退出操作
    // const handleCancelExit = () => {
    //     setExitDialogVisible(false);
    //     Taro.navigateBack();
    // };

    return (
        <View>
            <View className='gif-background' />
            <View className='exit-btn' onClick={handleSaveBeforeExit}>
                <Image className='exit' src={ exitbtn } />
            </View>

            { outputText && <View className="dialog-wrapper">
                <View className="dialog-container">
                    {/* 对话框图片背景 */}
                    <Image 
                    className="dialog-image" 
                    src={dialog} 
                    mode="aspectFit" 
                    />
                    
                    {/* 文本内容容器 - 根据图片特性精准定位 */}
                    <View className="text-container">
                    <ScrollView 
                        scrollY 
                        className="text-scroll"
                        scrollWithAnimation
                        enhanced
                        showScrollbar={false}
                    >
                        <Text className="dialog-text">
                        {outputText}
                        </Text>
                    </ScrollView>
                    </View>
                </View>
            </View>}

            {/* <Image className='role' src={role} /> */}
            <View className='user-input'>
                <View className='text-input'>
                    <Image className='text-icon' src={textIcon} onClick={() => setShowModal(true)} />
                </View>
                <View className='audio-input'>
                    <Image 
                        className={`audio-icon ${isRecording ? 'recording' : ''}`}
                        style={{ 
                        transform: isRecording ? `scale(${getAnimationScale()})` : 'scale(1)',
                        transition: 'transform 0.3s ease-in-out'
                        }}
                        src={audioIcon} 
                        onClick={handleRecord}
                    />
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

            {/* 退出确认对话框 */}
            {/* <Dialog
                open={exitDialogVisible}
                onClose={() => setExitDialogVisible(false)}
            >
                <Dialog.Header>结束对话</Dialog.Header>
                <Dialog.Content>
                    您确定要结束当前对话并保存吗？
                </Dialog.Content>
                <Dialog.Actions>
                    <Button onClick={() => setExitDialogVisible(false)}>取消</Button>
                    <Button onClick={handleCancelExit}>直接退出</Button>
                    <Button onClick={handleConfirmExit}>保存并退出</Button>
                </Dialog.Actions>
            </Dialog> */}
        </View>
    );
};

export default chatAI;