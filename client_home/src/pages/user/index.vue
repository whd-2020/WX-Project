<template>
  <view id="page_user" class="page_user" :class="{ 'has-logout': token }">
    <view class="me-header" @click="handleHeaderClick">
      <view class="me-header-avatar">
        <image class="me-avatar" :src="avatarUrl" mode="aspectFill"></image>
      </view>
      <view class="me-header-info">
        <view class="me-name">{{ token ? displayName : '点击登录' }}</view>
        <view class="me-id" v-if="token && userIdValue">ID: {{ userIdValue }}</view>
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

    <view class="me-logout-wrap" v-if="token">
      <view class="me-logout-btn" @click="sign_out">退出登录</view>
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

  export default {
    mixins: [tabbar, mixin],
    data() {
      return {
        tabbarIndex: 0,
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
          { key: 'system_sound', title: '系统音效', icon: 'tn-icon-lock', action: 'nav', url: '/pages/user/sound' },
          { key: 'feedback', title: '意见反馈', icon: 'tn-icon-comment-fill', action: 'toast' },
          { key: 'about', title: '关于我们', icon: 'tn-icon-help', action: 'toast' },
        ],
      };
    },
    computed: {
      // 头像地址：如果是完整 http(s) 链接，直接用；否则走后端资源拼接；都没有时用默认图
      avatarUrl() {
        const avatar = this.userInfo && this.userInfo.avatar;
        if (!avatar) {
          return '/static/img/default.png';
        }
        if (/^https?:\/\//.test(avatar)) {
          return avatar;
        }
        return this.$fullImgUrl(avatar) || '/static/img/default.png';
      },
      // 展示昵称：优先昵称，其次用户名
      displayName() {
        const userInfo = this.userInfo || {};
        return userInfo.nickname || userInfo.username || '游客';
      },
      userIdValue() {
        const userInfo = this.userInfo || {};
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
        this.$store.dispatch('auth/logOut').then((res) => {
        this.$Router.replaceAll('/pagesB/account/login');
        });
      },
      toLogin() {
        this.$Router.replace('/pagesB/account/login');
      },
      handleHeaderClick() {
        if (!this.token) {
          this.toLogin();
        }
      },
      handleEntry(item) {
        if (item && item.needLogin && !this.token) {
          this.toLogin();
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
</style>
