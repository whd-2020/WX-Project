<template>
  <view :style="{ paddingTop: vuex_custom_bar_height + 'px' }" class="page_diy_list page_game_record_list container">
	<view class="content">
	  <tn-nav-bar>游戏记录列表</tn-nav-bar>
	  <view class="page-list" id="page_diy_list">
	        <!-- 筛选模块(开始) -->
	        <view class="search-wrap">
	  	    	      	          <Search v-model="query.level_name" placeholder="搜索关卡名称" @search="search_" @cancel="search_cancel('level_name')" @input="(val) => inputValue(val, 'level_name')" />
	      	    	  	    	  	    	  	    	      	          <Search v-model="query.player_screen_name" placeholder="搜索玩家网名" @search="search_" @cancel="search_cancel('player_screen_name')" @input="(val) => inputValue(val, 'player_screen_name')" />
	      	    	  	    	  	    	  	    	  	    	      	          <Search v-model="query.total_game_score" placeholder="搜索游戏总分" @search="search_" @cancel="search_cancel('total_game_score')" @input="(val) => inputValue(val, 'total_game_score')" />
	      	    	  	    	  	  	    	      	    	  	    	  	    	  	    	      	    	  	    	  	    	  	    	  	    	      	    	  	    	  	  	          <MeDropdown
	            :menuList.sync="menuList"
	            themeColor="#0079fe"
	            :duration="300"
	            :isCeiling="true"
	            @onConfirm="onConfirmDropdown"
	            @onChange="onChangeDropdown"
	          ></MeDropdown>
	        </view>
	  
	        <!-- 筛选模块(结束) -->
	  <!-- 列表 -->
	        <view class="customized-list">
	  	          <view v-for="(o, i) in list" :key="i" class="customized-item">
	  	  	            <view
	              class="customized-item-body"
	              @click=" $navTo('/pagesC/game_record/details?game_record_id=' + o['game_record_id'])">
	  	              <view class="item-row " v-if="1 && $check_field('get', 'level_name')">
	                <view class="label" v-if="true">
	                  <span>关卡名称</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ o['level_name'] }}</span>
	                </view>
	    	              </view>
	  	              <view class="item-row " v-if="0 && $check_field('get', 'gamer')">
	                <view class="label" v-if="true">
	                  <span>游戏玩家</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ get_user_gamer(o['gamer']) }}</span>
	                </view>
	    	              </view>
	  	              <view class="item-row diy-image" v-if="1 && $check_field('get', 'player_avatar')">
	                <view class="label" v-if=" false">
	                  <span>玩家头像</span>
	                </view>
	    	                <view class="value diy_img">
	                  <image :src="$fullImgUrl(o['player_avatar'])" width="100%" height="100" />
	                </view>
	    	              </view>
	  	              <view class="item-row " v-if="1 && $check_field('get', 'player_screen_name')">
	                <view class="label" v-if="true">
	                  <span>玩家网名</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ o['player_screen_name'] }}</span>
	                </view>
	    	              </view>
	  	              <view class="item-row " v-if="0 && $check_field('get', 'gold_coin_rewards')">
	                <view class="label" v-if="true">
	                  <span>金币奖励</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ o['gold_coin_rewards'] }}</span>
	                </view>
	    	              </view>
	  	              <view class="item-row " v-if="1 && $check_field('get', 'game_time')">
	                <view class="label" v-if="true">
	                  <span>游戏时间</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ o['game_time'] }}</span>
	                </view>
	    	              </view>
	  	              <view class="item-row " v-if="1 && $check_field('get', 'number_of_steps_used')">
	                <view class="label" v-if="true">
	                  <span>使用步数</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ o['number_of_steps_used'] }}</span>
	                </view>
	    	              </view>
	  	              <view class="item-row " v-if="1 && $check_field('get', 'total_game_score')">
	                <view class="label" v-if="true">
	                  <span>游戏总分</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ o['total_game_score'] }}</span>
	                </view>
	    	              </view>
	  	              <view class="item-row " v-if="0 && $check_field('get', 'clearance_status')">
	                <view class="label" v-if="true">
	                  <span>通关状态</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ o['clearance_status'] }}</span>
	                </view>
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
	</view>
</template>

<script>
import Search from '@/components/businessCp/search.vue';
import mixin from '@/libs/mixins/page.js';
import MeDropdown from '@/components/me-dropdown/index.vue';

export default {
  mixins: [mixin],
  components: {
    Search,
    MeDropdown,
  },
  data() {
    return {
      url_get_list: '~/api/game_record/get_list?like=0',
              // 用户列表
      list_user_gamer: [],
                                        query: {
                level_name: "", // 关卡名称
                            player_screen_name: "", // 玩家网名
                              total_game_score: 0, // 游戏总分
                  game_record_id: 0, // ID
        page: 1,
        size: 10,
      },
      list: [],
      count: 50,
      menuList: [
                    {
          title: '游戏时间',
          type: 'sort',
          command: '`game_time`',
          value: 0,
        },
            {
          title: '使用步数',
          type: 'sort',
          command: '`number_of_steps_used`',
          value: 0,
        },
            {
          title: '游戏总分',
          type: 'sort',
          command: '`total_game_score`',
          value: 0,
        },
            {
            title: '发布时间',
            type: 'sort',
            command: '`create_time`',
            value: 0,
        }
      ],
    };
  },
  methods: {
    onConfirmDropdown(val) {
      if (val.value) {
        this.query.orderby = val.command + ' ' + val.value;
      } else {
        this.query.orderby = '';
      }
      this.search_();
    },
    onChangeDropdown(v) {},
    /**
     * 获取列表后
     * @param {Object} json
     * @param {Object} func
     */
    get_list_after(json, func) {
      let list = json.result.list;
      if (func) {
        func(json);
      }
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
          
              
      
      
      
              
      
        search_() {
      this.query.page = 1;
      this.get_list();
    },
    search_cancel(key) {
      this.query[key] = '';
      this.search_();
    },
	inputValue(val, key) {
	  this.query[key] = val;
	  this.search_();
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