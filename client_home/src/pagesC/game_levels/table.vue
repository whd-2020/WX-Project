<template>
  <view class="page_diy_table page_game_levels_table" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <!-- 返回箭头 -->
    <view class="back-arrow" @click="goBack"></view>
    <tn-nav-bar>游戏关卡列表</tn-nav-bar>
    <view class="page-list" id="page_diy_table">
      <!-- 筛选模块(开始) -->
      <view class="search-wrap">
        <!-- 搜索栏 -->
        <uni-forms :modelValue="query" labelWidth="70px">
            <uni-forms-item label="关卡名称" name="level_name">
                <uni-easyinput type="text" v-model="query.level_name" placeholder="关卡名称" />
              </uni-forms-item>
                      </uni-forms>
        <!-- /搜索栏 -->
        <view class="search-btn-wrap">
          <view class="me-btn btn-reset" @click="reset()"> 重置 </view>
          <view class="me-btn btn-search" @click="search_()"> 查询 </view>
        </view>
		<view class="toolbar">
				  <view width="100%" class="me-btn btn-add" @click="$navTo('/pagesC/game_levels/view?')" v-if="$check_action('/game_levels/table', 'add') || $check_action('/game_levels/view', 'add')">添加</view>
				</view>
      </view>
      <!-- 列表 -->
      <view class="list-wrap-row">
        <view v-for="(o, i) in list" :key="i" class="list-item">
          <view class="item-row " v-if="1 && $check_field('get','level_name')">
            <view class="label" v-if="true">
              <span>关卡名称</span>
            </view>
              <view class="value">
              <span>{{ o['level_name'] }}</span>
            </view>
            </view>
          <view class="item-row diy-image" v-if="1 && $check_field('get','level_picture')">
            <view class="label" v-if=" false">
              <span>关卡图片</span>
            </view>
              <view class="value diy_img">
              <image :src="$fullImgUrl(o['level_picture'])" width="100%" height="100" />
            </view>
            </view>
          <view class="item-row " v-if="1 && $check_field('get','limit_number_of_steps')">
            <view class="label" v-if="true">
              <span>限制步数</span>
            </view>
              <view class="value">
              <span>{{ o['limit_number_of_steps'] }}</span>
            </view>
            </view>
          <view class="item-row " v-if="0 && $check_field('get','elimination_quantity')">
            <view class="label" v-if="true">
              <span>消除数量</span>
            </view>
              <view class="value">
              <span>{{ o['elimination_quantity'] }}</span>
            </view>
            </view>
          <view class="item-row " v-if="1 && $check_field('get','game_duration')">
            <view class="label" v-if="true">
              <span>游戏时长</span>
            </view>
              <view class="value">
              <span>{{ o['game_duration'] }}</span>
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
          <view class="item-row " v-if="0 && $check_field('get','level_description')">
            <view class="label" v-if="true">
              <span>关卡说明</span>
            </view>
              <view class="value">
              <span>{{ o['level_description'] }}</span>
            </view>
            </view>
          <view class="content">
          </view>
          <view class="operate-bar">
            <view width="100%" class="me-btn btn-delete" @click="delInfo(i)"
                  v-if="$check_action('/game_levels/table', 'del') || $check_action('/game_levels/view', 'del')">
              删除
            </view>
            <view width="100%" class="me-btn btn-info" @click="$navTo('/pagesC/game_levels/view?' + field + '=' + o[field])"
                  v-if="$check_action('/game_levels/table', 'set') || $check_action('/game_levels/view', 'set') || $check_action('/game_levels/view', 'get')">
              详情
            </view>
      <!--跨表按钮-->
			              <view width="100%" class="me-btn btn-pay"
                    v-if="$check_action('/game_record/view', 'add')"
                    @click="to_form('/pagesC/game_record/view', o)">
                <span>开始游戏</span>
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
      url_get_list: '~/api/game_levels/get_list?like=0',
      url_del: '~/api/game_levels/del',

      // 字段ID
      field: 'game_levels_id',

      // 查询
      query: {
        size: 7,
        page: 1,
              level_name: "",
                          login_time: '',
        create_time: '',
      },

      // 数据
      list: [],
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
                                            },
  created() {
                              },
};
</script>

<style lang="scss" scoped>
  @import 'styles/pagesC/index.scss';
</style>