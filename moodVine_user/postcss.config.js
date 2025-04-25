module.exports = {
  plugins: [
    require('postcss-px-to-viewport')({
      viewportWidth: 780, // 视口宽度，默认值
      viewportHeight: 1688, // 可选，视口高度，通常不需要设置
      unitPrecision: 5, // 单位转换后的精度
      propList: ['*'], // 转换属性列表，'*'表示全部
      viewportUnit: 'vw', // 转换的目标单位
      fontViewportUnit: 'vw', // 字体使用的视口单位
      selectorBlackList: [], // 忽略某些选择器
      minPixelValue: 1, // 小于该值的不会被转换
      mediaQuery: true, // 是否在媒体查询中也进行转换
      replace: true, // 直接替换原有规则而非添加备份
      exclude: [/node_modules/], // 排除特定路径或文件夹
      include: undefined, // 仅转换指定路径内的文件，可选
      landscape: false, // 是否增加横屏样式
      landscapeUnit: 'vw', // 横屏转换单位
    //   landscapeWidth: window.innerWidth > window.innerHeight ? window.innerWidth : window.innerHeight, // 横屏宽度
    }),
  ],
};