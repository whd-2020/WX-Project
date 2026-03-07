<template>
  <view id="home" class="page_home" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
	<view class="header_bg"></view>
    <tn-nav-bar fixed :isBack="false">
      <view class="nav-wrapper">
        <view class="nav-user" @click="$navTo('/pages/user/info')">
          <!-- 头像：优先使用微信头像的完整https地址，其次再走资源拼接 -->
          <image :src="avatarUrl"></image>
        </view>
        <!-- 昵称：优先显示微信昵称，没有则回退到用户名 -->
        <view class="nav-username">
          {{ displayName }}
        </view>
        <!-- 原“推荐”标签已去掉，只保留头像区域 -->
      </view>
    </tn-nav-bar>

    <view class="tab-pane" v-show="tabIndex === 0">
      <!-- 顶部搜索框已去掉 -->

      <view class="scroll-x">
        <view class="left">
          <scroll-view class="scroll-view" :style="[!isExpand ? expandStyle : '']" scroll-x="true">
		    <view class="item">
		      推荐
		    </view>
            <view
              class="item"
              @click="clickMore(item.path)"
              v-for="(item, index) in list_menu"
              :key="index"
			   v-if="$check_action(item.path,'get') || item.path == '/webview/urlview' || item.path == '/chat/index'"
            >
              {{ item.mod_name }}
            </view>
          </scroll-view>
        </view>
        <view class="right">
          <view class="iconfont icon-liebiao" @click="isExpand = !isExpand"></view
        ></view>
      </view>

      <!-- 轮播图模块(开始) -->
      <view class="swiper" v-if="list_slide && list_slide.length > 0">
        <Slide :list="list_slide" :show_title="true" />
      </view>
      <!-- 轮播图模块(结束) -->

      <!-- 游戏特色卡片(开始) -->
      <view class="game-features" v-if="!list_slide || list_slide.length === 0">
        <view class="feature-card" @click="$navTo('/pages/track/rope')">
          <view class="feature-icon">🎯</view>
          <view class="feature-title">结绳计数</view>
          <view class="feature-desc">体验古代计数智慧</view>
        </view>
        <view class="feature-card" @click="$navTo('/pages/track/counting_rods')">
          <view class="feature-icon">📊</view>
          <view class="feature-title">筹算</view>
          <view class="feature-desc">感受古代数学工具</view>
        </view>
        <view class="feature-card" @click="$navTo('/pages/track/abacus')">
          <view class="feature-icon">🧮</view>
          <view class="feature-title">珠算</view>
          <view class="feature-desc">学习传统计算方式</view>
        </view>
        <view class="feature-card" @click="$navTo('/pages/track/comprehensive')">
          <view class="feature-icon">🏆</view>
          <view class="feature-title">综合挑战</view>
          <view class="feature-desc">挑战更高难度</view>
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
