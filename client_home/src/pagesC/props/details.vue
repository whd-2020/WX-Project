<template>
  <view id="page_diy_details" class="page_diy_details page_props_details" :style="{ paddingTop: vuex_custom_bar_height + 'px', paddingBottom: vuex_safe_area_bottom + 'px',}">
    <tn-nav-bar>道具商店</tn-nav-bar>
    <view class="page_diy page_props" id="props_details">
          <view class="warp cover_wrapper" v-if="$check_field('get', 'props_picture')">
        <image class="diy_img diy-image" :src="$fullImgUrl(obj['props_picture'])"/>
      </view>
      
      <view class="warp info-wrapper">
        <view class="container-fluid">
          <view class="row">
              <view v-if="$check_field('get', 'prop_name')" class="info-item  field_text ">
                <view class="diy_title">
                  <span>道具名称</span>
                </view>
                    <view class="diy_field diy_text">
                  <span>
                      {{ obj["prop_name"] }}
                  </span>
                </view>
                </view>
                  <view v-if="$check_field('get', 'gold_coins_required')" class="info-item  field_text ">
                <view class="diy_title">
                  <span>所需金币</span>
                </view>
                    <view class="diy_field diy_text">
                  <span>
                      {{ obj["gold_coins_required"] }}
                  </span>
                </view>
                </view>
                <view v-if="$check_field('get', 'props_use')" class="info-item  field_text ">
                <view class="diy_title">
                  <span>道具用途</span>
                </view>
                    <view class="diy_field diy_text">
                  <span>
                      {{ obj["props_use"] }}
                  </span>
                </view>
                </view>
            </view>
        </view>
      </view>

	<view class="kb-row">


        <view class="kb-wrap">
                  <view class="me-btn" v-if="$check_action('/purchase_record/edit', 'add')" @click="to_form('/pagesC/purchase_record/edit')">
            购买
          </view>
            </view>
    	</view>





    </view>
  </view>
</template>

<script>
  import mixin from '@/libs/mixins/page.js';

  export default {
    mixins: [mixin],
    components: {
    },
    data() {
      return {
        url_get_obj: '~/api/props/get_obj?',
        field: 'props_id',
        query: {
                props_id: 0,
        },
        // 商品详情初始化
        obj: {
          props_id: 0,
              prop_name: "",
                  props_picture: "",
                  gold_coins_required: "",
                  props_use: "",
            },
              };
    },
    methods: {
      openUrl(url) {
        uni.navigateTo({
          url: `/pages/webview/webview?url=${url}`,
        });
      },
      /**
       * 获取对象之后
       * @param {Object} json 结果对象
       */
      get_obj_after(json) {
        // 判断是否获取到数据
        if (this.obj) {
          var obj = this.obj;
        }
      },
                    },
    created() {
                    },
    onShow() {
        
    },
  };
</script>

<style lang="scss" scoped>
  @import 'styles/pagesC/index.scss';
</style>