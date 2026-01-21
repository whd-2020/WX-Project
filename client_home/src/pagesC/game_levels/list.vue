<template>
  <view :style="{ paddingTop: vuex_custom_bar_height + 'px' }" class="page_diy_list page_game_levels_list container">
	<view class="content">
	  <tn-nav-bar>游戏关卡列表</tn-nav-bar>
	  <view class="page-list" id="page_diy_list">
	        <!-- 筛选模块(开始) -->
	        <view class="search-wrap">
	  	    	      	          <Search v-model="query.level_name" placeholder="搜索关卡名称" @search="search_" @cancel="search_cancel('level_name')" @input="(val) => inputValue(val, 'level_name')" />
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
	              @click=" $navTo('/pagesC/game_levels/details?game_levels_id=' + o['game_levels_id'])">
	  	              <view class="item-row " v-if="1 && $check_field('get', 'level_name')">
	                <view class="label" v-if="true">
	                  <span>关卡名称</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ o['level_name'] }}</span>
	                </view>
	    	              </view>
	  	              <view class="item-row diy-image" v-if="1 && $check_field('get', 'level_picture')">
	                <view class="label" v-if=" false">
	                  <span>关卡图片</span>
	                </view>
	    	                <view class="value diy_img">
	                  <image :src="$fullImgUrl(o['level_picture'])" width="100%" height="100" />
	                </view>
	    	              </view>
	  	              <view class="item-row " v-if="1 && $check_field('get', 'limit_number_of_steps')">
	                <view class="label" v-if="true">
	                  <span>限制步数</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ o['limit_number_of_steps'] }}</span>
	                </view>
	    	              </view>
	  	              <view class="item-row " v-if="0 && $check_field('get', 'elimination_quantity')">
	                <view class="label" v-if="true">
	                  <span>消除数量</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ o['elimination_quantity'] }}</span>
	                </view>
	    	              </view>
	  	              <view class="item-row " v-if="1 && $check_field('get', 'game_duration')">
	                <view class="label" v-if="true">
	                  <span>游戏时长</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ o['game_duration'] }}</span>
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
	  	              <view class="item-row " v-if="0 && $check_field('get', 'level_description')">
	                <view class="label" v-if="true">
	                  <span>关卡说明</span>
	                </view>
	    	                <view class="value">
	                  <span>{{ o['level_description'] }}</span>
	                </view>
	    	              </view>
	  	  	            </view>
	  
	  	            <view class="customized-item-footer">
	    	    	    	              <view class="comment">
	                <text class="icon iconfont icon-pinglun"></text>
	                {{ o['comment_len'] || 0 }}
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
      url_get_list: '~/api/game_levels/get_list?like=0',
                                  query: {
                level_name: "", // 关卡名称
                            game_levels_id: 0, // ID
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
                              },
};
</script>

<style lang="scss" scoped>
  @import 'styles/pagesC/index.scss';
</style>