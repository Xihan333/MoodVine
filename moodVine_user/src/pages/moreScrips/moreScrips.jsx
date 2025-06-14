import { View, Text, Image } from '@tarojs/components'
import Taro from '@tarojs/taro'
import { useState, useEffect } from 'react'
import { useLoad } from '@tarojs/taro'

import { Search, Toast, Empty } from "@taroify/core"
import CustomSearch from '../../components/CustomSearch/CustomSearch'

import bg1 from '../../assets/moodpaper/blue.png'    
import bg2 from '../../assets/moodpaper/green.png'   
import bg3 from '../../assets/moodpaper/pink.png'    
import bg4 from '../../assets/moodpaper/purple.png' 

import 'normalize.css'
import './moreScrips.scss'
import request from '../../utils/request'
import { setScrip } from '../../store/features/scripSlice';
import { useSelector, useDispatch } from 'react-redux';

const Scrips = ({ data }) => {

   // 定义固定顺序的四张背景图
  const backgroundImages = [bg1, bg2, bg3, bg4];
  
  // 心情图标的映射关系（保持不变）
  const moodIcons = {
    1: require('../../assets/moodpaper/power.png'),
    2: require('../../assets/moodpaper/peace.png'),
    3: require('../../assets/moodpaper/sad.png'),
    4: require('../../assets/moodpaper/scared.png'),
    5: require('../../assets/moodpaper/mad.png'),
  };

  if (!data || data.length === 0) {
    return (
      <Empty className="empty-result">
        <Empty.Image src="search" />
        <Empty.Description>未找到匹配的纸条</Empty.Description>
      </Empty>
    )
  }

  const dispatch = useDispatch();
  const handleDetail = (item) => {
      dispatch(setScrip(item));
      Taro.navigateTo({
        url: '/pages/scripDetail/scripDetail',
      });
    };

  const itemList = data.map((item,index) => {
    const bgIndex = index % 4;
    const background = backgroundImages[bgIndex];

    // 获取对应心情图标
    const moodIcon = moodIcons[item.mood];

    return (
      <View 
        key={index} 
        className="scrip-card"
        style={{ backgroundImage: `url(${background})` }}
        onClick={()=>handleDetail(item)}
      >
        <Image className='mood' src={moodIcon} />
        <Text className="sentence">{item.sentence}</Text>
        <Text className='time'>{ item.time }</Text>
      </View>
    );
});

  return (
      <View className='scrip-list'>
        {itemList}
      </View>
    );
}

const MoreScrips = () => {
  const [allData,setScripData] = useState([
        { 'id': 1, 'mood': 1, 'sentence': '明天会更好', 'time': '5-29' },
        { 'id': 1, 'mood': 2, 'sentence': '今天天气不错，适合去公园散步', 'time': '5-30' },
        { 'id': 1, 'mood': 3, 'sentence': '最近工作压力有点大', 'time': '5-31' },
        { 'id': 1, 'mood': 4, 'sentence': '完成了一个大项目，很有成就感', 'time': '6-01' },
        { 'id': 1, 'mood': 5, 'sentence': '需要学习更多新知识', 'time': '6-02' },
        { 'id': 1, 'mood': 1, 'sentence': '和家人一起吃了美味的晚餐', 'time': '6-03' },
        { 'id': 1, 'mood': 2, 'sentence': '读了一本很好的书', 'time': '6-04' },
        { 'id': 1, 'mood': 3, 'sentence': '计划周末去爬山', 'time': '6-05' },
  ]);

  const fetchAllData = async() => {
    const scripRes = await request.get('/user/scrip/getAllScrips')
    // 处理纸条数据
      if (scripRes.data?.code === 200) {
        setScripData(Array.isArray(scripRes.data.data) 
          ? scripRes.data.data 
          : []);
      } else {
        setScripData([]);
        console.warn('纸条数据异常:', scripRes);
      }
  }

  useLoad(() => {
    fetchAllData();
  });
  
  // 过滤后的数据
  const [filteredData, setFilteredData] = useState(allData);
  
  // 初始化显示所有数据
  useEffect(() => {
    setFilteredData(allData);
  }, [allData]);
  
  // 搜索函数
  const handleSearch = (keyword) => {
    if (!keyword) {
      setFilteredData(allData);
      return;
    }
    
    const searchKeyword = keyword.toLowerCase().trim();
    
    const results = allData.filter(item => {
      // 搜索内容包括句子、心情和时间
      return (
        item.sentence.toLowerCase().includes(searchKeyword) ||
        (item.time && item.time.toLowerCase().includes(searchKeyword)) ||
        (getMoodText(item.mood) && getMoodText(item.mood).includes(searchKeyword))
      );
    });
    
    setFilteredData(results);
  };
  
  // 获取心情文本描述
  const getMoodText = (mood) => {
    const moodTexts = {
      1: '充满能量',
      2: '平静安宁',
      3: '悲伤低落',
      4: '紧张害怕',
      5: '愤怒生气'
    };
    
    return moodTexts[mood] || '';
  };

  return (
    <View className='more-scrips'>
      <CustomSearch onSearch={handleSearch}/>
      <Scrips data={filteredData}/>
    </View>
  );
};

export default MoreScrips;