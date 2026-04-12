<template>
  <view class="page_diy_table page_game_record_table" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <!-- 返回箭头 -->
    <view class="back-arrow" @click="goBack"></view>
    <tn-nav-bar>游戏记录列表</tn-nav-bar>
    <view class="page-list" id="page_diy_table">
      <!-- 筛选模块(开始) -->
      <view class="search-wrap">
        <!-- 搜索栏 -->
        <uni-forms :modelValue="query" labelWidth="70px">
            <uni-forms-item label="关卡名称" name="level_name">
                <uni-easyinput type="text" v-model="query.level_name" placeholder="关卡名称" />
              </uni-forms-item>
                  <uni-forms-item label="玩家网名" name="player_screen_name">
                <uni-easyinput type="text" v-model="query.player_screen_name" placeholder="玩家网名" />
              </uni-forms-item>
                    <uni-forms-item label="游戏总分" name="total_game_score">
                <uni-easyinput type="number" v-model="query.total_game_score" placeholder="游戏总分" />
              </uni-forms-item>
            </uni-forms>
        <!-- /搜索栏 -->
        <view class="search-btn-wrap">
          <view class="me-btn btn-reset" @click="reset()"> 重置 </view>
          <view class="me-btn btn-search" @click="search_()"> 查询 </view>
        </view>
		<view class="toolbar">
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
          <view class="item-row " v-if="0 && $check_field('get','gamer')">
            <view class="label" v-if="true">
              <span>游戏玩家</span>
            </view>
              <view class="value">
              <span>{{ get_user_gamer(o['gamer']) }}</span>
            </view>
            </view>
          <view class="item-row diy-image" v-if="1 && $check_field('get','player_avatar')">
            <view class="label" v-if=" false">
              <span>玩家头像</span>
            </view>
              <view class="value diy_img">
              <image :src="$fullImgUrl(o['player_avatar'])" width="100%" height="100" />
            </view>
            </view>
          <view class="item-row " v-if="1 && $check_field('get','player_screen_name')">
            <view class="label" v-if="true">
              <span>玩家网名</span>
            </view>
              <view class="value">
              <span>{{ o['player_screen_name'] }}</span>
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
          <view class="item-row " v-if="1 && $check_field('get','game_time')">
            <view class="label" v-if="true">
              <span>游戏时间</span>
            </view>
              <view class="value">
              <span>{{ o['game_time'] }}</span>
            </view>
            </view>
          <view class="item-row " v-if="1 && $check_field('get','number_of_steps_used')">
            <view class="label" v-if="true">
              <span>使用步数</span>
            </view>
              <view class="value">
              <span>{{ o['number_of_steps_used'] }}</span>
            </view>
            </view>
          <view class="item-row " v-if="1 && $check_field('get','total_game_score')">
            <view class="label" v-if="true">
              <span>游戏总分</span>
            </view>
              <view class="value">
              <span>{{ o['total_game_score'] }}</span>
            </view>
            </view>
          <view class="item-row " v-if="0 && $check_field('get','clearance_status')">
            <view class="label" v-if="true">
              <span>通关状态</span>
            </view>
              <view class="value">
              <span>{{ o['clearance_status'] }}</span>
            </view>
            </view>
          <view class="content">
          </view>
          <view class="operate-bar">
            <view width="100%" class="me-btn btn-delete" @click="delInfo(i)"
                  v-if="$check_action('/game_record/table', 'del') || $check_action('/game_record/view', 'del')">
              删除
            </view>
            <view width="100%" class="me-btn btn-info" @click="$navTo('/pagesC/game_record/view?' + field + '=' + o[field])"
                  v-if="$check_action('/game_record/table', 'set') || $check_action('/game_record/view', 'set') || $check_action('/game_record/view', 'get')">
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
      url_get_list: '~/api/game_record/get_list?like=0',
      url_del: '~/api/game_record/del',

      // 字段ID
      field: 'game_record_id',

      // 查询
      query: {
        size: 7,
        page: 1,
              level_name: "",
                        player_screen_name: "",
                          total_game_score: 0,
                login_time: '',
        create_time: '',
      },

      // 数据
      list: [],
                      // 用户列表
      list_user_gamer: [],
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
    get_list_before(param){
      var user_group = this.user.user_group;
      if(user_group != "管理员"){
          let sqlwhere = "(";
                            if(user_group=="游戏玩家"){
          sqlwhere+= "gamer = " + this.user.user_id + " or ";
        }
                                                                                        if (sqlwhere.length>1){
          sqlwhere = sqlwhere.substr(0,sqlwhere.length-4);
          sqlwhere += ")";
          param["sqlwhere"] = sqlwhere;
        }
        }
      return param;
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
     * 获取游戏玩家用户列表
     */
    async get_list_user_gamer() {
      var json = await this.$get("~/api/user/get_list?user_group=游戏玩家");
      if(json.result && json.result.list){
        this.list_user_gamer = json.result.list;
      }
      else if(json.error){
        console.error(json.error);
      }
    },

    get_user_gamer(id){
      let obj = this.list_user_gamer;
      let ret = "";
      for(let i=0;i<obj.length;i++){
        if(obj[i].user_id==id){
          ret = obj[i].nickname+"-"+obj[i].username;
        }
      }
      return ret;
    },
                                              },
  created() {
                this.get_list_user_gamer();
                                },
};
</script>

<style lang="scss" scoped>
  @import 'styles/pagesC/index.scss';
</style>