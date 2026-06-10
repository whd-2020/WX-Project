<template>
  <view id="home" class="page_home">
	<view class="header_bg"></view>
    <tn-nav-bar fixed :isBack="false" :zIndex="999">
      <view class="nav-wrapper">
        <view class="nav-user" @click="handleUserClick">
          <!-- 头像：优先使用微信头像的完整https地址，其次再走资源拼接 -->
          <image :src="avatarUrl"></image>
        </view>
        <!-- 昵称：优先显示微信昵称，没有则回退到用户名 -->
        <view class="nav-username">
          {{ displayName }}
        </view>
      </view>
    </tn-nav-bar>


    <view class="tab-pane" v-show="tabIndex === 0" :style="{ paddingTop: vuex_custom_bar_height + 'px', paddingBottom: '120rpx' }">
      <!-- 背景图片 -->
      <image class="bg-image" src="/static/img/index/ShouYe.png"></image>
      <!-- 游戏特色卡片(开始) -->
      <view class="game-features">
        <view class="feature-card" @click="handleFeatureClick('/pages/track/rope')">
          <view class="feature-image-wrapper">
            <image class="feature-image" src="/static/img/index/ShengJieJiShu.png"></image>
          </view>
        </view>
        <view class="feature-card" @click="handleFeatureClick('/pages/track/counting_rods')">
          <view class="feature-image-wrapper">
            <image class="feature-image" src="/static/img/index/ChouSuanYanSuan.png"></image>
          </view>
        </view>
        <view class="feature-card" @click="handleFeatureClick('/pages/track/abacus')">
          <view class="feature-image-wrapper">
            <image class="feature-image" src="/static/img/index/ZhuSuanQiMeng.png"></image>
          </view>
        </view>
        <view class="feature-card" @click="handleFeatureClick('/pages/track/comprehensive')">
          <view class="feature-image-wrapper">
            <image class="feature-image" src="/static/img/index/ShuZiRenZhi.png"></image>
          </view>
        </view>
      </view>
      <!-- 游戏特色卡片(结束) -->
      <view class="game-slogan">在游戏中解锁古代算数智慧</view>
	  <view class="home_recommend">
              	  </view>
    </view>
    <!-- 简单的底部导航栏 -->
    <view class="custom-tabbar">
      <view
        v-for="(item, index) in tabbarList"
        :key="index"
        class="tabbar-item"
        :class="{ active: tabbarIndex === index }"
        @click="switchTabbar(index)"
      >
        <image
          class="tabbar-icon"
          :src="tabbarIndex === index ? item.activeIcon : item.inactiveIcon"
        ></image>
        <text class="tabbar-text">{{ item.title }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import mixin from '@/libs/mixins/page.js';
import tabbar from '@/libs/mixins/tabbar.js';
import store from '@/store';

export default {
  mixins: [mixin, tabbar],
  data() {
    return {
      tabIndex: 0,
      tabbarIndex: 0,
    };
  },
  computed: {
    // 直接从store获取用户信息（绕过mixin映射问题）
    currentUserInfo() {
      return store.state.app.userInfo || {};
    },
    currentToken() {
      return store.state.app.token || '';
    },
    currentUserGroup() {
      return store.state.app.userGroup || '游客';
    },
    // 头像地址：如果是完整 http(s) 链接，直接用；否则走后端资源拼接；都没有时用默认图
    avatarUrl() {
      const userInfo = this.currentUserInfo;
      const avatar = userInfo.avatar || userInfo.avatarUrl;
      if (!avatar) {
        return '/static/img/avatar.jpg';
      }
      if (/^https?:\/\//.test(avatar)) {
        return avatar;
      }
      return this.$fullImgUrl(avatar) || '/static/img/avatar.jpg';
    },
    // 展示用昵称：优先昵称，其次用户名（兼容多种字段名）
    displayName() {
      const userInfo = this.currentUserInfo;
      return userInfo.nickname || userInfo.nickName || userInfo.username || '游客';
    },
  },
  created() {
    console.log('首页 created - tabbarList:', this.tabbarList);
    this.initUserInfo();
  },
  methods: {
    async initUserInfo() {
      const token = store.state.app.token;
      if (token) {
        try {
          await store.dispatch('auth/getUserInfo');
          console.log('用户信息获取成功');
        } catch (error) {
          console.log('获取用户信息失败:', error);
        }
      } else {
        console.log('未登录，使用默认用户信息');
      }
    },
    handleUserClick() {
      this.playClickSound();
      this.$navTo('/pages/user/info');
    },
    handleFeatureClick(url) {
      this.playClickSound();
      this.$navTo(url);
    },
  },
};
</script>
<style lang="scss" scoped>
@import 'styles/pages/index.scss';
</style>
