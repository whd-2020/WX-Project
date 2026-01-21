<template>
  <view class="page_diy_view page_game_record_view" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <tn-nav-bar>游戏记录详情</tn-nav-bar>
    <view id="page_diy_view" class="page-from">
      <uni-forms :modelValue="form" v-if="is_view()" labelWidth="70px">

        <uni-forms-item v-if="$check_field('get','level_name') || ($check_field('add','level_name') || $check_field('set','level_name'))" label="关卡名称" name="level_name">
                      <uni-easyinput type="text" v-model="form['level_name']" v-if="(form['game_record_id'] && $check_field('set','level_name')) || (!form['game_record_id'] && $check_field('add','level_name'))" :disabled="disabledObj['level_name_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','level_name')">
            {{ form['level_name'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','gamer') || ($check_field('add','gamer') || $check_field('set','gamer'))" label="游戏玩家" name="gamer">
              <uni-data-select
              id="form_gamer"
              v-model="form['gamer']"
              :localdata="list_user_gamer"
              :clear="!disabledObj['gamer_isDisabled']"
              :disabled="disabledObj['gamer_isDisabled']"
              v-if="(form['game_record_id'] && $check_field('set', 'gamer')) || (!form['game_record_id'] && $check_field('add', 'gamer'))"
			   style="width: 100%;"
          ></uni-data-select>
          <uni-data-select
              v-model="form['gamer']"
              :localdata="list_user_gamer"
              :clear="false"
              :disabled="true"
              v-else-if="$check_field('get', 'gamer')"
              id="gamer"
			  style="width: 100%;"
          ></uni-data-select>
            </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','player_avatar') || ($check_field('add','player_avatar') || $check_field('set','player_avatar'))" label="玩家头像" name="player_avatar">
              <!-- 修改权限 -->
          <view class="from-img" v-if="form['player_avatar'] && $check_field('set', 'player_avatar')">
            <image v-if="disabledObj['player_avatar_isDisabled']" :src="$fullImgUrl(form['player_avatar'])" />
            <image v-if="!disabledObj['player_avatar_isDisabled']" :src="$fullImgUrl(form['player_avatar'])" @click="change_img('player_avatar')" />
          </view>
          <!-- 添加权限 -->
          <view class="from-img" v-else-if="!form['player_avatar'] && ($check_field('add','player_avatar') || $check_field('set','player_avatar'))">
            <view v-if="disabledObj['player_avatar_isDisabled']" class="add-img">
              <text>+</text>
            </view>
            <view v-if="!disabledObj['player_avatar_isDisabled']" class="add-img" @click="change_img('player_avatar')">
              <text>+</text>
            </view>
          </view>
          <!-- 查询权限 -->
          <view class="from-img" v-else-if="$check_field('get', 'player_avatar')">
            <image :src="$fullImgUrl(form['player_avatar'])" />
          </view>
            </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','player_screen_name') || ($check_field('add','player_screen_name') || $check_field('set','player_screen_name'))" label="玩家网名" name="player_screen_name">
                      <uni-easyinput type="text" v-model="form['player_screen_name']" v-if="(form['game_record_id'] && $check_field('set','player_screen_name')) || (!form['game_record_id'] && $check_field('add','player_screen_name'))" :disabled="disabledObj['player_screen_name_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','player_screen_name')">
            {{ form['player_screen_name'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','gold_coin_rewards') || ($check_field('add','gold_coin_rewards') || $check_field('set','gold_coin_rewards'))" label="金币奖励" name="gold_coin_rewards">
                      <uni-easyinput type="text" v-model="form['gold_coin_rewards']" v-if="(form['game_record_id'] && $check_field('set','gold_coin_rewards')) || (!form['game_record_id'] && $check_field('add','gold_coin_rewards'))" :disabled="disabledObj['gold_coin_rewards_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','gold_coin_rewards')">
            {{ form['gold_coin_rewards'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','game_time') || ($check_field('add','game_time') || $check_field('set','game_time'))" label="游戏时间" name="game_time">
                      <uni-easyinput type="text" v-model="form['game_time']" v-if="(form['game_record_id'] && $check_field('set','game_time')) || (!form['game_record_id'] && $check_field('add','game_time'))" :disabled="disabledObj['game_time_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','game_time')">
            {{ form['game_time'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','number_of_steps_used') || ($check_field('add','number_of_steps_used') || $check_field('set','number_of_steps_used'))" label="使用步数" name="number_of_steps_used">
                      <uni-easyinput type="text" v-model="form['number_of_steps_used']" v-if="(form['game_record_id'] && $check_field('set','number_of_steps_used')) || (!form['game_record_id'] && $check_field('add','number_of_steps_used'))" :disabled="disabledObj['number_of_steps_used_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','number_of_steps_used')">
            {{ form['number_of_steps_used'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','total_game_score') || ($check_field('add','total_game_score') || $check_field('set','total_game_score'))" label="游戏总分" name="total_game_score">
                      <uni-easyinput type="text" v-model="form['total_game_score']" v-if="(form['game_record_id'] && $check_field('set','total_game_score')) || (!form['game_record_id'] && $check_field('add','total_game_score'))" :disabled="disabledObj['total_game_score_isDisabled']" />
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get','total_game_score')">
            {{ form['total_game_score'] }}
          </text>
                    </uni-forms-item>
        <uni-forms-item v-if="$check_field('get','clearance_status') || ($check_field('add','clearance_status') || $check_field('set','clearance_status'))" label="通关状态" name="clearance_status">
              <uni-data-select
              v-model="form.clearance_status"
              :localdata="list_clearance_status"
              :clear="!disabledObj['clearance_status_isDisabled']"
              :disabled="disabledObj['clearance_status_isDisabled']"
              v-if="(form['game_record_id'] && $check_field('set', 'clearance_status')) || (!form['game_record_id'] && $check_field('add', 'clearance_status'))"
			  style="width: 100%;"
          ></uni-data-select>
          <!-- 仅查看 -->
          <text v-else-if="$check_field('get', 'clearance_status')">
            {{ form['clearance_status'] }}
          </text>
            </uni-forms-item>
      </uni-forms>
      <view class="form-footer" v-if="$check_action('/game_record/view','set') || ($check_action('/game_record/view','add') || $check_option('/game_record/table','examine'))">
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
      field: "game_record_id",
      url_add: "~/api/game_record/add?",
      url_set: "~/api/game_record/set?",
      url_get_obj: "~/api/game_record/get_obj?",
      url_upload: "~/api/game_record/upload?",
      level_name: null,
      gamer: null,
      player_avatar: null,
      player_screen_name: null,
      gold_coin_rewards: null,
      game_time: null,
      number_of_steps_used: null,
      total_game_score: null,
      clearance_status: null,
      query: {
        "game_record_id": 0,
      },
      form: {
          "level_name":  '', // 关卡名称
            "gamer": 0, // 游戏玩家
            "player_avatar":  '', // 玩家头像
            "player_screen_name":  '', // 玩家网名
            "gold_coin_rewards":  0 , // 金币奖励
            "game_time":  0 , // 游戏时间
            "number_of_steps_used":  0 , // 使用步数
            "total_game_score":  0 , // 游戏总分
            "clearance_status":  '', // 通关状态
          "game_record_id": 0, // ID
      },
      disabledObj:{
          "level_name_isDisabled": false,
            "gamer_isDisabled": false,
            "player_avatar_isDisabled": false,
            "player_screen_name_isDisabled": false,
                            "clearance_status_isDisabled": false,
        },
                      // 用户列表
      list_user_gamer: [],
                  // 用户组
      group_user_gamer: "",
                                                                              // 通关状态选项列表
      list_clearance_status: [],
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
        url: this.$fullUrl('~/api/game_record/upload?'), //仅为示例，非真实的接口地址
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
      if (type == 'gamer') this['gamer'] = this.form['gamer'] = "";
      if (type == 'player_avatar') this['player_avatar'] = this.form['player_avatar'] = "";
      if (type == 'player_screen_name') this['player_screen_name'] = this.form['player_screen_name'] = "";
      if (type == 'gold_coin_rewards') this['gold_coin_rewards'] = this.form['gold_coin_rewards'] = "";
      if (type == 'game_time') this['game_time'] = this.form['game_time'] = "";
      if (type == 'number_of_steps_used') this['number_of_steps_used'] = this.form['number_of_steps_used'] = "";
      if (type == 'total_game_score') this['total_game_score'] = this.form['total_game_score'] = "";
      if (type == 'clearance_status') this['clearance_status'] = this.form['clearance_status'] = "";
    },
    submit_() {
      if (this['level_name'] !== null) this.form['level_name'] = this['level_name']
      if (this['gamer'] !== null) this.form['gamer'] = this['gamer']
      if (this['player_avatar'] !== null) this.form['player_avatar'] = this['player_avatar']
      if (this['player_screen_name'] !== null) this.form['player_screen_name'] = this['player_screen_name']
      if (this['gold_coin_rewards'] !== null) this.form['gold_coin_rewards'] = this['gold_coin_rewards']
      if (this['game_time'] !== null) this.form['game_time'] = this['game_time']
      if (this['number_of_steps_used'] !== null) this.form['number_of_steps_used'] = this['number_of_steps_used']
      if (this['total_game_score'] !== null) this.form['total_game_score'] = this['total_game_score']
      if (this['clearance_status'] !== null) this.form['clearance_status'] = this['clearance_status']
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
            '/game_record/upload',
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
          uploadFilePathApi('/game_record/upload',filePath,undefined,undefined,
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
    async get_list_user_gamer() {
      var json = await this.$get("~/api/user/get_list?user_group=游戏玩家");
      if(json.result && json.result.list){
        json.result.list.map((o) => this.list_user_gamer.push({value:o.user_id,text:o.nickname + '-' + o.username}));
      }
      else if(json.error){
        console.error(json.error);
      }
    },
        /**
     * 获取游戏玩家用户组
     */
    async get_group_user_gamer() {
      this.form["gamer"] = this.user.user_id;
      var json = await this.$get("~/api/user_group/get_obj?name=游戏玩家");
      if(json.result && json.result.obj){
        this.group_user_gamer = json.result.obj;
        this.get_user_session_gamer(this.form['gamer'])
      }
      else if(json.error){
        console.error(json.error);
      }
    },
    get_user_session_gamer(id){
      var _this = this;
      var user_id = {"user_id":_this.user.user_id}
      var url = "~/api/"+_this.group_user_gamer.source_table+"/get_obj"
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
          _this.form["gamer"] = _this.user.user_id
          _this.disabledObj['gamer' + '_isDisabled'] = true
          for (var i=0;i<arr.length;i++){
            if (arr[i]!=='examine_state' && arr[i]!=='examine_reply') {
              for (var j = 0; j < arrForm.length; j++) {
                if (arr[i] === arrForm[j]) {
                  if (arr[i] !== "gamer") {
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
     * 获取通关状态列表
     */
    async get_list_clearance_status() {
          ['成功','失败'].map((o) => this.list_clearance_status.push({value:o,text:o}));
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
        bl = this.$check_action('/game_record/table', 'add');
        console.log(bl ? '你有表格添加权限视作有添加权限' : '你没有表格添加权限');
      }
      if (!bl) {
        bl = this.$check_action('/game_record/table', 'set');
        console.log(bl ? '你有表格添加权限视作有修改权限' : '你没有表格修改权限');
      }
      if (!bl) {
        bl = this.$check_action('/game_record/view', 'add');
        console.log(bl ? '你有视图添加权限视作有添加权限' : '你没有视图添加权限');
      }
      if (!bl) {
        bl = this.$check_action('/game_record/view', 'set');
        console.log(bl ? '你有视图修改权限视作有修改权限' : '你没有视图修改权限');
      }
      if (!bl) {
        bl = this.$check_action('/game_record/view', 'get');
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
            this.get_list_user_gamer();
          this.get_group_user_gamer();
                                      this.get_list_clearance_status();
      },
};
</script>

<style lang="scss" scoped>
  @import 'styles/pagesC/index.scss';
</style>