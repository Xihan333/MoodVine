import Taro from '@tarojs/taro'
import { Video } from '@tarojs/components'
import bg from '../assets/bgvideo.mp4'

const VideoBackground = () => {
  return (
    <Video
      src={bg}
      loop={true}          // 关键：开启循环
      autoplay={true}      // 自动播放
      muted={true}         // 静音避免移动端播放限制[1,6](@ref)
      objectFit="cover"    // 填充容器
      className="bg-video" // 自定义样式
      controls={false}     // 隐藏控制条
    />
  );
};

export default VideoBackground;