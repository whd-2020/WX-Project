<template>
  <view class="page_diy_view page_game_levels_view" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <tn-nav-bar>游戏关卡详情</tn-nav-bar>
    <view id="page_diy_view" class="page-from">
      <uni-forms :modelValue="form" v-if="is_view()" labelWidth="70px">

        <uni-forms-item v-if="$check_field('get','level_name') || ($check_field('add','level_name') || $check_field('set','level_name'))" label="关卡名称" name="level_name">
                      <uni-easyinput type="text" v-model="form['level_name']" v-if="(form['game_levels_id'] && $check_field('set','level_name')) || (!form['game_levels_id'] && $check_field('add','level_name'))" :disabled="disabledObj['level_name_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','level_name')">
            {{ form['level_name'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','level_picture') || ($check_field('add','level_picture') || $check_field('set','level_picture'))" label="关卡图片" name="level_picture">
              <!-- 修改权限 -->
          <view class="from-img" v-if="form['level_picture'] && $check_field('set', 'level_picture')">
            <image v-if="disabledObj['level_picture_isDisabled']" :src="$fullImgUrl(form['level_picture'])" />
            <image v-if="!disabledObj['level_picture_isDisabled']" :src="$fullImgUrl(form['level_picture'])" @click="change_img('level_picture')" />
          </view>
          <!-- 添加权限 -->
          <view class="from-img" v-else-if="!form['level_picture'] && ($check_field('add','level_picture') || $check_field('set','level_picture'))">
            <view v-if="disabledObj['level_picture_isDisabled']" class="add-img">
              <text>+</text>
            </view>
            <view v-if="!disabledObj['level_picture_isDisabled']" class="add-img" @click="change_img('level_picture')">
              <text>+</text>
            </view>
          </view>
          <!-- 查询权限 -->
          <view class="from-img" v-else-if="$check_field('get', 'level_picture')">
            <image :src="$fullImgUrl(form['level_picture'])" />
          </view>
            </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','limit_number_of_steps') || ($check_field('add','limit_number_of_steps') || $check_field('set','limit_number_of_steps'))" label="限制步数" name="limit_number_of_steps">
                      <uni-easyinput type="text" v-model="form['limit_number_of_steps']" v-if="(form['game_levels_id'] && $check_field('set','limit_number_of_steps')) || (!form['game_levels_id'] && $check_field('add','limit_number_of_steps'))" :disabled="disabledObj['limit_number_of_steps_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','limit_number_of_steps')">
            {{ form['limit_number_of_steps'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','elimination_quantity') || ($check_field('add','elimination_quantity') || $check_field('set','elimination_quantity'))" label="消除数量" name="elimination_quantity">
                      <uni-easyinput type="text" v-model="form['elimination_quantity']" v-if="(form['game_levels_id'] && $check_field('set','elimination_quantity')) || (!form['game_levels_id'] && $check_field('add','elimination_quantity'))" :disabled="disabledObj['elimination_quantity_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','elimination_quantity')">
            {{ form['elimination_quantity'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','game_duration') || ($check_field('add','game_duration') || $check_field('set','game_duration'))" label="游戏时长" name="game_duration">
                      <uni-easyinput type="text" v-model="form['game_duration']" v-if="(form['game_levels_id'] && $check_field('set','game_duration')) || (!form['game_levels_id'] && $check_field('add','game_duration'))" :disabled="disabledObj['game_duration_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','game_duration')">
            {{ form['game_duration'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','gold_coin_rewards') || ($check_field('add','gold_coin_rewards') || $check_field('set','gold_coin_rewards'))" label="金币奖励" name="gold_coin_rewards">
                      <uni-easyinput type="text" v-model="form['gold_coin_rewards']" v-if="(form['game_levels_id'] && $check_field('set','gold_coin_rewards')) || (!form['game_levels_id'] && $check_field('add','gold_coin_rewards'))" :disabled="disabledObj['gold_coin_rewards_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','gold_coin_rewards')">
            {{ form['gold_coin_rewards'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','level_description') || ($check_field('add','level_description') || $check_field('set','level_description'))" label="关卡说明" name="level_description">
              <uni-easyinput type="textarea" v-model="form['level_description']" v-if="(form['game_levels_id'] && $check_field('set', 'level_description')) || (!form['game_levels_id'] && $check_field('add', 'level_description'))" :disabled="disabledObj['level_description_isDisabled']"/>
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get', 'level_description')">
            {{ form['level_description'] }}
          </text>
            </uni-forms-item>
      </uni-forms>
      <view class="form-footer" v-if="$check_action('/game_levels/view','set') || ($check_action('/game_levels/view','add') || $check_option('/game_levels/table','examine'))">
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
      field: "game_levels_id",
      url_add: "~/api/game_levels/add?",
      url_set: "~/api/game_levels/set?",
      url_get_obj: "~/api/game_levels/get_obj?",
      url_upload: "~/api/game_levels/upload?",
      level_name: null,
      level_picture: null,
      limit_number_of_steps: null,
      elimination_quantity: null,
      game_duration: null,
      gold_coin_rewards: null,
      level_description: null,
      query: {
        "game_levels_id": 0,
      },
      form: {
          "level_name":  '', // 关卡名称
            "level_picture":  '', // 关卡图片
            "limit_number_of_steps":  0 , // 限制步数
            "elimination_quantity":  0 , // 消除数量
            "game_duration":  0 , // 游戏时长
            "gold_coin_rewards":  0 , // 金币奖励
            "level_description":  '', // 关卡说明
          "game_levels_id": 0, // ID
      },
      disabledObj:{
          "level_name_isDisabled": false,
            "level_picture_isDisabled": false,
                            "level_description_isDisabled": false,
        },
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
        url: this.$fullUrl('~/api/game_levels/upload?'), //仅为示例，非真实的接口地址
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
      if (type == 'level_name') this['level_name'] = this.form['level_name'] = "";
      if (type == 'level_picture') this['level_picture'] = this.form['level_picture'] = "";
      if (type == 'limit_number_of_steps') this['limit_number_of_steps'] = this.form['limit_number_of_steps'] = "";
      if (type == 'elimination_quantity') this['elimination_quantity'] = this.form['elimination_quantity'] = "";
      if (type == 'game_duration') this['game_duration'] = this.form['game_duration'] = "";
      if (type == 'gold_coin_rewards') this['gold_coin_rewards'] = this.form['gold_coin_rewards'] = "";
      if (type == 'level_description') this['level_description'] = this.form['level_description'] = "";
    },
    submit_() {
      if (this['level_name'] !== null) this.form['level_name'] = this['level_name']
      if (this['level_picture'] !== null) this.form['level_picture'] = this['level_picture']
      if (this['limit_number_of_steps'] !== null) this.form['limit_number_of_steps'] = this['limit_number_of_steps']
      if (this['elimination_quantity'] !== null) this.form['elimination_quantity'] = this['elimination_quantity']
      if (this['game_duration'] !== null) this.form['game_duration'] = this['game_duration']
      if (this['gold_coin_rewards'] !== null) this.form['gold_coin_rewards'] = this['gold_coin_rewards']
      if (this['level_description'] !== null) this.form['level_description'] = this['level_description']
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
            '/game_levels/upload',
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
          uploadFilePathApi('/game_levels/upload',filePath,undefined,undefined,
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
     * 获取对象之后
     * @param {Object} json
     * @param {Object} func
     */
    get_obj_after(json, func) {
                                                                                        },

    is_view() {
      var bl = this.user_group == '管理员';

      if (!bl) {
        bl = this.$check_action('/game_levels/table', 'add');
        console.log(bl ? '你有表格添加权限视作有添加权限' : '你没有表格添加权限');
      }
      if (!bl) {
        bl = this.$check_action('/game_levels/table', 'set');
        console.log(bl ? '你有表格添加权限视作有修改权限' : '你没有表格修改权限');
      }
      if (!bl) {
        bl = this.$check_action('/game_levels/view', 'add');
        console.log(bl ? '你有视图添加权限视作有添加权限' : '你没有视图添加权限');
      }
      if (!bl) {
        bl = this.$check_action('/game_levels/view', 'set');
        console.log(bl ? '你有视图修改权限视作有修改权限' : '你没有视图修改权限');
      }
      if (!bl) {
        bl = this.$check_action('/game_levels/view', 'get');
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
                              },
};
</script>

<style lang="scss" scoped>
  @import 'styles/pagesC/index.scss';
</style>