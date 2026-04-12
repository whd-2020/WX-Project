<template>
  <view id="page_user" class="page_user" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <!-- 用户栏模块(开始) -->
    <view class="user">
      <!-- 头像 -->
      <view class="left">
        <view class="avatar">
          <!-- 头像：兼容微信返回的完整 https 链接 -->
          <image :src="avatarUrl"></image>
        </view>
        <view class="base-info" v-if="token">
          <view class="username">{{ displayName }}</view>
          <view class="signature">{{ userInfo.user_group }}</view>
        </view>
        <view class="base-info" v-else @click="toLogin">
          <view class="username">游客</view>
        </view>
      </view>
      <!-- 右侧其它 -->
      <view class="other">
        <view class="user-info-btn" @click="$navTo('/pages/user/info')">
          <text class="iconfont icon-jinggao"></text>
          基本信息
        </view>
      </view>
    </view>
    <!-- 用户栏模块(结束) -->
    <view class="split"></view>
    <!-- 自定义栏模块(开始) -->
    <view class="nav-list">
      <view v-for="(item, index) in cellList" :key="index" v-if="$check_action(item.auth, 'get')">
        <tn-list-cell
            @click="$navTo(item.url)"
            :arrow="true"
        >
          <view class="nav-list-row">
            <image class="menu-img" :src="'/static/img/default.png'"></image>
            <text>{{ item.title }}</text>
          </view>
        </tn-list-cell>
      </view>
    </view>

    <!-- 自定义栏模块(结束) -->

    <!-- 按钮模块(开始) -->
    <view class="user-center-footer">
      <view class="me-btn logout-btn" v-if="token" @click="sign_out">退出登录</view>
      <view v-else class="me-btn login-btn" @click="toLogin">立即登录</view>
    </view>
    <!-- 按钮模块(结束) -->
    
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

  export default {
    mixins: [tabbar],
    data() {
      return {
        tabbarIndex: 0,
        chartAble: false,
        cellList: [
          {
            title: '游戏记录',
            auth: '/game_record/table',
            url: '/pagesC/game_record/table',
            icon: 'icon-yonghu',
          },
          {
            title: '游戏成就',
            auth: '/game_achievements/table',
            url: '/pagesC/game_achievements/table',
            icon: 'icon-yonghu',
          },
          {
            title: '个人成就',
            auth: '/achievements/table',
            url: '/pagesC/achievements/table',
            icon: 'icon-yonghu',
          },
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
    },
    onLoad() {
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
</style>
