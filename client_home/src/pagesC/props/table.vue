<template>
  <view class="page_diy_table page_props_table" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <!-- 返回箭头 -->
    <view class="back-arrow" @click="goBack"></view>
    <tn-nav-bar>道具商店列表</tn-nav-bar>
    <view class="page-list" id="page_diy_table">
      <!-- 筛选模块(开始) -->
      <view class="search-wrap">
        <!-- 搜索栏 -->
        <uni-forms :modelValue="query" labelWidth="70px">
            <uni-forms-item label="道具名称" name="prop_name">
                <uni-easyinput type="text" v-model="query.prop_name" placeholder="道具名称" />
              </uni-forms-item>
                </uni-forms>
        <!-- /搜索栏 -->
        <view class="search-btn-wrap">
          <view class="me-btn btn-reset" @click="reset()"> 重置 </view>
          <view class="me-btn btn-search" @click="search_()"> 查询 </view>
        </view>
		<view class="toolbar">
				  <view width="100%" class="me-btn btn-add" @click="$navTo('/pagesC/props/view?')" v-if="$check_action('/props/table', 'add') || $check_action('/props/view', 'add')">添加</view>
				</view>
      </view>
      <!-- 列表 -->
      <view class="list-wrap-row">
        <view v-for="(o, i) in list" :key="i" class="list-item">
          <view class="item-row " v-if="1 && $check_field('get','prop_name')">
            <view class="label" v-if="true">
              <span>道具名称</span>
            </view>
              <view class="value">
              <span>{{ o['prop_name'] }}</span>
            </view>
            </view>
          <view class="item-row diy-image" v-if="1 && $check_field('get','props_picture')">
            <view class="label" v-if=" false">
              <span>道具图片</span>
            </view>
              <view class="value diy_img">
              <image :src="$fullImgUrl(o['props_picture'])" width="100%" height="100" />
            </view>
            </view>
          <view class="item-row " v-if="1 && $check_field('get','gold_coins_required')">
            <view class="label" v-if="true">
              <span>所需金币</span>
            </view>
              <view class="value">
              <span>{{ o['gold_coins_required'] }}</span>
            </view>
            </view>
          <view class="item-row " v-if="1 && $check_field('get','props_use')">
            <view class="label" v-if="true">
              <span>道具用途</span>
            </view>
              <view class="value">
              <span>{{ o['props_use'] }}</span>
            </view>
            </view>
          <view class="content">
          </view>
          <view class="operate-bar">
            <view width="100%" class="me-btn btn-delete" @click="delInfo(i)"
                  v-if="$check_action('/props/table', 'del') || $check_action('/props/view', 'del')">
              删除
            </view>
            <view width="100%" class="me-btn btn-info" @click="$navTo('/pagesC/props/view?' + field + '=' + o[field])"
                  v-if="$check_action('/props/table', 'set') || $check_action('/props/view', 'set') || $check_action('/props/view', 'get')">
              详情
            </view>
      <!--跨表按钮-->
			              <view width="100%" class="me-btn btn-pay"
                    v-if="$check_action('/purchase_record/view', 'add')"
                    @click="to_form('/pagesC/purchase_record/view', o)">
                <span>购买</span>
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
      url_get_list: '~/api/props/get_list?like=0',
      url_del: '~/api/props/del',

      // 字段ID
      field: 'props_id',

      // 查询
      query: {
        size: 7,
        page: 1,
              prop_name: "",
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