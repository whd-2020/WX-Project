<template>
  <view class="page_diy_table page_game_achievements_table" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <tn-nav-bar>游戏成就列表</tn-nav-bar>
    <view class="page-list" id="page_diy_table">
      <!-- 筛选模块(开始) -->
      <view class="search-wrap">
        <!-- 搜索栏 -->
        <uni-forms :modelValue="query" labelWidth="70px">
            <uni-forms-item label="成就名称" name="achievement_name">
                <uni-easyinput type="text" v-model="query.achievement_name" placeholder="成就名称" />
              </uni-forms-item>
              <uni-forms-item label="成就类型" name="type_of_achievement">
                      <uni-data-select
                    placeholder="请选择成就类型"
                    v-model="query['type_of_achievement']"
                    :localdata="list_type_of_achievement"
            ></uni-data-select>
                    </uni-forms-item>
              </uni-forms>
        <!-- /搜索栏 -->
        <view class="search-btn-wrap">
          <view class="me-btn btn-reset" @click="reset()"> 重置 </view>
          <view class="me-btn btn-search" @click="search_()"> 查询 </view>
        </view>
		<view class="toolbar">
				  <view width="100%" class="me-btn btn-add" @click="$navTo('/pagesC/game_achievements/view?')" v-if="$check_action('/game_achievements/table', 'add') || $check_action('/game_achievements/view', 'add')">添加</view>
				</view>
      </view>
      <!-- 列表 -->
      <view class="list-wrap-row">
        <view v-for="(o, i) in list" :key="i" class="list-item">
          <view class="item-row " v-if="0 && $check_field('get','achievement_name')">
            <view class="label" v-if="true">
              <span>成就名称</span>
            </view>
              <view class="value">
              <span>{{ o['achievement_name'] }}</span>
            </view>
            </view>
          <view class="item-row " v-if="0 && $check_field('get','type_of_achievement')">
            <view class="label" v-if="true">
              <span>成就类型</span>
            </view>
              <view class="value">
              <span>{{ o['type_of_achievement'] }}</span>
            </view>
            </view>
          <view class="item-row " v-if="0 && $check_field('get','score_settings')">
            <view class="label" v-if="true">
              <span>分数设置</span>
            </view>
              <view class="value">
              <span>{{ o['score_settings'] }}</span>
            </view>
            </view>
          <view class="item-row " v-if="0 && $check_field('get','gold_coin_rewards')">
            <view class="label" v-if="true">
              <span>金币奖励</span>
            </view>
              <view class="value">
              <span>{{ o['gold_coin_rewards'] }}</span>
            </view>
            </view>
          <view class="content">
          </view>
          <view class="operate-bar">
            <view width="100%" class="me-btn btn-delete" @click="delInfo(i)"
                  v-if="$check_action('/game_achievements/table', 'del') || $check_action('/game_achievements/view', 'del')">
              删除
            </view>
            <view width="100%" class="me-btn btn-info" @click="$navTo('/pagesC/game_achievements/view?' + field + '=' + o[field])"
                  v-if="$check_action('/game_achievements/table', 'set') || $check_action('/game_achievements/view', 'set') || $check_action('/game_achievements/view', 'get')">
              详情
            </view>
          </view>
        </view>
      </view>
      <!-- /列表 -->
      <!-- 分页器 -->
      <uni-pagination
        class="pager"
        show-icon="true"
        :total="count"
        :pageSize="query.size"
        :current="query.page"
        @change="page_change"
      ></uni-pagination>
      <!-- /分页器 -->
    </view>
  </view>
</template>
<script>
import mixin from '@/libs/mixins/page.js';

export default {
  mixins: [mixin],
  data() {
    return {
      // 获取数据地址
      url_get_list: '~/api/game_achievements/get_list?like=0',
      url_del: '~/api/game_achievements/del',

      // 字段ID
      field: 'game_achievements_id',

      // 查询
      query: {
        size: 7,
        page: 1,
              achievement_name: "",
                    type_of_achievement: "",
                  login_time: '',
        create_time: '',
      },

      // 数据
      list: [],
                // 成就类型列表
      list_type_of_achievement: [{value:"",text:"全部"}],
                };
  },
  methods: {
    search_() {
      this.query.page = 1;
      this.get_list();
    },
    /**
     * 重置
     */
    reset: function reset() {
      uni.clear(this.query);
      uni.push(this.query, this.config);
      this.get_list();
    },
    delInfo(v) {
      let _this = this;
      uni.showModal({
        title: '删除',
        content: '此操作将永久删除该文件, 是否继续?',
        success: function (res) {
          if (res.confirm) {
            let list = [v];
            _this.delAll(list);
          } else if (res.cancel) {
            console.log('用户点击取消');
          }
        },
      });
    },
            /**
     * 获取成就类型列表
     */
    async get_list_type_of_achievement() {
              ['累计','单局','彩蛋'].map((o) => this.list_type_of_achievement.push({value:o,text:o}));
                },
                    },
  created() {
          // 初始化成就类型列表
    this.get_list_type_of_achievement();
              },
};
</script>

<style lang="scss" scoped>
  @import 'styles/pagesC/index.scss';
</style>