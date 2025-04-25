import { View, Text } from '@tarojs/components'
import { Input, Button, List, Cell, Tag, Empty, Popup } from '@taroify/core'
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
      url: '/pages/shop/shop',
    });
  };

  const handleSearch = () => {
    console.log("被戳辣啊啊啊啊")
  }

  return (
    <View className='index'>
      <Text onClick={hhh}>Hello world!</Text>
    </View>
  )
}
