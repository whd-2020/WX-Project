import { mapState } from 'vuex';
export default {
  data() {
    return {
      // 底部tabbar菜单数据
      tabbarList: [
        {
          title: '首页',
          activeIcon: '/static/images/tabbar/index-active.png',
          inactiveIcon: '/static/images/tabbar/index.png',
          pagePath: '/pages/index/index',
        },
        {
          title: '我的',
          activeIcon: '/static/images/tabbar/user-active.png',
          inactiveIcon: '/static/images/tabbar/user.png',
          pagePath: '/pages/user/index',
        },
      ],
    };
  },
  methods: {
    // 切换导航
    switchTabbar(index) {
      console.log('👆 用户点击了底部导航栏，准备播放点击音效...');
      this.playClickSound();
      
      const menu = this.tabbarList[index];
      if (menu.pagePath !== this.$Router.currentRoute.path) {
        this.$Router.pushTab(menu.pagePath);
      }
      setTimeout(() => {
        this._switchTabbarPage(index);
      }, 0);
    },

    // 导航页面滚动到底部
    tabbarPageScrollLower(e) {},

    // 切换导航页面
    _switchTabbarPage(index) {},
  },

  onLoad(options) {
    uni.hideTabBar();
  },

  onShow() {},

  onHide() {},
};
