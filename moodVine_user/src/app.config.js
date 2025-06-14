export default defineAppConfig({
  // 在这里进行路由配置
  pages: [
    'pages/index/index',
    'pages/score/score',
    'pages/shop/shop',
    'pages/activityList/activityList',
    'pages/test/test',
    'pages/activityDetail/activityDetail',
    'pages/diaryEditor/diaryEditor',
    'pages/diaryList/diaryList',
    'pages/people/people',
    'pages/moodPage/moodPage',
    'pages/vinePage/vinePage',
    'pages/moreScrips/moreScrips',
    'pages/scripDetail/scripDetail',
    'pages/chatAI/chatAI'
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
        // pagePath: 'pages/score/score',
        pagePath: 'pages/vinePage/vinePage',
        text: '藤蔓',
        iconPath: './assets/vine.png',
        selectedIconPath: './assets/vine_fill.png',
      },
      {
        pagePath: 'pages/diaryEditor/diaryEditor',
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
