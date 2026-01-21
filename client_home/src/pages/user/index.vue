<template>
  <view id="page_user" class="page_user" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <tn-nav-bar :isBack="false" :bottomShadow="false">个人中心</tn-nav-bar>
    <!-- 用户栏模块(开始) -->
    <view class="user">
      <!-- 头像 -->
      <view class="left">
        <view class="avatar">
          <image :src="$fullImgUrl(userInfo.avatar) || '/static/img/default.png'"></image>
        </view>
        <view class="base-info" v-if="token">
          <view class="username">{{ userInfo.nickname }}</view>
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
    <view class="menu-group my-data">
      <view class="menu-row-sm">
        <view v-if="$check_action('/comment/table', 'get')" class="menu-item comment" @click="$navTo('/pages/user/comment')">
          <image class="menu-img" :src="'/static/img/default.png'"></image>
          <view class="name">评论</view>
        </view>
        <view class="menu-item data" v-if="chartAble" @click="$navTo('/pagesA/user_center/index')">
          <image class="menu-img" :src="'/static/img/default.png'"></image>
          <view class="name">统计</view>
        </view>
      </view>
    </view>

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
    <tn-tabbar
        :value="tabbarIndex"
        :list="tabbarList"
        @change="switchTabbar"
        :animation="true"
        :safeAreaInsetBottom="true"
    ></tn-tabbar>
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
            title: '游戏玩家',
            auth: '/gamer/table',
            url: '/pagesC/gamer/table',
            icon: 'icon-yonghu',
          },
              {
            title: '游戏关卡',
            auth: '/game_levels/table',
            url: '/pagesC/game_levels/table',
            icon: 'icon-yonghu',
          },
              {
            title: '游戏记录',
            auth: '/game_record/table',
            url: '/pagesC/game_record/table',
            icon: 'icon-yonghu',
          },
              {
            title: '道具商店',
            auth: '/props/table',
            url: '/pagesC/props/table',
            icon: 'icon-yonghu',
          },
              {
            title: '购买记录',
            auth: '/purchase_record/table',
            url: '/pagesC/purchase_record/table',
            icon: 'icon-yonghu',
          },
              {
            title: '游戏成就',
            auth: '/game_achievements/table',
            url: '/pagesC/game_achievements/table',
            icon: 'icon-yonghu',
          },
              {
            title: '玩家成就',
            auth: '/achievements/table',
            url: '/pagesC/achievements/table',
            icon: 'icon-yonghu',
          },
          ],
      };
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
