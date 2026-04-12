<template>
  <view class="page_diy_view page_purchase_record_view" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <!-- 返回箭头 -->
    <view class="back-arrow" @click="goBack"></view>
    <tn-nav-bar>购买记录详情</tn-nav-bar>
    <view id="page_diy_view" class="page-from">
      <uni-forms :modelValue="form" v-if="is_view()" labelWidth="70px">

        <uni-forms-item v-if="$check_field('get','purchase_user') || ($check_field('add','purchase_user') || $check_field('set','purchase_user'))" label="购买用户" name="purchase_user">
              <uni-data-select
              id="form_purchase_user"
              v-model="form['purchase_user']"
              :localdata="list_user_purchase_user"
              :clear="!disabledObj['purchase_user_isDisabled']"
              :disabled="disabledObj['purchase_user_isDisabled']"
              v-if="(form['purchase_record_id'] && $check_field('set', 'purchase_user')) || (!form['purchase_record_id'] && $check_field('add', 'purchase_user'))"
			   style="width: 100%;"
          ></uni-data-select>
          <uni-data-select
              v-model="form['purchase_user']"
              :localdata="list_user_purchase_user"
              :clear="false"
              :disabled="true"
              v-else-if="$check_field('get', 'purchase_user')"
              id="purchase_user"
			  style="width: 100%;"
          ></uni-data-select>
            </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','player_screen_name') || ($check_field('add','player_screen_name') || $check_field('set','player_screen_name'))" label="玩家网名" name="player_screen_name">
                      <uni-easyinput type="text" v-model="form['player_screen_name']" v-if="(form['purchase_record_id'] && $check_field('set','player_screen_name')) || (!form['purchase_record_id'] && $check_field('add','player_screen_name'))" :disabled="disabledObj['player_screen_name_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','player_screen_name')">
            {{ form['player_screen_name'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','prop_name') || ($check_field('add','prop_name') || $check_field('set','prop_name'))" label="道具名称" name="prop_name">
                      <uni-easyinput type="text" v-model="form['prop_name']" v-if="(form['purchase_record_id'] && $check_field('set','prop_name')) || (!form['purchase_record_id'] && $check_field('add','prop_name'))" :disabled="disabledObj['prop_name_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','prop_name')">
            {{ form['prop_name'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','props_picture') || ($check_field('add','props_picture') || $check_field('set','props_picture'))" label="道具图片" name="props_picture">
                      <uni-easyinput type="text" v-model="form['props_picture']" v-if="(form['purchase_record_id'] && $check_field('set','props_picture')) || (!form['purchase_record_id'] && $check_field('add','props_picture'))" :disabled="disabledObj['props_picture_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','props_picture')">
            {{ form['props_picture'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','props_use') || ($check_field('add','props_use') || $check_field('set','props_use'))" label="道具用途" name="props_use">
                      <uni-easyinput type="text" v-model="form['props_use']" v-if="(form['purchase_record_id'] && $check_field('set','props_use')) || (!form['purchase_record_id'] && $check_field('add','props_use'))" :disabled="disabledObj['props_use_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','props_use')">
            {{ form['props_use'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','gold_coins_required') || ($check_field('add','gold_coins_required') || $check_field('set','gold_coins_required'))" label="所需金币" name="gold_coins_required">
                      <uni-easyinput type="text" v-model="form['gold_coins_required']" v-if="(form['purchase_record_id'] && $check_field('set','gold_coins_required')) || (!form['purchase_record_id'] && $check_field('add','gold_coins_required'))" :disabled="disabledObj['gold_coins_required_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','gold_coins_required')">
            {{ form['gold_coins_required'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','use_status') || ($check_field('add','use_status') || $check_field('set','use_status'))" label="使用状态" name="use_status">
              <uni-data-select
              v-model="form.use_status"
              :localdata="list_use_status"
              :clear="!disabledObj['use_status_isDisabled']"
              :disabled="disabledObj['use_status_isDisabled']"
              v-if="(form['purchase_record_id'] && $check_field('set', 'use_status')) || (!form['purchase_record_id'] && $check_field('add', 'use_status'))"
			  style="width: 100%;"
          ></uni-data-select>
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get', 'use_status')">
            {{ form['use_status'] }}
          </text>
            </uni-forms-item>
      </uni-forms>
      <view class="form-footer" v-if="$check_action('/purchase_record/view','set') || ($check_action('/purchase_record/view','add') || $check_option('/purchase_record/table','examine'))">
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
      field: "purchase_record_id",
      url_add: "~/api/purchase_record/add?",
      url_set: "~/api/purchase_record/set?",
      url_get_obj: "~/api/purchase_record/get_obj?",
      url_upload: "~/api/purchase_record/upload?",
      purchase_user: null,
      player_screen_name: null,
      prop_name: null,
      props_picture: null,
      props_use: null,
      gold_coins_required: null,
      use_status: null,
      query: {
        "purchase_record_id": 0,
      },
      form: {
          "purchase_user": 0, // 购买用户
            "player_screen_name":  '', // 玩家网名
            "prop_name":  '', // 道具名称
            "props_picture":  '', // 道具图片
            "props_use":  '', // 道具用途
            "gold_coins_required":  '', // 所需金币
            "use_status":  '', // 使用状态
          "purchase_record_id": 0, // ID
      },
      disabledObj:{
          "purchase_user_isDisabled": false,
            "player_screen_name_isDisabled": false,
            "prop_name_isDisabled": false,
            "props_picture_isDisabled": false,
            "props_use_isDisabled": false,
            "gold_coins_required_isDisabled": false,
            "use_status_isDisabled": false,
        },
              // 用户列表
      list_user_purchase_user: [],
                  // 用户组
      group_user_purchase_user: "",
                                                                      // 使用状态选项列表
      list_use_status: [],
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
        url: this.$fullUrl('~/api/purchase_record/upload?'), //仅为示例，非真实的接口地址
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
      if (type == 'purchase_user') this['purchase_user'] = this.form['purchase_user'] = "";
      if (type == 'player_screen_name') this['player_screen_name'] = this.form['player_screen_name'] = "";
      if (type == 'prop_name') this['prop_name'] = this.form['prop_name'] = "";
      if (type == 'props_picture') this['props_picture'] = this.form['props_picture'] = "";
      if (type == 'props_use') this['props_use'] = this.form['props_use'] = "";
      if (type == 'gold_coins_required') this['gold_coins_required'] = this.form['gold_coins_required'] = "";
      if (type == 'use_status') this['use_status'] = this.form['use_status'] = "";
    },
    submit_() {
      if (this['purchase_user'] !== null) this.form['purchase_user'] = this['purchase_user']
      if (this['player_screen_name'] !== null) this.form['player_screen_name'] = this['player_screen_name']
      if (this['prop_name'] !== null) this.form['prop_name'] = this['prop_name']
      if (this['props_picture'] !== null) this.form['props_picture'] = this['props_picture']
      if (this['props_use'] !== null) this.form['props_use'] = this['props_use']
      if (this['gold_coins_required'] !== null) this.form['gold_coins_required'] = this['gold_coins_required']
      if (this['use_status'] !== null) this.form['use_status'] = this['use_status']
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
            '/purchase_record/upload',
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
          uploadFilePathApi('/purchase_record/upload',filePath,undefined,undefined,
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
     * 获取游戏玩家用户列表
     */
    async get_list_user_purchase_user() {
      var json = await this.$get("~/api/user/get_list?user_group=游戏玩家");
      if(json.result && json.result.list){
        json.result.list.map((o) => this.list_user_purchase_user.push({value:o.user_id,text:o.nickname + '-' + o.username}));
      }
      else if(json.error){
        console.error(json.error);
      }
    },
        /**
     * 获取游戏玩家用户组
     */
    async get_group_user_purchase_user() {
      this.form["purchase_user"] = this.user.user_id;
      var json = await this.$get("~/api/user_group/get_obj?name=游戏玩家");
      if(json.result && json.result.obj){
        this.group_user_purchase_user = json.result.obj;
        this.get_user_session_purchase_user(this.form['purchase_user'])
      }
      else if(json.error){
        console.error(json.error);
      }
    },
    get_user_session_purchase_user(id){
      var _this = this;
      var user_id = {"user_id":_this.user.user_id}
      var url = "~/api/"+_this.group_user_purchase_user.source_table+"/get_obj"
      this.$get(url, user_id, function(res) {
        if (res.result && res.result.obj) {
          var arr = []
          for (let key in res.result.obj) {
            arr.push(key)
          }
          var arrForm = []
          for (let key in _this.form) {
            arrForm.push(key)
          }
          _this.form["purchase_user"] = _this.user.user_id
          _this.disabledObj['purchase_user' + '_isDisabled'] = true
          for (var i=0;i<arr.length;i++){
            if (arr[i]!=='examine_state' && arr[i]!=='examine_reply') {
              for (var j = 0; j < arrForm.length; j++) {
                if (arr[i] === arrForm[j]) {
                  if (arr[i] !== "purchase_user") {
                    _this.form[arrForm[j]] = res.result.obj[arr[i]]
                    _this.disabledObj[arrForm[j] + '_isDisabled'] = true
                    break;
                  } else {
                    _this.disabledObj[arrForm[j] + '_isDisabled'] = true
                  }
                }
              }
            }
          }
        }
      });
    },
      
      
      
      
      
      
        /**
     * 获取使用状态列表
     */
    async get_list_use_status() {
          ['可用','禁用'].map((o) => this.list_use_status.push({value:o,text:o}));
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
        bl = this.$check_action('/purchase_record/table', 'add');
        console.log(bl ? '你有表格添加权限视作有添加权限' : '你没有表格添加权限');
      }
      if (!bl) {
        bl = this.$check_action('/purchase_record/table', 'set');
        console.log(bl ? '你有表格添加权限视作有修改权限' : '你没有表格修改权限');
      }
      if (!bl) {
        bl = this.$check_action('/purchase_record/view', 'add');
        console.log(bl ? '你有视图添加权限视作有添加权限' : '你没有视图添加权限');
      }
      if (!bl) {
        bl = this.$check_action('/purchase_record/view', 'set');
        console.log(bl ? '你有视图修改权限视作有修改权限' : '你没有视图修改权限');
      }
      if (!bl) {
        bl = this.$check_action('/purchase_record/view', 'get');
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
        this.get_list_user_purchase_user();
          this.get_group_user_purchase_user();
                                  this.get_list_use_status();
      },
};
</script>

<style lang="scss" scoped>
  @import 'styles/pagesC/index.scss';
</style>