<template>
  <view class="page_diy_edit page_game_record_edit" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <!-- 返回箭头 -->
    <view class="back-arrow" @click="goBack"></view>
    <tn-nav-bar>游戏记录</tn-nav-bar>
    <view class="page-custom " id="page_diy_edit">
      <view class="custom-from">
          <view v-if="$check_field('set', 'level_name') || $check_field('add', 'level_name') ||  $check_field('get', 'level_name')" class="custom-from-item">
          <view class="label">
            <text>
              关卡名称
            </text>
          </view>
              <!-- 文本 -->
                  <view class="content">
            <input type="text" id="form_level_name" v-model="form['level_name']" placeholder="请输入关卡名称" v-if="(form['level_name'] && $check_field('set','level_name')) || (!form['level_name'] && $check_field('add','level_name'))" :disabled="disabledObj['level_name_isDisabled']" />
            <text v-else-if="$check_field('get','level_name')">{{ form['level_name'] }}</text>
          </view>
                    </view>
            <view v-if="$check_field('set', 'gamer') || $check_field('add', 'gamer') ||  $check_field('get', 'gamer')" class="custom-from-item">
          <view class="label">
            <text>游戏玩家</text>
          </view>
          <view class="content">
            <uni-data-select
                    id="form_gamer" v-model="form['gamer']"
                    :localdata="list_user_gamer"
                    :clear="!disabledObj['gamer_isDisabled']"
                    :disabled="disabledObj['gamer_isDisabled']"
                    v-if="(form['gamer'] && $check_field('set', 'gamer')) || (!form['gamer'] && $check_field('add', 'gamer'))" style="width: 100%;">
            </uni-data-select>
            <text v-else-if="$check_field('get', 'gamer')">
              {{form['gamer']}}
            </text>
          </view>
        </view>
            <view v-if="$check_field('set', 'player_avatar') || $check_field('add', 'player_avatar') ||  $check_field('get', 'player_avatar')" class="custom-from-item">
          <view class="label">
            <text>
              玩家头像
            </text>
          </view>
              <!-- 图片 -->
          <!-- 修改权限 -->
          <view class="content" v-if="form['player_avatar'] && $check_field('set', 'player_avatar')">
            <image v-if="disabledObj['player_avatar_isDisabled']" :src="$fullImgUrl(form['player_avatar'])" />
            <image v-if="!disabledObj['player_avatar_isDisabled']" :src="$fullImgUrl(form['player_avatar'])" @click="change_img('player_avatar')" />
          </view>
          <!-- 添加权限 -->
          <view class="content" v-else-if="!form['player_avatar'] && $check_field('add', 'player_avatar')">
            <view v-if="disabledObj['player_avatar_isDisabled']" class="btn_add_img">
              <text>+</text>
            </view>
            <view v-if="!disabledObj['player_avatar_isDisabled']" class="btn_add_img diy_text_row" @click="change_img('player_avatar')">
              <text>+</text>
            </view>
          </view>
          <!-- 查询权限 -->
          <view class="content" v-else-if="$check_field('get', 'player_avatar')">
            <image :src="$fullImgUrl(form['player_avatar'])" />
          </view>
          <uni-icons style="display: none" class="forward" type="forward" id="form_img_player_avatar"></uni-icons>
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
            <view v-if="$check_field('set', 'gold_coin_rewards') || $check_field('add', 'gold_coin_rewards') ||  $check_field('get', 'gold_coin_rewards')" class="custom-from-item">
          <view class="label">
            <text>
              金币奖励
            </text>
          </view>
              <!-- 文本 -->
                  <view class="content">
            <input type="text" id="form_gold_coin_rewards" v-model="form['gold_coin_rewards']" placeholder="请输入金币奖励" v-if="(form['gold_coin_rewards'] && $check_field('set','gold_coin_rewards')) || (!form['gold_coin_rewards'] && $check_field('add','gold_coin_rewards'))" :disabled="disabledObj['gold_coin_rewards_isDisabled']" />
            <text v-else-if="$check_field('get','gold_coin_rewards')">{{ form['gold_coin_rewards'] }}</text>
          </view>
                    </view>
            <view v-if="$check_field('set', 'game_time') || $check_field('add', 'game_time') ||  $check_field('get', 'game_time')" class="custom-from-item">
          <view class="label">
            <text>
              游戏时间
            </text>
          </view>
              <!-- 文本 -->
                  <view class="content">
            <input type="text" id="form_game_time" v-model="form['game_time']" placeholder="请输入游戏时间" v-if="(form['game_time'] && $check_field('set','game_time')) || (!form['game_time'] && $check_field('add','game_time'))" :disabled="disabledObj['game_time_isDisabled']" />
            <text v-else-if="$check_field('get','game_time')">{{ form['game_time'] }}</text>
          </view>
                    </view>
            <view v-if="$check_field('set', 'number_of_steps_used') || $check_field('add', 'number_of_steps_used') ||  $check_field('get', 'number_of_steps_used')" class="custom-from-item">
          <view class="label">
            <text>
              使用步数
            </text>
          </view>
              <!-- 文本 -->
                  <view class="content">
            <input type="text" id="form_number_of_steps_used" v-model="form['number_of_steps_used']" placeholder="请输入使用步数" v-if="(form['number_of_steps_used'] && $check_field('set','number_of_steps_used')) || (!form['number_of_steps_used'] && $check_field('add','number_of_steps_used'))" :disabled="disabledObj['number_of_steps_used_isDisabled']" />
            <text v-else-if="$check_field('get','number_of_steps_used')">{{ form['number_of_steps_used'] }}</text>
          </view>
                    </view>
            <view v-if="$check_field('set', 'total_game_score') || $check_field('add', 'total_game_score') ||  $check_field('get', 'total_game_score')" class="custom-from-item">
          <view class="label">
            <text>
              游戏总分
            </text>
          </view>
              <!-- 文本 -->
                  <view class="content">
            <input type="text" id="form_total_game_score" v-model="form['total_game_score']" placeholder="请输入游戏总分" v-if="(form['total_game_score'] && $check_field('set','total_game_score')) || (!form['total_game_score'] && $check_field('add','total_game_score'))" :disabled="disabledObj['total_game_score_isDisabled']" />
            <text v-else-if="$check_field('get','total_game_score')">{{ form['total_game_score'] }}</text>
          </view>
                    </view>
            <view v-if="$check_field('set', 'clearance_status') || $check_field('add', 'clearance_status') ||  $check_field('get', 'clearance_status')" class="custom-from-item">
          <view class="label">
            <text>
              通关状态
            </text>
          </view>
              <!-- 选项 -->
          <view class="content">
            <uni-data-select
                    id="form_clearance_status"
                    v-model="form['clearance_status']"
                    :localdata="list_clearance_status"
                    v-if="(form['clearance_status'] && $check_field('set','clearance_status')) || (!form['clearance_status'] && $check_field('add','clearance_status'))"
				    style="width: 100%;"
            ></uni-data-select>
            <text v-else-if="$check_field('get','clearance_status')">{{ form['clearance_status'] }}</text>
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
      url_get_obj: '~/api/game_record/get_obj?',
      url_add: '~/api/game_record/add?',
      url_set: '~/api/game_record/set?',
	  today: new Date().toISOString().split('T')[0],
      // 查询条件
      query: {
        game_record_id: 0,
          level_name: "",
            gamer: 0,
            player_avatar: "",
            player_screen_name: "",
            gold_coin_rewards: 0,
            game_time: 0,
            number_of_steps_used: 0,
            total_game_score: 0,
            clearance_status: "",
        },
      obj: {
        game_record_id: 0,
          level_name:  '', // 关卡名称
            gamer: 0, // 游戏玩家
            player_avatar:  '', // 玩家头像
            player_screen_name:  '', // 玩家网名
            gold_coin_rewards:  0 , // 金币奖励
            game_time:  0 , // 游戏时间
            number_of_steps_used:  0 , // 使用步数
            total_game_score:  0 , // 游戏总分
            clearance_status:  '', // 通关状态
        },

      // 表单字段
      form: {
        game_record_id: 0,
          level_name:  '', // 关卡名称
            gamer: 0, // 游戏玩家
            player_avatar:  '', // 玩家头像
            player_screen_name:  '', // 玩家网名
            gold_coin_rewards:  0 , // 金币奖励
            game_time:  0 , // 游戏时间
            number_of_steps_used:  0 , // 使用步数
            total_game_score:  0 , // 游戏总分
            clearance_status:  '', // 通关状态
        },
      disabledObj: {
          level_name_isDisabled: false,
            gamer_isDisabled: false,
            player_avatar_isDisabled: false,
            player_screen_name_isDisabled: false,
                            clearance_status_isDisabled: false,
        },
                      // 用户列表
      list_user_gamer: [],
                                                                      list_clearance_status: [],
              field: 'game_record_id',
      table_key: 'game_record',

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
    async get_list_user_gamer() {
        var json = await this.$get("~/api/user/get_list?user_group=游戏玩家");
        if(json.result && json.result.list){
          json.result.list.map((o) => this.list_user_gamer.push({value:o.user_id,text:o.nickname + '-' + o.username}));
        }
        else if(json.error){
          console.error(json.error);
        }
    },
            async get_user_session_gamer(){
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
                _this.form["gamer"] = user_id
                _this.disabledObj['gamer' + '_isDisabled'] = true
                for (var i=0;i<arr.length;i++){
                  if (arr[i]!=='examine_state' && arr[i]!=='examine_reply') {
                    for (var j = 0; j < arrForm.length; j++) {
                      if (arr[i] === arrForm[j]) {
                        if (arr[i] !== "gamer") {
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
    * 获取通关状态列表
    */
    async get_list_clearance_status() {
              ['成功','失败'].map((o) => this.list_clearance_status.push({value:o,text:o}));
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
            url: _self.$fullUrl('~/api/game_record/upload?'),
            filePath: tempFilePaths[0],
            name: 'file',
            formData: {
              game_record: 'test',
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
              this.get_user_session_gamer();
        this.get_list_user_gamer();
                                            this.get_list_clearance_status();
        },
};
</script>

