<template>
  <view :style="{ paddingTop: vuex_custom_bar_height + 'px' }" class="page_diy_list page_achievements_list container">
	<view class="content">
	  <!-- 返回箭头 -->
	  <view class="back-arrow" @click="goBack"></view>
	  <tn-nav-bar>玩家成就列表</tn-nav-bar>
	  <view class="page-list" id="page_diy_list">
	        <!-- 筛选模块(开始) -->
	        <view class="search-wrap">
	  	    	  	    	      	          <Search v-model="query.player_screen_name" placeholder="搜索玩家网名" @search="search_" @cancel="search_cancel('player_screen_name')" @input="(val) => inputValue(val, 'player_screen_name')" />
	      	    	  	    	      	          <Search v-model="query.achievement_name" placeholder="搜索成就名称" @search="search_" @cancel="search_cancel('achievement_name')" @input="(val) => inputValue(val, 'achievement_name')" />
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
	              @click=" $navTo('/pagesC/achievements/details?achievements_id=' + o['achievements_id'])">
	  	              <view class="item-row " v-if="0 && $check_field('get', 'game_user')">
	                <view class="label" v-if="true">
	                  <span>游戏玩家</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ get_user_game_user(o['game_user']) }}</span>
	                </view>
	    	              </view>
	  	              <view class="item-row " v-if="0 && $check_field('get', 'player_screen_name')">
	                <view class="label" v-if="true">
	                  <span>玩家网名</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ o['player_screen_name'] }}</span>
	                </view>
	    	              </view>
	  	              <view class="item-row " v-if="1 && $check_field('get', 'achievement_name')">
	                <view class="label" v-if="true">
	                  <span>成就名称</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ o['achievement_name'] }}</span>
	                </view>
	    	              </view>
	  	              <view class="item-row " v-if="1 && $check_field('get', 'gold_coin_rewards')">
	                <view class="label" v-if="true">
	                  <span>金币奖励</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ o['gold_coin_rewards'] }}</span>
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
      url_get_list: '~/api/achievements/get_list?like=0',
          // 用户列表
      list_user_game_user: [],
                        query: {
                  player_screen_name: "", // 玩家网名
                        achievement_name: "", // 成就名称
                  achievements_id: 0, // ID
        page: 1,
        size: 10,
      },
      list: [],
      count: 50,
      menuList: [
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
    async get_list_user_game_user() {
      var json = await this.$get("~/api/user/get_list?user_group=游戏玩家");
      if(json.result && json.result.list){
        this.list_user_game_user = json.result.list;
      }
      else if(json.error){
        console.error(json.error);
      }
    },
    get_user_game_user(id){
      let obj = this.list_user_game_user;
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
        this.get_list_user_game_user();
                      },
};
</script>

<style lang="scss" scoped>
  @import 'styles/pagesC/index.scss';
</style>