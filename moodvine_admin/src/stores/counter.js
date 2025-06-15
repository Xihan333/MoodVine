import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useStore = defineStore('store', () => {
  const count = ref(0)
  const doubleCount = computed(() => count.value * 2)
  const increment = () => {
    count.value++
  }
  // 是否加载中
  const loading=ref(false);
  const userInfo = ref({
    'userId': 0,
    'userType': "",
    'userName': "",
    'token': ""
  })
  const setUserInfo = (t) => {
    userInfo.value = t;
    localStorage.setItem("BOOK_EMPLOYEE",JSON.stringify(userInfo.value))
  }
  //token
  const token = ref();
  // 设置, 清除token
  const setToken = (t) => {
    token.value = t
  }

  // 当前选中的标签页
  const selectedTab=ref('homepage')
  // 已打开的标签页列表, 默认是首页
  const tabPaneList = ref([
    {
      title:'数据统计',
      name:'home',
      close:false,
      role:'ALL'
    }
  ])
  //没有子菜单的菜单
  const itemList = ref([
    {
      title:'数据统计',
      name:'数据统计',
      path: 'home',
      close:false,
      role:'ALL'
    },
    {
      name:'书籍管理',
      path:'Book-manage',
      role:'ALL'
    },
    {
      name:'预售管理',
      path:'presell-manage',
      role:'ALL'
    },
    {
      name:'订单管理',
      path:'order-manage',
      role:'ALL'
    },
    {
      name:'分类管理',
      path:'category-manage',
      role:'ROLE_ADMIN'
    },
    {
      name:'员工管理',
      path:'employee-manage',
      role:'ROLE_ADMIN'
    },
  ])

  const addTab = (item) => {//增加标签页方法
    //查找此界面是否已打开
    if(tabPaneList.value.findIndex(e=>e.name==item.path)===-1){
      tabPaneList.value.push({//添加当前标签页
        title: item.name,
        name: item.path,
        close:true//使除了首页标签页外其他都可关闭
      })
    }
    selectedTab.value=item.path;
  }
  const changeTab = (item) => {//切换标签页方法
       //将需打开标签页路由赋予editableTabsvalue
       selectedTab.value=item.path;
  }
  const updateSelectedTab = (name) => {//更新当前标签页
    selectedTab.value=name;
  }
  const updateTabList = (name) => {//更新标签页列表
    tabPaneList.value=tabPaneList.value.filter(tab=>tab.name!==name);
  }
  const updateLoading = (newValue) => {
    loading.value=newValue;
  }
  const otherInitTab = (title,name) =>{
    selectedTab.value=name;
    tabPaneList.value=[{
      'title':title,
      'name':name,
      'close':false
    }]
  }
  

  return { count, doubleCount, increment,loading, token, setToken, userInfo, setUserInfo, selectedTab, tabPaneList, itemList,
    addTab,changeTab,updateSelectedTab,updateTabList,updateLoading,otherInitTab
  }
})
