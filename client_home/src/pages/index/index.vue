<template>
  <view id="home" class="page_home" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
	<view class="header_bg"></view>
    <tn-nav-bar fixed :isBack="false" :zIndex="999">
      <view class="nav-wrapper">
        <view class="nav-user" @click="$navTo('/pages/user/info')">
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
      <image class="bg-image" src="/static/img/index/LoadingPage.png"></image>
      <!-- 游戏特色卡片(开始) -->
      <view class="game-features" style="position: relative; z-index: 10;">
        <view class="feature-card" @click="$navTo('/pages/track/rope')">
          <view class="feature-image-wrapper">
            <image class="feature-image" src="/static/img/index/ShengJieJiShu.png"></image>
          </view>
        </view>
        <view class="feature-card" @click="$navTo('/pages/track/counting_rods')">
          <view class="feature-image-wrapper">
            <image class="feature-image" src="/static/img/index/ChouSuanYanSuan.png"></image>
          </view>
        </view>
        <view class="feature-card" @click="$navTo('/pages/track/abacus')">
          <view class="feature-image-wrapper">
            <image class="feature-image" src="/static/img/index/ZhuSuanQiMeng.png"></image>
          </view>
        </view>
        <view class="feature-card" @click="$navTo('/pages/track/comprehensive')">
          <view class="feature-image-wrapper">
            <image class="feature-image" src="/static/img/index/ShuZiRenZhi.png"></image>
          </view>
        </view>
      </view>
      <!-- 游戏特色卡片(结束) -->

      <!-- 菜单模块(开始) -->
      <view class="menu" v-if="list_menu.length > 0">
        <list_menu :list="list_menu"></list_menu>
      </view>
      <!-- 菜单模块(结束) -->
	  <view class="home_recommend">
              	  </view>
      <!-- 底部“游戏公告”和“更多”区域已移除 -->
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
import Search from '@/components/businessCp/search.vue';
              import Card from '@/components/common/card.vue';
import list_menu from '@/components/diy/list_menu.vue';
import Slide from '@/components/common/slide.vue';
import Notice from '@/components/common/notice.vue';

import mixin from '@/libs/mixins/page.js';
import tabbar from '@/libs/mixins/tabbar.js';
import * as HomeApi from '@/api/home';

export default {
  mixins: [mixin, tabbar],
  components: {
    Search,
    Card,
                  list_menu,
    Slide,
    Notice,
  },
  data() {
    return {
      tabIndex: 0,
      isExpand: false,
      expandStyle: {
        whiteSpace: 'nowrap',
      },
      tabbarIndex: 0,
                    list_slide: [],
      list_menu: [],
      list_notice: [],
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
    // 展示用昵称：优先昵称，其次用户名
    displayName() {
      const userInfo = this.userInfo || {};
      return userInfo.nickname || userInfo.username || '游客';
    },
  },
  created() {
    console.log('首页 created - tabbarList:', this.tabbarList);
  },
  methods: {
    getFullPath(path) {
      if (path) {
        const fullPath = this.pathList[path];
        if (fullPath) {
          return fullPath + path;
        } else {
          return '/pagesC' + path;
        }
      }
      return '';
    },
    onClickTab(val) {
      this.tabIndex = val;
    },
                            
    /**
     *  获取轮播图
     */
    get_slides() {
      HomeApi.gethomeSlidesListApi().then((res) => {
        if (res.result && res.result.list) {
          this.list_slide = res.result.list;
        }
      });
    },

    /**
     *  获取导航栏
     */
    get_menu() {
      const user_group = this.userGroup;
      HomeApi.getMenuListApi({
        size: '0',
        get: 1,
        user_group,
        position: 'top',
      }).then((res) => {
        if (res.result && res.result.list) {
          this.list_menu = res.result.list;
        }
      });
    },

    /**
     *  获取公告列表
     */
    get_notice() {
      HomeApi.getHomeNoticeListApi({ page: 1, size: 3 }).then((res) => {
        if (res.result && res.result.list) {
          // console.log('公告', res.result.list);
          this.list_notice = res.result.list;
          this.list_notice.map((o) => {
            o['praise_len'];
          });
          this.get_praise(this.list_notice, 'notice', 'notice_id');
        }
      });
    },
    /**
     *  获取点赞数
     *  @param {Object} list
     */
    get_praise(list, table, idName) {
      if (list) {
        for (let i = 0; i < list.length; i++) {
          list[i].praise_len = 0;
        }

        HomeApi.getPraiseListApi({
          source_table: table,
          groupby: 'source_id',
        }).then((res) => {
          if (res.result && res.result.list) {
            res.result.list.map((o) => {
              for (let i = 0; i < list.length; i++) {
                let oj = list[i];
                if (oj[idName] === o['source_id']) {
                  oj['praise_len'] = o['count'];
                  break;
                }
              }
            });
          } else if (res.error) {
            // console.error(res.error);
          }
        });
      }
    },
	clickMore(url){
		if(url == "/article/index" || url == "/forum/index"){
			this.$navToTab(this.getFullPath(url))
		}else{
			this.$navTo(this.getFullPath(url))
		}
	}
  },
  onShow() {
    this.get_slides();
    this.get_menu();
    this.get_notice();
                },
};
</script>
<style lang="scss" scoped>
@import 'styles/pages/index.scss';
</style>
