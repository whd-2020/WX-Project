<template>
  <view class="page_diy_view page_props_view" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <!-- 返回箭头 -->
    <view class="back-arrow" @click="goBack"></view>
    <tn-nav-bar>道具商店详情</tn-nav-bar>
    <view id="page_diy_view" class="page-from">
      <uni-forms :modelValue="form" v-if="is_view()" labelWidth="70px">

        <uni-forms-item v-if="$check_field('get','prop_name') || ($check_field('add','prop_name') || $check_field('set','prop_name'))" label="道具名称" name="prop_name">
                      <uni-easyinput type="text" v-model="form['prop_name']" v-if="(form['props_id'] && $check_field('set','prop_name')) || (!form['props_id'] && $check_field('add','prop_name'))" :disabled="disabledObj['prop_name_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','prop_name')">
            {{ form['prop_name'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','props_picture') || ($check_field('add','props_picture') || $check_field('set','props_picture'))" label="道具图片" name="props_picture">
              <!-- 修改权限 -->
          <view class="from-img" v-if="form['props_picture'] && $check_field('set', 'props_picture')">
            <image v-if="disabledObj['props_picture_isDisabled']" :src="$fullImgUrl(form['props_picture'])" />
            <image v-if="!disabledObj['props_picture_isDisabled']" :src="$fullImgUrl(form['props_picture'])" @click="change_img('props_picture')" />
          </view>
          <!-- 添加权限 -->
          <view class="from-img" v-else-if="!form['props_picture'] && ($check_field('add','props_picture') || $check_field('set','props_picture'))">
            <view v-if="disabledObj['props_picture_isDisabled']" class="add-img">
              <text>+</text>
            </view>
            <view v-if="!disabledObj['props_picture_isDisabled']" class="add-img" @click="change_img('props_picture')">
              <text>+</text>
            </view>
          </view>
          <!-- 查询权限 -->
          <view class="from-img" v-else-if="$check_field('get', 'props_picture')">
            <image :src="$fullImgUrl(form['props_picture'])" />
          </view>
            </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','gold_coins_required') || ($check_field('add','gold_coins_required') || $check_field('set','gold_coins_required'))" label="所需金币" name="gold_coins_required">
                      <uni-easyinput type="text" v-model="form['gold_coins_required']" v-if="(form['props_id'] && $check_field('set','gold_coins_required')) || (!form['props_id'] && $check_field('add','gold_coins_required'))" :disabled="disabledObj['gold_coins_required_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','gold_coins_required')">
            {{ form['gold_coins_required'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','props_use') || ($check_field('add','props_use') || $check_field('set','props_use'))" label="道具用途" name="props_use">
              <uni-data-select
              v-model="form.props_use"
              :localdata="list_props_use"
              :clear="!disabledObj['props_use_isDisabled']"
              :disabled="disabledObj['props_use_isDisabled']"
              v-if="(form['props_id'] && $check_field('set', 'props_use')) || (!form['props_id'] && $check_field('add', 'props_use'))"
			  style="width: 100%;"
          ></uni-data-select>
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get', 'props_use')">
            {{ form['props_use'] }}
          </text>
            </uni-forms-item>
      </uni-forms>
      <view class="form-footer" v-if="$check_action('/props/view','set') || ($check_action('/props/view','add') || $check_option('/props/table','examine'))">
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
      field: "props_id",
      url_add: "~/api/props/add?",
      url_set: "~/api/props/set?",
      url_get_obj: "~/api/props/get_obj?",
      url_upload: "~/api/props/upload?",
      prop_name: null,
      props_picture: null,
      gold_coins_required: null,
      props_use: null,
      query: {
        "props_id": 0,
      },
      form: {
          "prop_name":  '', // 道具名称
            "props_picture":  '', // 道具图片
            "gold_coins_required":  '', // 所需金币
            "props_use":  '', // 道具用途
          "props_id": 0, // ID
      },
      disabledObj:{
          "prop_name_isDisabled": false,
            "props_picture_isDisabled": false,
            "gold_coins_required_isDisabled": false,
            "props_use_isDisabled": false,
        },
                                          // 道具用途选项列表
      list_props_use: [],
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
        url: this.$fullUrl('~/api/props/upload?'), //仅为示例，非真实的接口地址
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
      if (type == 'prop_name') this['prop_name'] = this.form['prop_name'] = "";
      if (type == 'props_picture') this['props_picture'] = this.form['props_picture'] = "";
      if (type == 'gold_coins_required') this['gold_coins_required'] = this.form['gold_coins_required'] = "";
      if (type == 'props_use') this['props_use'] = this.form['props_use'] = "";
    },
    submit_() {
      if (this['prop_name'] !== null) this.form['prop_name'] = this['prop_name']
      if (this['props_picture'] !== null) this.form['props_picture'] = this['props_picture']
      if (this['gold_coins_required'] !== null) this.form['gold_coins_required'] = this['gold_coins_required']
      if (this['props_use'] !== null) this.form['props_use'] = this['props_use']
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
            '/props/upload',
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
          uploadFilePathApi('/props/upload',filePath,undefined,undefined,
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
     * 获取道具用途列表
     */
    async get_list_props_use() {
          ['提示','打乱','增加步数'].map((o) => this.list_props_use.push({value:o,text:o}));
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
        bl = this.$check_action('/props/table', 'add');
        console.log(bl ? '你有表格添加权限视作有添加权限' : '你没有表格添加权限');
      }
      if (!bl) {
        bl = this.$check_action('/props/table', 'set');
        console.log(bl ? '你有表格添加权限视作有修改权限' : '你没有表格修改权限');
      }
      if (!bl) {
        bl = this.$check_action('/props/view', 'add');
        console.log(bl ? '你有视图添加权限视作有添加权限' : '你没有视图添加权限');
      }
      if (!bl) {
        bl = this.$check_action('/props/view', 'set');
        console.log(bl ? '你有视图修改权限视作有修改权限' : '你没有视图修改权限');
      }
      if (!bl) {
        bl = this.$check_action('/props/view', 'get');
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
                  this.get_list_props_use();
      },
};
</script>

<style lang="scss" scoped>
  @import 'styles/pagesC/index.scss';
</style>