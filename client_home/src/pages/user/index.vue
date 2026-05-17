<template>
  <view id="page_user" class="page_user" :class="{ 'has-logout': currentToken }">
    <view class="me-header" @click="handleHeaderClick">
      <view class="me-header-avatar">
        <image class="me-avatar" :src="avatarUrl" mode="aspectFill"></image>
      </view>
      <view class="me-header-info">
        <view class="me-name">{{ currentToken ? displayName : '点击登录' }}</view>
        <view class="me-id" v-if="currentToken && userIdValue">ID: {{ userIdValue }}</view>
      </view>
    </view>

    <view class="me-card">
      <view class="me-section-title">
        <view class="me-section-mark"></view>
        <text>快捷功能</text>
      </view>
      <view class="me-quick-grid">
        <view
          v-for="item in quickList"
          :key="item.key"
          class="me-quick-item"
          @click="handleEntry(item)"
        >
          <view class="me-quick-icon" :style="{ background: item.bg }">
            <image v-if="item.img" class="me-quick-img" :src="item.img" mode="aspectFit"></image>
            <text v-else :class="['me-quick-icon-text', item.icon]"></text>
          </view>
          <view class="me-quick-text">{{ item.title }}</view>
        </view>
      </view>
    </view>

    <view class="me-card">
      <view class="me-section-title">
        <view class="me-section-mark"></view>
        <text>系统设置</text>
      </view>
      <view class="me-settings">
        <view
          v-for="item in settingList"
          :key="item.key"
          class="me-setting-row"
          @click="handleEntry(item)"
        >
          <view class="me-setting-left">
            <text :class="['me-setting-icon', item.icon]"></text>
            <text class="me-setting-text">{{ item.title }}</text>
          </view>
          <text class="me-setting-arrow tn-icon-right"></text>
        </view>
      </view>
    </view>

    <view class="me-logout-wrap" v-if="currentToken">
      <view class="me-logout-btn" @click="sign_out">退出登录</view>
    </view>

    <!-- 关于我们弹窗 -->
    <view v-if="showAboutModal" class="about-modal-overlay" @click="showAboutModal = false">
      <view class="about-modal" @click.stop>
        <view class="about-modal-close" @click="showAboutModal = false">×</view>
        <view class="about-modal-content">
          <view class="about-title">关于我们</view>
          <view class="about-body">
            <text class="about-text"> 本游戏是一款融合数学启蒙与历史文化的教育类小游戏。</text>
            <text class="about-text">我们带领孩子穿越远古、汉代、明代直至现代，还原结绳、算筹、算盘等传统计数工具，让小朋友在趣味动手操作中理解数字本源，见证并传承中华计算文明的智慧与演变。</text>
          </view>
          <view class="about-footer">
            <text class="footer-text">版本号：V1.0</text>
            <text class="footer-text">版权所有 © 2026 保留所有权利</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 游戏玩法弹窗 -->
    <view v-if="showGamePlayModal" class="gameplay-modal-overlay" @click="showGamePlayModal = false">
      <view class="gameplay-modal" @click.stop>
        <view class="gameplay-modal-close" @click="showGamePlayModal = false">×</view>
        <scroll-view class="gameplay-modal-content" scroll-y>
          <view class="gameplay-title">游戏玩法</view>
          <view class="gameplay-body">
            <view class="gameplay-section">
              <text class="section-title">🎯 游戏目标</text>
              <text class="section-text">通过体验不同历史时期的计数工具，了解数学发展历史，掌握基础数学概念。</text>
            </view>
            <view class="gameplay-section">
              <text class="section-title">🎮 操作方式</text>
              <text class="section-text">1. 点击首页下方快捷功能进入对应游戏关卡</text>
              <text class="section-text">2. 按照游戏内提示完成各项操作任务</text>
              <text class="section-text">3. 通关所有关卡可解锁更多成就和奖励</text>
            </view>
            <view class="gameplay-section">
              <text class="section-title">⭐ 关卡介绍</text>
              <text class="section-text">• 结绳计数：学习远古人类用绳结记事的方法</text>
              <text class="section-text">• 筹算演算：体验古代算筹计数与计算</text>
              <text class="section-text">• 珠算启蒙：认识算盘并学习基础拨珠方法</text>
              <text class="section-text">• 数字认知：综合练习与数字概念巩固</text>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>

    <!-- 意见反馈弹窗 -->
    <view v-if="showFeedbackModal" class="feedback-modal-overlay" @click="showFeedbackModal = false">
      <view class="feedback-modal" @click.stop>
        <view class="feedback-modal-close" @click="showFeedbackModal = false">×</view>
        <view class="feedback-modal-content">
          <view class="feedback-title">意见反馈</view>
          <view class="feedback-body">
            <text class="feedback-text">感谢您的使用！如果在使用过程中遇到问题，或者有任何关于内容、玩法的建议，欢迎通过以下方式告诉我们：</text>
            <view class="feedback-email">
              <text class="email-label">📧 反馈邮箱：</text>
              <text class="email-address" selectable="true">support@gushuxinyu.com</text>
            </view>
            <text class="feedback-text feedback-note">我们会认真查看每一条反馈，持续优化产品</text>
          </view>
        </view>
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
  import tabbar from '@/libs/mixins/tabbar.js';
  import mixin from '@/libs/mixins/page.js';
  import store from '@/store';
  import AudioManager from '@/utils/audio-manager.js';

  export default {
    mixins: [tabbar, mixin],
    data() {
      return {
        tabbarIndex: 0,
        showAboutModal: false,
        showGamePlayModal: false,
        showFeedbackModal: false,
        quickList: [
          {
            key: 'track_rope',
            title: '结绳计数',
            img: '/static/img/rope/ShengZi.png',
            bg: '#BFD7FF',
            action: 'nav',
            url: '/pages/track/rope',
          },
          {
            key: 'track_counting_rods',
            title: '筹算演算',
            img: '/static/images/games/basic_counting_rods.png',
            bg: '#BFE8C9',
            action: 'nav',
            url: '/pages/track/counting_rods',
          },
          {
            key: 'track_abacus',
            title: '珠算启蒙',
            img: '/static/img/index/ZhuSuanQiMeng.png',
            bg: '#D2F2F2',
            action: 'nav',
            url: '/pages/track/abacus',
          },
          {
            key: 'track_comprehensive',
            title: '数字认知',
            img: '/static/images/games/level_challenge.png',
            bg: '#BFE7FF',
            action: 'nav',
            url: '/pages/track/comprehensive',
          },
        ],
        settingList: [
          { key: 'my_info', title: '我的信息', icon: 'tn-icon-identity', action: 'nav', url: '/pages/user/info', needLogin: true },
          { key: 'system_sound', title: '系统音效', icon: 'tn-icon-sound', action: 'nav', url: '/pages/user/sound' },
          { key: 'game_play', title: '游戏玩法', icon: 'tn-icon-play', action: 'gamePlay', needLogin: true },
          { key: 'feedback', title: '意见反馈', icon: 'tn-icon-comment-fill', action: 'feedback' },
          { key: 'about', title: '关于我们', icon: 'tn-icon-help', action: 'about' },
        ],
      };
    },
    computed: {
      // 直接从store获取状态
      currentToken() {
        return store.state.app.token || '';
      },
      currentUserInfo() {
        return store.state.app.userInfo || {};
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
      // 展示昵称：优先昵称，其次用户名
      displayName() {
        const userInfo = this.currentUserInfo;
        return userInfo.nickname || userInfo.nickName || userInfo.username || '游客';
      },
      userIdValue() {
        const userInfo = this.currentUserInfo;
        return (
          userInfo.user_id ||
          userInfo.id ||
          userInfo.uid ||
          userInfo.userId ||
          userInfo.userid ||
          ''
        );
      },
    },
    methods: {
      sign_out() {
        this.playClickSound();
        this.$store.dispatch('auth/logOut').then((res) => {
        this.$Router.replaceAll('/pagesB/account/login');
        });
      },
      toLogin() {
        this.playClickSound();
        this.$Router.replace('/pagesB/account/login');
      },
      handleHeaderClick() {
        this.playClickSound();
        if (!store.state.app.token) {
          this.toLogin();
        }
      },
      handleEntry(item) {
        console.log('handleEntry item:', item);
        if (item.key === 'system_sound') {
          AudioManager.playClickSoundForce();
        } else {
          this.playClickSound();
        }
        if (item && item.needLogin && !store.state.app.token) {
          this.toLogin();
          return;
        }
        if (item.key === 'about') {
          this.showAboutModal = true;
          return;
        }
        if (item.key === 'game_play') {
          this.showGamePlayModal = true;
          return;
        }
        if (item.key === 'feedback') {
          this.showFeedbackModal = true;
          return;
        }
        if (item && item.action === 'nav' && item.url) {
          this.$navTo(item.url);
          return;
        }
        this.$toast('暂未开放');
      },
    },
    mounted() {
      for (let i = 0;i<this.tabbarList.length;i++){
        if (this.tabbarList[i].pagePath === "/pages/user/index") {
          this.tabbarIndex = i;
        }
      }
    }
  };
</script>

<style lang="scss" scoped>
  @import 'styles/pages/index.scss';

  .page_user {
    min-height: 100vh;
    background: #f6f7fb;
    padding-bottom: 140rpx;
    box-sizing: border-box;
  }

  .page_user.has-logout {
    padding-bottom: 260rpx;
  }

  .me-header {
    background: #ffffff;
    padding: 20rpx 24rpx;
    display: flex;
    align-items: center;
  }

  .me-header-avatar {
    width: 96rpx;
    height: 96rpx;
    margin-right: 20rpx;
  }

  .me-avatar {
    width: 96rpx;
    height: 96rpx;
    border-radius: 48rpx;
    background: #f0f0f0;
  }

  .me-header-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }

  .me-name {
    font-size: 38rpx;
    font-weight: 600;
    color: #111111;
    line-height: 1.2;
  }

  .me-id {
    margin-top: 12rpx;
    font-size: 24rpx;
    color: #8d8d8d;
    line-height: 1.2;
  }

  .me-card {
    background: #ffffff;
    margin: 12rpx 20rpx 0;
    border-radius: 20rpx;
    padding: 18rpx 18rpx 6rpx;
    box-sizing: border-box;
  }

  .me-section-title {
    display: flex;
    align-items: center;
    font-size: 30rpx;
    font-weight: 600;
    color: #222222;
    padding: 0 0 12rpx;
  }

  .me-section-mark {
    width: 8rpx;
    height: 28rpx;
    background: #2f80ff;
    border-radius: 4rpx;
    margin-right: 14rpx;
  }

  .me-quick-grid {
    display: flex;
    flex-direction: row;
    align-items: flex-start;
    justify-content: space-between;
    padding: 4rpx 0 8rpx;
  }

  .me-quick-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 10rpx 0;
  }

  .me-quick-icon {
    width: 92rpx;
    height: 92rpx;
    border-radius: 46rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 12rpx;
  }

  .me-quick-img {
    width: 58rpx;
    height: 58rpx;
  }

  .me-quick-icon-text {
    font-size: 44rpx;
    color: #ffffff;
    line-height: 1;
  }

  .me-quick-text {
    font-size: 24rpx;
    color: #333333;
    line-height: 1.2;
  }

  .me-settings {
    display: flex;
    flex-direction: column;
  }

  .me-setting-row {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    padding: 22rpx 0;
  }

  .me-setting-row + .me-setting-row {
    border-top: 1rpx solid #f0f0f0;
  }

  .me-setting-left {
    display: flex;
    flex-direction: row;
    align-items: center;
  }

  .me-setting-icon {
    font-size: 34rpx;
    color: #2f80ff;
    margin-right: 16rpx;
    line-height: 1;
  }

  .me-setting-text {
    font-size: 30rpx;
    color: #222222;
  }

  .me-setting-arrow {
    font-size: 30rpx;
    color: #c0c0c0;
    line-height: 1;
  }

  .me-logout-wrap {
    position: fixed;
    left: 20rpx;
    right: 20rpx;
    bottom: calc(120rpx + env(safe-area-inset-bottom, 0px) + 16rpx);
    z-index: 10001;
  }

  .me-logout-btn {
    height: 88rpx;
    border-radius: 44rpx;
    background: #ff3b30;
    color: #ffffff;
    font-size: 32rpx;
    font-weight: 600;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .about-modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 9999;
    display: flex;
    align-items: flex-start;
    justify-content: center;
    padding-top: 100rpx;
    padding-bottom: 240rpx;
    box-sizing: border-box;
  }

  .about-modal {
    width: 88%;
    max-height: 80vh;
    background: linear-gradient(180deg, #ffffff 0%, #f8f9fa 100%);
    border-radius: 24rpx;
    position: relative;
    overflow: hidden;
    box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.15);
  }

  .about-modal-close {
    position: absolute;
    top: 20rpx;
    right: 20rpx;
    width: 60rpx;
    height: 60rpx;
    line-height: 56rpx;
    text-align: center;
    font-size: 48rpx;
    color: #999;
    z-index: 10;
  }

  .about-modal-content {
    padding: 50rpx 40rpx 70rpx;
  }

  .about-title {
    text-align: center;
    font-size: 40rpx;
    font-weight: 700;
    color: #2c3e50;
    margin-bottom: 40rpx;
    position: relative;
    padding-bottom: 20rpx;
  }

  .about-title::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 80rpx;
    height: 4rpx;
    background: linear-gradient(90deg, #2f80ff, #6a9eff);
    border-radius: 2rpx;
  }

  .about-body {
    display: flex;
    flex-direction: column;
  }

  .about-text {
    font-size: 30rpx;
    color: #5a6c7d;
    line-height: 1.8;
    margin-bottom: 24rpx;
    text-align: justify;
    text-indent: 60rpx;
    letter-spacing: 1rpx;
  }

  .about-footer {
    margin-top: 30rpx;
    padding-top: 30rpx;
    border-top: 1rpx solid #e8eaed;
    display: flex;
    flex-direction: column;
    align-items: center;
    background: rgba(47, 128, 255, 0.03);
    border-radius: 0 0 24rpx 24rpx;
  }

  .footer-text {
    font-size: 26rpx;
    color: #8a9cad;
    text-align: center;
    margin-bottom: 10rpx;
  }

  .gameplay-modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 9999;
    display: flex;
    align-items: flex-start;
    justify-content: center;
    padding-top: 120rpx;
    padding-bottom: 240rpx;
    box-sizing: border-box;
  }

  .gameplay-modal {
    width: 88%;
    max-height: calc(100vh - 360rpx);
    background: linear-gradient(180deg, #ffffff 0%, #f8f9fa 100%);
    border-radius: 24rpx;
    position: relative;
    overflow: hidden;
    box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.15);
    display: flex;
    flex-direction: column;
  }

  .gameplay-modal-close {
    position: absolute;
    top: 20rpx;
    right: 20rpx;
    width: 60rpx;
    height: 60rpx;
    line-height: 56rpx;
    text-align: center;
    font-size: 48rpx;
    color: #999;
    z-index: 10;
  }

  .gameplay-modal-content {
    padding: 50rpx 40rpx;
    height: 900rpx;
  }

  .gameplay-title {
    text-align: center;
    font-size: 40rpx;
    font-weight: 700;
    color: #2c3e50;
    margin-bottom: 30rpx;
    position: relative;
    padding-bottom: 20rpx;
  }

  .gameplay-title::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 80rpx;
    height: 4rpx;
    background: linear-gradient(90deg, #2f80ff, #6a9eff);
    border-radius: 2rpx;
  }

  .gameplay-body {
    padding-top: 10rpx;
  }

  .gameplay-section {
    display: flex;
    flex-direction: column;
    margin-bottom: 30rpx;
    
    &:last-child {
      margin-bottom: 0;
    }
  }

  .section-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #2f80ff;
    margin-bottom: 16rpx;
  }

  .section-text {
    font-size: 28rpx;
    color: #5a6c7d;
    line-height: 1.8;
    margin-bottom: 8rpx;
    text-align: justify;
  }

  .feedback-modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 9999;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .feedback-modal {
    width: 88%;
    background: linear-gradient(180deg, #ffffff 0%, #f8f9fa 100%);
    border-radius: 24rpx;
    position: relative;
    overflow: hidden;
    box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.15);
  }

  .feedback-modal-close {
    position: absolute;
    top: 20rpx;
    right: 20rpx;
    width: 60rpx;
    height: 60rpx;
    line-height: 56rpx;
    text-align: center;
    font-size: 48rpx;
    color: #999;
    z-index: 10;
  }

  .feedback-modal-content {
    padding: 50rpx 40rpx 60rpx;
  }

  .feedback-title {
    text-align: center;
    font-size: 40rpx;
    font-weight: 700;
    color: #2c3e50;
    margin-bottom: 40rpx;
    position: relative;
    padding-bottom: 20rpx;
  }

  .feedback-title::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 80rpx;
    height: 4rpx;
    background: linear-gradient(90deg, #2f80ff, #6a9eff);
    border-radius: 2rpx;
  }

  .feedback-body {
    display: flex;
    flex-direction: column;
  }

  .feedback-text {
    font-size: 30rpx;
    color: #5a6c7d;
    line-height: 1.8;
    margin-bottom: 30rpx;
    text-align: justify;
  }

  .feedback-email {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 30rpx;
    background: rgba(47, 128, 255, 0.08);
    border-radius: 16rpx;
    margin-bottom: 30rpx;
  }

  .email-label {
    font-size: 28rpx;
    color: #5a6c7d;
    margin-bottom: 12rpx;
  }

  .email-address {
    font-size: 34rpx;
    font-weight: 600;
    color: #2f80ff;
  }

  .feedback-note {
    text-align: center;
    color: #7f8c8d;
    font-style: italic;
    margin-bottom: 0;
  }
</style>
