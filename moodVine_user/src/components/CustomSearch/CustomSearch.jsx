import { View, Input, Image } from '@tarojs/components';
import Taro from '@tarojs/taro';
import { useState } from 'react';
import './CustomSearch.scss';
import { Search, Close } from "@taroify/icons"

const CustomSearch = ({ onSearch }) => {
  const [searchValue, setSearchValue] = useState('');
  const [showClear, setShowClear] = useState(false);

  // 处理搜索逻辑
  const handleSearch = () => {
    if (searchValue.trim()) {
      onSearch(searchValue);
      // 收起键盘
      Taro.hideKeyboard();
    }
  };

  // 清除输入
  const handleClear = () => {
    setSearchValue('');
    setShowClear(false);
    onSearch('');
  };

  // 输入改变时处理
  const handleChange = (e) => {
    const value = e.detail.value;
    setSearchValue(value);
    setShowClear(!!value);
  };

  return (
    <View className="search-container">
      {/* 搜索框主体 */}
      <View className="search-box">
        
        {/* 输入区域 */}
        <Input
          className="search-input"
          type="text"
          placeholder="搜索我的小纸条"
          placeholderClass="placeholder"
          value={searchValue}
          onInput={handleChange}
          onConfirm={handleSearch}
          confirmType="search"
        />
        
        {/* 清除按钮 - 只在有内容时显示 */}
        {showClear && (
          <View className="clear-btn" onClick={handleClear}>
            <Close />
          </View>
        )}
        
        {/* 搜索按钮 */}
        <View className="search-btn" onClick={handleSearch}>
            <Search style={{ color: "#9E8568" }} size="20" className="magnifier" mode="aspectFit" />
        </View>
      </View>
    </View>
  );
};

export default CustomSearch;