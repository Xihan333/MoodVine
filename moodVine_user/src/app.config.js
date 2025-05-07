export default defineAppConfig({
  // 在这里进行路由配置
  pages: [
    'pages/index/index',
    'pages/score/score',
    'pages/shop/shop',
    'pages/activity/activity',
    'pages/test/test',
    'pages/people/people'
  ],
  window: {
    backgroundTextStyle: 'light',
    navigationBarBackgroundColor: '#F8ECDF',
    navigationBarTitleText: 'WeChat',
    navigationBarTextStyle: 'black'
  },
  // 导航栏
  tabBar: {
    color: '#2c2c2c', // 未选中文字颜色
    selectedColor: '#2c2c2c', // 选中文字颜色
    backgroundColor: '#F8ECDF', // 背景颜色
    borderStyle: 'white', // 边框颜色（仅支持 black/white）
    list: [
      {
        pagePath: 'pages/index/index',
        text: '首页',
        iconPath: './assets/homepage.png',
        selectedIconPath: './assets/homepage_fill.png', // 选中图标
      },
      {
        pagePath: 'pages/score/score',
        text: '蜜罐',
        iconPath: './assets/coupons.png',
        selectedIconPath: './assets/coupons_fill.png',
      },
      {
        pagePath: 'pages/activity/activity',
        text: '活动',
        iconPath: './assets/activity.png',
        selectedIconPath: './assets/activity_fill.png',
      },
      {
        pagePath: 'pages/people/people',
        text: '我的',
        iconPath: './assets/people.png',
        selectedIconPath: './assets/people_fill.png',
      },
    ],
  },
})
