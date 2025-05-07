import React from "react";
import clsx from "clsx";
import "./MoodCalendar.scss";
import { View, Text } from '@tarojs/components';

const MONTH = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

// 生成气泡提示的内容，主要就是处理英语就的复数词尾，中文就没这破事。
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
    console.log(contributions)
	const firstDate = new Date(contributions[0].date);
	const startRow = firstDate.getDay();
	const months = [];
	let total = 0;
	let latestMonth = -1;

	const tiles = contributions.map((c, i) => {
		const date = new Date(c.date);
		const month = date.getMonth();
		total += c.count;

		// 在星期天的月份出现变化的列上面显示月份。
		if (date.getDay() === 0 && month !== latestMonth) {
			// 计算月份对应的列，从 1 开始、左上角格子留空所以 +2
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
              
              // 使用过滤去除 null
              {months.filter(Boolean)}
		}
		return (
            <View
                className="tile"
                key={`${i}`}
                data-level={c.level}
                title={getTooltip(c, new Date(c.date))}
            />
		);
	});

	// 第一格不一定是周日，此时前面会有空白，需要设置下起始行。
	tiles[0] = React.cloneElement(tiles[0], {
		style: { gridRow: startRow + 1 },
	});
	// 如果第一格不是周日，则首月可能跑到第二列，需要再检查下。
	if (MONTH[firstDate.getMonth()] === months[0].props.children) {
		months[0].props.style.gridColumn = 2;
	}
	// 第一个月可能跟第二个重叠，此时隐藏第一个。
	if (months[1].props.style.gridColumn - months[0].props.style.gridColumn < 3) {
		months[0] = null;
	}
	// 如果最后一个月在最后一格，则会超出布局范围，故隐藏。
	if (months.at(-1).props.style.gridColumn > 53) {
		months[months.length - 1] = null;
	}

    console.log(months)
    console.log(tiles)

	return (
        <View {...rest} className={clsx("container", className)}>
        {months}
        <Text className="week">Mon</Text>
        <Text className="week">Wed</Text>
        <Text className="week">Fri</Text>
    
        <View className="tiles">{tiles}</View>
    
        <View className="total">
          {total} contributions in the last two months
        </View>
        <View className="legend">
          Less
          <View className="tile" data-level={0}/>
          ...
        </View>
      </View>
	);
}

// 里头需要循环 365 次，耗时 3ms，还是用 memo 包装下吧。
export default ContributionCalendar