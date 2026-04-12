<template>
  <view class="page_diy_edit page_purchase_record_edit" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <!-- 返回箭头 -->
    <view class="back-arrow" @click="goBack"></view>
    <tn-nav-bar>购买记录</tn-nav-bar>
    <view class="page-custom " id="page_diy_edit">
      <view class="custom-from">
          <view v-if="$check_field('set', 'purchase_user') || $check_field('add', 'purchase_user') ||  $check_field('get', 'purchase_user')" class="custom-from-item">
          <view class="label">
            <text>购买用户</text>
          </view>
          <view class="content">
            <uni-data-select
                    id="form_purchase_user" v-model="form['purchase_user']"
                    :localdata="list_user_purchase_user"
                    :clear="!disabledObj['purchase_user_isDisabled']"
                    :disabled="disabledObj['purchase_user_isDisabled']"
                    v-if="(form['purchase_user'] && $check_field('set', 'purchase_user')) || (!form['purchase_user'] && $check_field('add', 'purchase_user'))" style="width: 100%;">
            </uni-data-select>
            <text v-else-if="$check_field('get', 'purchase_user')">
              {{form['purchase_user']}}
            </text>
          </view>
        </view>
            <view v-if="$check_field('set', 'player_screen_name') || $check_field('add', 'player_screen_name') ||  $check_field('get', 'player_screen_name')" class="custom-from-item">
          <view class="label">
            <text>
              玩家网名
            </text>
          </view>
              <!-- 文本 -->
                  <view class="content">
            <input type="text" id="form_player_screen_name" v-model="form['player_screen_name']" placeholder="请输入玩家网名" v-if="(form['player_screen_name'] && $check_field('set','player_screen_name')) || (!form['player_screen_name'] && $check_field('add','player_screen_name'))" :disabled="disabledObj['player_screen_name_isDisabled']" />
            <text v-else-if="$check_field('get','player_screen_name')">{{ form['player_screen_name'] }}</text>
          </view>
                    </view>
            <view v-if="$check_field('set', 'prop_name') || $check_field('add', 'prop_name') ||  $check_field('get', 'prop_name')" class="custom-from-item">
          <view class="label">
            <text>
              道具名称
            </text>
          </view>
              <!-- 文本 -->
                  <view class="content">
            <input type="text" id="form_prop_name" v-model="form['prop_name']" placeholder="请输入道具名称" v-if="(form['prop_name'] && $check_field('set','prop_name')) || (!form['prop_name'] && $check_field('add','prop_name'))" :disabled="disabledObj['prop_name_isDisabled']" />
            <text v-else-if="$check_field('get','prop_name')">{{ form['prop_name'] }}</text>
          </view>
                    </view>
            <view v-if="$check_field('set', 'props_picture') || $check_field('add', 'props_picture') ||  $check_field('get', 'props_picture')" class="custom-from-item">
          <view class="label">
            <text>
              道具图片
            </text>
          </view>
              <!-- 文本 -->
                  <view class="content">
            <input type="text" id="form_props_picture" v-model="form['props_picture']" placeholder="请输入道具图片" v-if="(form['props_picture'] && $check_field('set','props_picture')) || (!form['props_picture'] && $check_field('add','props_picture'))" :disabled="disabledObj['props_picture_isDisabled']" />
            <text v-else-if="$check_field('get','props_picture')">{{ form['props_picture'] }}</text>
          </view>
                    </view>
            <view v-if="$check_field('set', 'props_use') || $check_field('add', 'props_use') ||  $check_field('get', 'props_use')" class="custom-from-item">
          <view class="label">
            <text>
              道具用途
            </text>
          </view>
              <!-- 文本 -->
                  <view class="content">
            <input type="text" id="form_props_use" v-model="form['props_use']" placeholder="请输入道具用途" v-if="(form['props_use'] && $check_field('set','props_use')) || (!form['props_use'] && $check_field('add','props_use'))" :disabled="disabledObj['props_use_isDisabled']" />
            <text v-else-if="$check_field('get','props_use')">{{ form['props_use'] }}</text>
          </view>
                    </view>
            <view v-if="$check_field('set', 'gold_coins_required') || $check_field('add', 'gold_coins_required') ||  $check_field('get', 'gold_coins_required')" class="custom-from-item">
          <view class="label">
            <text>
              所需金币
            </text>
          </view>
              <!-- 文本 -->
                  <view class="content">
            <input type="text" id="form_gold_coins_required" v-model="form['gold_coins_required']" placeholder="请输入所需金币" v-if="(form['gold_coins_required'] && $check_field('set','gold_coins_required')) || (!form['gold_coins_required'] && $check_field('add','gold_coins_required'))" :disabled="disabledObj['gold_coins_required_isDisabled']" />
            <text v-else-if="$check_field('get','gold_coins_required')">{{ form['gold_coins_required'] }}</text>
          </view>
                    </view>
            <view v-if="$check_field('set', 'use_status') || $check_field('add', 'use_status') ||  $check_field('get', 'use_status')" class="custom-from-item">
          <view class="label">
            <text>
              使用状态
            </text>
          </view>
              <!-- 选项 -->
          <view class="content">
            <uni-data-select
                    id="form_use_status"
                    v-model="form['use_status']"
                    :localdata="list_use_status"
                    v-if="(form['use_status'] && $check_field('set','use_status')) || (!form['use_status'] && $check_field('add','use_status'))"
				    style="width: 100%;"
            ></uni-data-select>
            <text v-else-if="$check_field('get','use_status')">{{ form['use_status'] }}</text>
          </view>

            </view>
          <view class="form-footer">
          <view class="me-btn" @click="submit()">提交</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import mixin from '@/libs/mixins/page.js';
export default {
  mixins: [mixin],
  data() {
    return {
      url_get_obj: '~/api/purchase_record/get_obj?',
      url_add: '~/api/purchase_record/add?',
      url_set: '~/api/purchase_record/set?',
	  today: new Date().toISOString().split('T')[0],
      // 查询条件
      query: {
        purchase_record_id: 0,
          purchase_user: 0,
            player_screen_name: "",
            prop_name: "",
            props_picture: "",
            props_use: "",
            gold_coins_required: "",
            use_status: "",
        },
      obj: {
        purchase_record_id: 0,
          purchase_user: 0, // 购买用户
            player_screen_name:  '', // 玩家网名
            prop_name:  '', // 道具名称
            props_picture:  '', // 道具图片
            props_use:  '', // 道具用途
            gold_coins_required:  '', // 所需金币
            use_status:  '', // 使用状态
        },

      // 表单字段
      form: {
        purchase_record_id: 0,
          purchase_user: 0, // 购买用户
            player_screen_name:  '', // 玩家网名
            prop_name:  '', // 道具名称
            props_picture:  '', // 道具图片
            props_use:  '', // 道具用途
            gold_coins_required:  '', // 所需金币
            use_status:  '', // 使用状态
        },
      disabledObj: {
          purchase_user_isDisabled: false,
            player_screen_name_isDisabled: false,
            prop_name_isDisabled: false,
            props_picture_isDisabled: false,
            props_use_isDisabled: false,
            gold_coins_required_isDisabled: false,
            use_status_isDisabled: false,
        },
              // 用户列表
      list_user_purchase_user: [],
                                                              list_use_status: [],
              field: 'purchase_record_id',
      table_key: 'purchase_record',

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
            async get_user_session_purchase_user(){
        var _this = this;
        var json = await this.$get("~/api/user_group/get_obj?name=游戏玩家");
        if(json.result && json.result.obj){
          var source_table = json.result.obj.source_table;
          var user_id = _this.userInfo.user_id;
          if (user_id){
            var url = "~/api/"+source_table+"/get_obj"
            this.$get(url, { user_id: _this.userInfo.user_id }, function (res) {
              if (res.result && res.result.obj) {
                var arr = []
                for (let key in res.result.obj) {
                  arr.push(key)
                }
                var arrForm = []
                for (let key in _this.form) {
                  arrForm.push(key)
                }
                _this.form["purchase_user"] = user_id
                _this.disabledObj['purchase_user' + '_isDisabled'] = true
                for (var i=0;i<arr.length;i++){
                  if (arr[i]!=='examine_state' && arr[i]!=='examine_reply') {
                    for (var j = 0; j < arrForm.length; j++) {
                      if (arr[i] === arrForm[j]) {
                        if (arr[i] !== "purchase_user") {
                          _this.form[arrForm[j]] = res.result.obj[arr[i]]
                          _this.disabledObj[arrForm[j] + '_isDisabled'] = true
                          break;
                        }
                      }
                    }
                  }
                }
              }
            });
          }
        }
        else if(json.error){
          console.error(json.error);
        }
    },
            
                
                
                
                
                    /**
    * 获取使用状态列表
    */
    async get_list_use_status() {
              ['可用','禁用'].map((o) => this.list_use_status.push({value:o,text:o}));
                },
                    
                change_img(key_name) {
      var _self = this;
      _self.upload_img_flag = false;
      // 选择图像方法
      uni.chooseImage({
        count: 1,
        sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
        sourceType: ['album'], //从相册选择
        success: function (res) {
          const tempFilePaths = res.tempFilePaths;
          const uploadTask = uni.uploadFile({
            url: _self.$fullUrl('~/api/purchase_record/upload?'),
            filePath: tempFilePaths[0],
            name: 'file',
            formData: {
              purchase_record: 'test',
            },
            header: {
              'x-auth-token': _self.$store.state.app.userInfo.token,
            },
            success: function (uploadFileRes) {
              var filename = JSON.parse(uploadFileRes.data).result.url;
              var img_url = filename;
              _self.form[key_name] = img_url;
            },
          });

          uploadTask.onProgressUpdate(function (res) {
            _self.percent = res.progress;
            console.log('上传进度' + res.progress);
            console.log('已经上传的数据长度' + res.totalBytesSent);
            console.log('预期需要上传的数据总长度' + res.totalBytesExpectedToSend);
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
     * 获取对象后获取缓存表单
     * @param {Object} json
     * @param {Object} func
     */
    get_obj_after(json, func) {
      var form = uni.db.get('form');
      var obj = Object.assign({}, form, this.obj);
      if (form) {
        this.obj = uni.push(this.obj, obj);
      }
      if (form) {
        this.form = uni.push(this.form, form);
      }
      if (func) {
        func(json);
      }
    },

  },
  onLoad() {
        this.get_user_session_purchase_user();
        this.get_list_user_purchase_user();
                                      this.get_list_use_status();
        },
};
</script>

