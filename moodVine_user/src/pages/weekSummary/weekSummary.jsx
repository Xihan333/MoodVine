import { View, Text, Image } from '@tarojs/components'
import { useState } from 'react'
import { useLoad } from '@tarojs/taro'
import request from '../../utils/request'
import weekbg from '../../assets/weekbg.png'
import './weekSummary.scss'

const moodMap = {
  1: '充满能量',
  2: '平静安宁',
  3: '悲伤低落',
  4: '紧张害怕',
  5: '愤怒生气'
}

const WeekSummary = () => {
  const [analysis, setAnalysis] = useState({
    activityClockIn: 0,
    diaryCount: 0,
    scripCount: 0,
    tabs: [],
    mostFrequentMood: 1
  })

  const getAnalysis = async() => {
    const res = await request.get('/user/getWeeklyStatistics')
        console.log(res.data)
    if (res.data.code == 200) {
      setAnalysis(res.data.data)
    }
  }

  useLoad(() => {
    getAnalysis()
  })

  // 处理标签显示
  const renderTags = () => {
    if (!analysis.tabs || analysis.tabs.length === 0) {
      return "无标签"
    }

    const ans = analysis.tabs.map((item) => item.content)
    return ans.join('，')
  }

  return (
    <View className="week-summary-page">
      {/* 全屏背景图 */}
      <Image 
        className="background-image" 
        src={weekbg} 
        mode="aspectFill"
      />
      
      {/* 居中内容容器 */}
      <View className="content-container">
        <Text className="title">本周总结</Text>
        
        <View className="summary-row">
          <Text>主题周打卡次数：</Text>
          <Text className="value">{analysis.activityClockIn}次</Text>
        </View>
        
        <View className="summary-row">
          <Text>记愈次数：</Text>
          <Text className="value">{analysis.diaryCount}次</Text>
        </View>
        
        <View className="summary-row">
          <Text>聊愈次数：</Text>
          <Text className="value">{analysis.scripCount}次</Text>
        </View>
        
        <View className="summary-row">
          <Text>主要情绪：</Text>
          <Text className="value">{moodMap[analysis.mostFrequentMood]}</Text>
        </View>
        
        <View className="summary-row">
          <Text>获得标签：</Text>
          <Text className="value">{renderTags()}</Text>
        </View>
      </View>
    </View>
  )
}

export default WeekSummary