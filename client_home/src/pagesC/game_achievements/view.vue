<template>
  <view class="page_diy_view page_game_achievements_view" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <!-- 返回箭头 -->
    <view class="back-arrow" @click="goBack"></view>
    <tn-nav-bar>游戏成就详情</tn-nav-bar>
    <view id="page_diy_view" class="page-from">
      <uni-forms :modelValue="form" v-if="is_view()" labelWidth="70px">

        <uni-forms-item v-if="$check_field('get','achievement_name') || ($check_field('add','achievement_name') || $check_field('set','achievement_name'))" label="成就名称" name="achievement_name">
                      <uni-easyinput type="text" v-model="form['achievement_name']" v-if="(form['game_achievements_id'] && $check_field('set','achievement_name')) || (!form['game_achievements_id'] && $check_field('add','achievement_name'))" :disabled="disabledObj['achievement_name_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','achievement_name')">
            {{ form['achievement_name'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','type_of_achievement') || ($check_field('add','type_of_achievement') || $check_field('set','type_of_achievement'))" label="成就类型" name="type_of_achievement">
              <uni-data-select
              v-model="form.type_of_achievement"
              :localdata="list_type_of_achievement"
              :clear="!disabledObj['type_of_achievement_isDisabled']"
              :disabled="disabledObj['type_of_achievement_isDisabled']"
              v-if="(form['game_achievements_id'] && $check_field('set', 'type_of_achievement')) || (!form['game_achievements_id'] && $check_field('add', 'type_of_achievement'))"
			  style="width: 100%;"
          ></uni-data-select>
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get', 'type_of_achievement')">
            {{ form['type_of_achievement'] }}
          </text>
            </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','score_settings') || ($check_field('add','score_settings') || $check_field('set','score_settings'))" label="分数设置" name="score_settings">
                      <uni-easyinput type="text" v-model="form['score_settings']" v-if="(form['game_achievements_id'] && $check_field('set','score_settings')) || (!form['game_achievements_id'] && $check_field('add','score_settings'))" :disabled="disabledObj['score_settings_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','score_settings')">
            {{ form['score_settings'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','gold_coin_rewards') || ($check_field('add','gold_coin_rewards') || $check_field('set','gold_coin_rewards'))" label="金币奖励" name="gold_coin_rewards">
                      <uni-easyinput type="text" v-model="form['gold_coin_rewards']" v-if="(form['game_achievements_id'] && $check_field('set','gold_coin_rewards')) || (!form['game_achievements_id'] && $check_field('add','gold_coin_rewards'))" :disabled="disabledObj['gold_coin_rewards_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','gold_coin_rewards')">
            {{ form['gold_coin_rewards'] }}
          </text>
                    </uni-forms-item>
      </uni-forms>
      <view class="form-footer" v-if="$check_action('/game_achievements/view','set') || ($check_action('/game_achievements/view','add') || $check_option('/game_achievements/table','examine'))">
        <view class="me-btn btn-submit" @click="submit_()"> 提交 </view>
        <view class="me-btn btn-cancel" @click="cancel()">取消</view>
      </view>
      <view class="form_button" v-else>
        <view class="me-btn btn-cancel" @click="cancel()">返回</view>
      </view>
    </view>
  </view>
</template>

<script>
import mixin from '@/libs/mixins/page.js';
                import { uploadFilePathApi, uploadFileApi } from '@/api/common.js';
export default {
  mixins: [mixin],
  data() {
    return {
	  today: new Date().toISOString().split('T')[0],
      field: "game_achievements_id",
      url_add: "~/api/game_achievements/add?",
      url_set: "~/api/game_achievements/set?",
      url_get_obj: "~/api/game_achievements/get_obj?",
      url_upload: "~/api/game_achievements/upload?",
      achievement_name: null,
      type_of_achievement: null,
      score_settings: null,
      gold_coin_rewards: null,
      query: {
        "game_achievements_id": 0,
      },
      form: {
          "achievement_name":  '', // 成就名称
            "type_of_achievement":  '', // 成就类型
            "score_settings":  0 , // 分数设置
            "gold_coin_rewards":  '', // 金币奖励
          "game_achievements_id": 0, // ID
      },
      disabledObj:{
          "achievement_name_isDisabled": false,
            "type_of_achievement_isDisabled": false,
                "gold_coin_rewards_isDisabled": false,
        },
                          // 成就类型选项列表
      list_type_of_achievement: [],
                            };
  },
  methods: {
    /**
     * 提交前验证事件
     * @param {Object} 请求参数
     * @return {String} 验证成功返回null, 失败返回错误提示
     */
    async submit_check(param) {
                                                                      return null;
    },
    changeLog(v, value) {
      this.form[value] = v;
    },
    uploadFile_(type) {
      // #ifdef APP-VUE
      uni.chooseFile({
        count: 1,
        success: (chooseImageRes) => {
          const tempFilePaths = chooseImageRes.tempFilePaths;
          this.successChoose(tempFilePaths[0], type);
        },
      });
      // #endif
      // #ifdef !APP-VUE
      if (
        uni.getSystemInfoSync().uniPlatform != 'mp-weixin' ||
        uni.getSystemInfoSync().platform == 'devtools'
      ) {
        uni.chooseImage({
          count: 1,
          success: (chooseImageRes) => {
            const tempFilePaths = chooseImageRes.tempFilePaths;
            this.successChoose(tempFilePaths[0], type);
          },
        });
      } else {
        wx.chooseMessageFile({
          count: 1,
          success: (chooseImageRes) => {
            const tempFilePaths = chooseImageRes.tempFiles;
            this.successChoose(tempFilePaths[0].path, type);
          },
        });
      }
      // #endif
    },
    successChoose(filePath, type) {
      uni.uploadFile({
        url: this.$fullUrl('~/api/game_achievements/upload?'), //仅为示例，非真实的接口地址
        filePath,
        name: 'file',
        success: (uploadFileRes) => {
          if (uploadFileRes.data.error) {
            uni.showToast({ title: uploadFileRes.data.error.message, icon: 'none' });
          } else {
            this[type] = JSON.parse(uploadFileRes.data).result.url;
          }
        },
      });
    },
    close_(type) {
      if (type == 'achievement_name') this['achievement_name'] = this.form['achievement_name'] = "";
      if (type == 'type_of_achievement') this['type_of_achievement'] = this.form['type_of_achievement'] = "";
      if (type == 'score_settings') this['score_settings'] = this.form['score_settings'] = "";
      if (type == 'gold_coin_rewards') this['gold_coin_rewards'] = this.form['gold_coin_rewards'] = "";
    },
    submit_() {
      if (this['achievement_name'] !== null) this.form['achievement_name'] = this['achievement_name']
      if (this['type_of_achievement'] !== null) this.form['type_of_achievement'] = this['type_of_achievement']
      if (this['score_settings'] !== null) this.form['score_settings'] = this['score_settings']
      if (this['gold_coin_rewards'] !== null) this.form['gold_coin_rewards'] = this['gold_coin_rewards']
      console.log(this.form)
      this.submit();
    },
    /**
     * 上传文件
     * @param {Object} param文件参数
     */
    change_file(key_name) {
      var _self = this;
      // 选择图像方法
      uni.chooseFile({
        count: 1,
        sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
        sourceType: ['album'], //从相册选择
        success: (file) => {
          const tempFilePaths = file.tempFilePaths;
          const filePath = tempFilePaths[0];
          uploadFilePathApi(
            '/game_achievements/upload',
            filePath,
            undefined,
            {
              i_want_to_customize: 'test',
            },
            (task) => {
              task.onProgressUpdate((res) => {
                this.percent = res.progress;
                console.log('上传进度' + res.progress);
                console.log('已经上传的数据长度' + res.totalBytesSent);
                console.log('预期需要上传的数据总长度' + res.totalBytesExpectedToSend);
              });
            }
          ).then((res) => {
            const filename = res.result.url;
            this.form[key_name] = filename;
          });
        },
        error: function (e) {
          console.log(e);
        },
      });
    },
    /**
     * 上传图片
     * @param {Object} param文件参数
     */
    change_img(key_name) {
      var _self = this;
      _self.upload_img_flag = false;
      // 选择图像方法
      uni.chooseImage({
        count: 1,
        sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
        sourceType: ['album'], //从相册选择
        success: (file) => {
          const tempFilePaths = file.tempFilePaths;
          const filePath = tempFilePaths[0];
          uploadFilePathApi('/game_achievements/upload',filePath,undefined,undefined,
            (task) => {
              task.onProgressUpdate((res) => {
                this.percent = res.progress;
                console.log('上传进度' + res.progress);
                console.log('已经上传的数据长度' + res.totalBytesSent);
                console.log('预期需要上传的数据总长度' + res.totalBytesExpectedToSend);
              });
            }
          ).then((res) => {
            const filename = res.result.url;
            this.form[key_name] = filename;
          });
        },
        error: function (e) {
          console.log(e);
        },
      });
    },
    /**
     * 获取对象后获取缓存表单
     * @param {Object} json
     * @param {Object} func
     */
    get_obj_before(param) {
      var form = uni.db.get('form');
      if (form) {
        delete form.examine_state;
        delete form.examine_reply;
        this.obj = uni.push(this.obj, form);
        this.form = uni.push(this.form, form);
      }
      var arr = [];
      for (let key in form) {
        arr.push(key);
      }
      for (var i = 0; i < arr.length; i++) {
        this.disabledObj[arr[i] + '_isDisabled'] = true;
      }
                      uni.db.del('form');

      return param;
    },
    
        /**
     * 获取成就类型列表
     */
    async get_list_type_of_achievement() {
          ['累计','单局','彩蛋'].map((o) => this.list_type_of_achievement.push({value:o,text:o}));
            },
            
      
      
      /**
     * 获取对象之后
     * @param {Object} json
     * @param {Object} func
     */
    get_obj_after(json, func) {
                                                    },

    is_view() {
      var bl = this.user_group == '管理员';

      if (!bl) {
        bl = this.$check_action('/game_achievements/table', 'add');
        console.log(bl ? '你有表格添加权限视作有添加权限' : '你没有表格添加权限');
      }
      if (!bl) {
        bl = this.$check_action('/game_achievements/table', 'set');
        console.log(bl ? '你有表格添加权限视作有修改权限' : '你没有表格修改权限');
      }
      if (!bl) {
        bl = this.$check_action('/game_achievements/view', 'add');
        console.log(bl ? '你有视图添加权限视作有添加权限' : '你没有视图添加权限');
      }
      if (!bl) {
        bl = this.$check_action('/game_achievements/view', 'set');
        console.log(bl ? '你有视图修改权限视作有修改权限' : '你没有视图修改权限');
      }
      if (!bl) {
        bl = this.$check_action('/game_achievements/view', 'get');
        console.log(bl ? '你有视图查询权限视作有查询权限' : '你没有视图查询权限');
      }

      console.log(
        bl
          ? '具有当前页面的查看权，请注意这不代表你有字段的查看权'
          : '无权查看当前页，请注意即便有字段查询权限没有页面查询权限也不行'
      );

      return bl;
    },
  },
  created() {
          this.get_list_type_of_achievement();
              },
};
</script>

<style lang="scss" scoped>
  @import 'styles/pagesC/index.scss';
</style>