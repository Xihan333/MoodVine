import React from "react";
import clsx from "clsx";
import "./MoodCalendar.scss";
import { View, Text } from '@tarojs/components';

const MONTH = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

// 生成日期范围过滤器（关键修改点[7,8]）
function getRecentFiveMonthsRange() {
  const today = new Date();
  const cutoffDate = new Date(today);
  cutoffDate.setMonth(cutoffDate.getMonth() - 4); // 改为获取五个月前的第一天[6,7](@ref)
  cutoffDate.setDate(1);
  return cutoffDate;
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
  // 过滤最近五个月数据（核心修改[3,6]）
  const cutoffDate = getRecentFiveMonthsRange();
  const filteredContributions = contributions.filter(c => {
    const itemDate = new Date(c.date);
    return itemDate >= cutoffDate;
  });

  const firstDate = filteredContributions.length > 0 
    ? new Date(filteredContributions[0].date)
    : new Date();
  const startRow = firstDate.getDay();
  const months = [];
  let total = 0;
  let latestMonth = -1;

  const tiles = filteredContributions.map((c, i) => {
    const date = new Date(c.date);
    const month = date.getMonth();
    total += c.count;
	console.log(c.level)

    if (date.getDay() === 0 && month !== latestMonth) {
      const gridColumn = 2 + Math.floor((i + startRow) / 7);
      latestMonth = month;
      months.push(
        MONTH[month] ? (
          <Text 
            className="month" 
            key={`month-${i}-${month}`} 
            style={{ gridColumn }}
          >
            {MONTH[month]}
          </Text>
        ) : null
      );
    };
	const level_color = [ `#f0f0f0`,  `#9BE9A8`,   `#40C463`,   `#30A14E`,   `#216E39`,  `#0F532D` ];
    return (
      <View
        className="tile"
        key={`${i}`}
        data-level={c.level}
        title={getTooltip(c, new Date(c.date))}
		style={{ 
			'--tile-color': level_color[c.level],
  		}}
      />
    );
  });

  // 调整首月显示逻辑（适配五个月显示[4]）
  if (tiles.length > 0) {
    tiles[0] = React.cloneElement(tiles[0], {
      style: { gridRow: startRow + 1 },
    });
    
    // 清理多余月份标签
    const visibleMonths = new Set(filteredContributions
      .map(c => new Date(c.date).getMonth())
      .filter((v, i, a) => a.indexOf(v) === i)
    );
    months.forEach((month, index) => {
      if (month && !visibleMonths.has(new Date(filteredContributions[index].date).getMonth())) {
        months[index] = null;
      }
    });
  }

  return (
    <View {...rest} className={clsx("container", className)}>
      {months.filter(Boolean)}
      <Text className="week">Mon</Text>
      <Text className="week">Wed</Text>
      <Text className="week">Fri</Text>
    
      <View className="tiles">{tiles}</View>
    
      <View className="legend">
        Less
        <View className="tile"/>
        ...
      </View>
    </View>
  );
}

export default React.memo(ContributionCalendar);