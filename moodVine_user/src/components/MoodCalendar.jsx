import React from "react";
import clsx from "clsx";
import "./MoodCalendar.scss";
import { View, Text } from '@tarojs/components';

const MONTH = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

// 获取最近五个月的第一天到今天的所有日期范围
function getRecentFiveMonthsRange() {
  const endDate = new Date();
  const startDate = new Date();
  startDate.setMonth(endDate.getMonth() - 4);
  startDate.setDate(1);
  return { startDate, endDate };
}

// 生成日期范围的完整日期数组
function generateDateRange(startDate, endDate) {
  const dates = [];
  const current = new Date(startDate);
  
  while (current <= endDate) {
    const dateStr = [
      current.getFullYear(),
      String(current.getMonth() + 1).padStart(2, '0'),
      String(current.getDate()).padStart(2, '0')
    ].join('-');
    
    dates.push(dateStr);
    current.setDate(current.getDate() + 1);
  }
  
  return dates;
}

// 创建填充数据（无数据的日期设为level:0）
function generateFilledContributions(dates, contributions) {
  const contributionMap = {};
  contributions.forEach(c => {
    const date = new Date(c.date);
    const dateStr = [
      date.getFullYear(),
      String(date.getMonth() + 1).padStart(2, '0'),
      String(date.getDate()).padStart(2, '0')
    ].join('-');
    
    contributionMap[dateStr] = c;
  });
  
  return dates.map(dateStr => {
    if (contributionMap[dateStr]) {
      return contributionMap[dateStr];
    } else {
      return {
        date: dateStr,
        count: 0,
        level: 0
      };
    }
  });
}

function getTooltip(oneDay, date) {
  const s = date.toISOString().split("T")[0];
  switch (oneDay.count) {
    case 0:
      return `No contributions on ${s}`;
    case 1:
      return `1 contribution on ${s}`;
    default:
      return `${oneDay.count} contributions on ${s}`;
  }
}

function ContributionCalendar({ contributions, className, ...rest }) {
  // 获取最近五个月日期范围
  const { startDate, endDate } = getRecentFiveMonthsRange();
  
  // 生成所有日期
  const allDates = generateDateRange(
    new Date(startDate), 
    new Date(endDate)
  );
  
  // 生成完整贡献数据（包含填充0值）
  const filledContributions = generateFilledContributions(
    allDates, 
    contributions
  );
  
  // 渲染起点设置（使用第一个填充日期的星期数）
  const firstDate = new Date(filledContributions[0].date);
  const startRow = firstDate.getDay();
  
  // 准备渲染元素
  const months = [];
  let latestMonth = -1;
  let tiles = [];

  filledContributions.forEach((c, i) => {
    const date = new Date(c.date);
    const month = date.getMonth();
    
    // 月份标签逻辑（每月的第一个星期日显示标签）
    if (date.getDay() === 0) {
      if (month !== latestMonth) {
        const gridColumn = 2 + Math.floor(i / 7);
        latestMonth = month;
        months.push(
          <Text 
            className="month" 
            key={`month-${i}-${month}`} 
            style={{ gridColumn }}
          >
            {MONTH[month]}
          </Text>
        );
      }
    }
    
    // 创建心情方格
    const level_color = [ 
      `#f0f0f0`, // level0 - 无数据
      `#9BE9A8`, // level1
      `#40C463`, // level2
      `#30A14E`, // level3
      `#216E39`, // level4
      `#0F532D`  // level5
    ];
    
    tiles.push(
      <View
        className="tile"
        key={`${c.date}`}
        data-level={c.level}
        title={getTooltip(c, date)}
        style={{ 
          '--tile-color': level_color[c.level],
        }}
      />
    );
  });

  // 应用起始行偏移
  if (tiles.length > 0) {
    tiles = tiles.map((tile, i) => {
      if (i === 0) {
        return React.cloneElement(tile, {
          style: { 
            ...tile.props.style,
            gridRow: startRow + 1
          },
        });
      }
      return tile;
    });
  }

  return (
    <View {...rest} className={clsx("container", className)}>
      {months}
      <Text className="week">Mon</Text>
      <Text className="week">Wed</Text>
      <Text className="week">Fri</Text>
    
      <View className="tiles">{tiles}</View>
    
    </View>
  );
}

export default React.memo(ContributionCalendar);