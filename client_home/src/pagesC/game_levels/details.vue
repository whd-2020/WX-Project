<template>
  <view id="page_diy_details" class="page_diy_details page_game_levels_details" :style="{ paddingTop: vuex_custom_bar_height + 'px', paddingBottom: vuex_safe_area_bottom + 'px',}">
    <!-- 返回箭头 -->
    <view class="back-arrow" @click="goBack"></view>
    <tn-nav-bar>游戏关卡</tn-nav-bar>
    <view class="page_diy page_game_levels" id="game_levels_details">
          <view class="warp cover_wrapper" v-if="$check_field('get', 'level_picture')">
        <image class="diy_img diy-image" :src="$fullImgUrl(obj['level_picture'])"/>
      </view>
            
      <view class="warp info-wrapper">
        <view class="container-fluid">
          <view class="row">
              <view v-if="$check_field('get', 'level_name')" class="info-item  field_text ">
                <view class="diy_title">
                  <span>关卡名称</span>
                </view>
                    <view class="diy_field diy_text">
                  <span>
                      {{ obj["level_name"] }}
                  </span>
                </view>
                </view>
                  <view v-if="$check_field('get', 'limit_number_of_steps')" class="info-item  field_text ">
                <view class="diy_title">
                  <span>限制步数</span>
                </view>
                    <view class="diy_field diy_number">
                    <span>
                        {{ obj['limit_number_of_steps'] }}
                    </span>
                </view>
                </view>
                <view v-if="$check_field('get', 'elimination_quantity')" class="info-item  field_text ">
                <view class="diy_title">
                  <span>消除数量</span>
                </view>
                    <view class="diy_field diy_number">
                    <span>
                        {{ obj['elimination_quantity'] }}
                    </span>
                </view>
                </view>
                <view v-if="$check_field('get', 'game_duration')" class="info-item  field_text ">
                <view class="diy_title">
                  <span>游戏时长</span>
                </view>
                    <view class="diy_field diy_number">
                    <span>
                        {{ obj['game_duration'] }}
                    </span>
                </view>
                </view>
                <view v-if="$check_field('get', 'gold_coin_rewards')" class="info-item  field_text ">
                <view class="diy_title">
                  <span>金币奖励</span>
                </view>
                    <view class="diy_field diy_number">
                    <span>
                        {{ obj['gold_coin_rewards'] }}
                    </span>
                </view>
                </view>
                <view v-if="$check_field('get', 'level_description')" class="info-item field_text ">
                <view class="diy_title">
                  <span>关卡说明</span>
                </view>
                    <view class="diy_field diy_desc">
                  <span>
                      {{ obj["level_description"] }}
                  </span>
                </view>
                </view>
            </view>
        </view>
      </view>

	<view class="kb-row">


        <view class="kb-wrap">
                  <view class="me-btn" v-if="$check_action('/game_record/edit', 'add')" @click="to_form('/pagesC/game_record/edit')">
            开始游戏
          </view>
            </view>
    	</view>


      <!-- 评论区列表 -->
      <view class="comment-title">评论区</view>
      <list_comment :list="list_comment" @refresh="get_comment"></list_comment>

      <view :style="'height: 100rpx'"></view>
      <view class="footer" :style="{ paddingBottom: vuex_safe_area_bottom + 'px' }">
        <view class="footer-wrap">
              <view class="go-comment" @click="goComment('/pagesB/comment/edit?source_table=game_levels&source_field=game_levels_id&source_id=' + obj['game_levels_id'])">
            评论点什么...
          </view>
    
            </view>
      </view>


    </view>
  </view>
</template>

<script>
  import list_comment from '@/components/diy/list_comment.vue';
  import mixin from '@/libs/mixins/page.js';

  export default {
    mixins: [mixin],
    components: {
      list_comment
    },
    data() {
      return {
        url_get_obj: '~/api/game_levels/get_obj?',
        field: 'game_levels_id',
        query: {
                game_levels_id: 0,
        },
        // 商品详情初始化
        obj: {
          game_levels_id: 0,
              level_name: "",
                  level_picture: "",
                  limit_number_of_steps: 0,
                  elimination_quantity: 0,
                  game_duration: 0,
                  gold_coin_rewards: 0,
                  level_description: "",
            },
        // 评论列表初始化
        list_comment: [],
                    };
    },
    methods: {
      goComment(path) {
        if (this.$check_action('/comment/list', 'add')) {
          this.$navTo(path);
        } else {
          this.$toast('暂无权限');
        }
      },
      openUrl(url) {
        uni.navigateTo({
          url: `/pages/webview/webview?url=${url}`,
        });
      },
      /**
       * 获取评论
       * @param {Object} obj
       */
      get_comment(obj) {
		if(obj == undefined){
			obj = this.obj
		}
        var query = {
          source_table: 'game_levels',
          source_field: 'game_levels_id',
          source_id: obj['game_levels_id'],
          orderby: 'create_time desc',
          reply_to_id: '0',
        };
        this.$get('/comment/get_list', query, (json) => {
          if (json.result) {
            var list_comment = json.result.list;
            list_comment.map((o) => {
              o.list_reply = [];
            });
            this.add_reply(obj, list_comment).then((list) => {
              this.list_comment = list;
            });
          }
        });
      },
      /**
       * @param { Array } list 评论列表
       * 添加回复到评论列表
       */
      add_reply(obj, list) {
        return new Promise((resolve) => {
          for (let idx = 0; idx < list.length; idx++) {
            const obj = list[idx];
            this.$get('/comment/get_list',
                {
                  source_table: 'game_levels',
                  source_field: 'game_levels_id',
                  source_id: obj['game_levels_id'],
                  orderby: 'create_time desc',
                  reply_to_id: obj.comment_id,
                },
                (res) => {
                  if (res.result) {
                    obj.list_reply = res.result.list;
                  }
                }
            );
          }
          resolve(list);
        }).catch((e) => {
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
          // 获取评论
          this.get_comment(obj);
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