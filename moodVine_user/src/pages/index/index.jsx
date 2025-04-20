import { View, Text } from '@tarojs/components'
import { useLoad } from '@tarojs/taro'
import Taro from '@tarojs/taro';
import 'normalize.css'
import './index.scss'

export default function Index() {

  useLoad(() => {
    console.log('Page loaded.')
  })

  const hhh = () => {
    Taro.navigateTo({
      url: '/pages/score/score',
    });
  };

  return (
    <View className='index'>
      <Text onClick={hhh}>Hello world!</Text>
    </View>
  )
}
