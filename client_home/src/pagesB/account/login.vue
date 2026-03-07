<template>
  <view id="account_login" class="user_account account_login">
    <!-- 返回按钮 -->
    <view class="back-button" @click="goBack">
      <view class="tn-icon-left-arrow back-icon"></view>
    </view>
    
    <view class="container">
      <view class="custom-1"></view>
      <view class="custom-2"></view>
      <view class="custom-3"></view>
      <view class="custom-4"><text>开心消消乐游戏</text></view>
      <view class="custom-5"></view>

      <view class="login-wrapper">
        <view class="input-content">
          <view class="input-item">
            <view class="input-item-icon">
              <view class="tn-icon-my-lack"></view>
            </view>
            <input
                type="text"
                v-model="form.username"
                placeholder="请输入用户名"
                maxlength="16"
                data-key="username"
            />
          </view>
          <view class="input-item">
            <view class="input-item-icon">
              <view class="tn-icon-lock"></view>
            </view>
            <input
                v-model="form.password"
                placeholder="请输入密码"
                placeholder-class="input-empty"
                maxlength="20"
                :password="!showPassword"
                data-key="password"
                @confirm="login"
            />
            <view class="input-item-icon">
              <view
                  @click="showPassword = !showPassword"
                  :class="[showPassword ? 'tn-icon-eye' : 'tn-icon-eye-hide']"
              >
              </view>
            </view>
          </view>
        </view>
        <!-- 原来账号登录的按钮，注销掉，采用微信一键登录 -->
        <button class="confirm-btn" @click="login" :disabled="logining">账号登录</button>
        
        <!-- 微信登录按钮 -->
        <button class="wechat-login-btn" @click="wechatLogin" :disabled="logining">
          <text>微信一键登录</text>
        </button>
        
        <view class="forget-section">
          <view @click="$navTo('/pagesB/account/forgot')">忘记密码?</view>
        </view>
                		<view class="register-section">
			还没有账号?
			<view @click="$navTo('/pagesB/account/register')" class="text">马上注册</view>
		</view>
      </view>
    </view>

  </view>
</template>

<script>
  import mixin from '@/libs/mixins/page.js';
  // 新增wechatLoginApi,api接口
  import {loginApi, wechatLoginApi} from '@/api/login.js';
  import jsencrypt from '@/components/jsencrypt/jsencrypt.vue';

  export default {
    mixins: [mixin],
    data() {
      return {
        logining: false,
		allow_user: [
			'管理员'
				,"游戏玩家"
									],
        form: {
          username: '',
          password: '',
        },
        rules: {
          username: {
            rules: [
              {
                required: true,
                errorMessage: '请输入用户名',
              },
              {
                minLength: 5,
                maxLength: 16,
                errorMessage: '用户名长度在 {minLength} 到 {maxLength} 个字符',
              },
            ],
          },
          password: {
            rules: [
              {
                required: true,
                errorMessage: '请输入密码',
              },
              {
                minLength: 5,
                maxLength: 16,
                errorMessage: '密码长度在 {minLength} 到 {maxLength} 个字符',
              },
            ],
          },
        },
        showPassword: false,
      };
    },
    onLoad() {
      console.log(this.$u.route);
    },
    methods: {
      goBack() {
        // 返回到上一页
        uni.navigateBack({
          delta: 1,
          fail: () => {
            // 如果没有上一页，则跳转到首页
            uni.switchTab({
              url: '/pages/index/index'
            });
          }
        });
      },
      closePopup() {
        this.logining = false;
      },
      login() {
        this.logining = true;
        let form = Object.assign({}, this.form);
          const publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+mEDzYLbKNB9rbOuvGgwdBUpPaHryRGarxBQppkOzlj+ouep8MMq1Xg7NBkjLOV2vnn4E5AVvX0XVOmBg8W5eNQ1uS1HCG2fie8BpXGgl1pWj/HYIrA2d/U7xxvMO8UMhAGfMdaGrPrGdZTr95pzL/q+VJZOcqSAgux/YEdu11wIDAQAB";
        form.password = jsencrypt.setEncrypt(publicKey,form.password);
          loginApi(form)
          .then((res) => {
            if (res.result && res.result.obj) {
              let user = res.result.obj;
			  if(this.allow_user.includes(user.user_group)){
				  				  // 缓存token
				  this.$u.vuex('token', user.token);
				  // 存储用户信息
				  this.$u.vuex('userInfo', user);
				  // 设置权限集
				  this.$u.vuex('userGroup', user.user_group);
				  // 前往首页
				  uni.switchTab({
				    url: '/pages/index/index',
				  });
				  console.log('---登录成功---');
			  }else{
				  this.$toast("请登录账号", 'error');
			  }
              
            } else if (res.error) {
              this.$toast(res.error.message, 'error');
            }
          })
          .finally(() => {
            this.logining = false;
          });
      },
      /**
       * 手动重置表单
       */
      resetForm() {
        this.$refs.form.resetFields();
      },
      /**
       * 微信登录
       */
      wechatLogin() {
        this.logining = true;
        // 注意：微信要求 getUserProfile 必须直接在用户点击事件中调用
        // 所以先调用 getUserProfile，成功后再去 uni.login 获取 code
        console.log('---点击微信一键登录---');
        uni.getUserProfile({
          desc: '用于完善用户资料',
          success: (userRes) => {
            console.log('getUserProfile 返回 userRes:', userRes);
            
            // 检查是否获取到真实的用户信息
            let nickName = userRes.userInfo.nickName;
            let avatarUrl = userRes.userInfo.avatarUrl;
            
            // 如果获取到的是默认值，提示用户
            if (nickName === '微信用户' || !nickName || nickName.trim() === '') {
              console.warn('获取到的昵称为默认值，可能用户拒绝了授权');
              // 仍然允许登录，但使用默认值，后续用户可以完善信息
              nickName = '微信用户';
            }
            
            // 第二步：获取微信登录 code
            uni.login({
              provider: 'weixin',
              success: (loginRes) => {
                console.log('uni.login 返回 loginRes:', loginRes);
                if (loginRes.code) {
                  // 第三步：将 code 和用户信息发送到后端
                  const loginData = {
                    code: loginRes.code,
                    nickName: nickName,
                    avatarUrl: avatarUrl,
                    gender: userRes.userInfo.gender,
                    country: userRes.userInfo.country,
                    province: userRes.userInfo.province,
                    city: userRes.userInfo.city,
                  };
                  console.log('准备提交到后端的微信登录数据 loginData:', loginData);

                  wechatLoginApi(loginData)
                    .then((res) => {
                      console.log('后端 wechatLoginApi 返回结果:', res);
                      if (res.result && res.result.obj) {
                        let user = res.result.obj;
                        if (this.allow_user.includes(user.user_group)) {
                          // 缓存token
                          this.$u.vuex('token', user.token);
                          // 存储用户信息
                          this.$u.vuex('userInfo', user);
                          // 设置权限集
                          this.$u.vuex('userGroup', user.user_group);
                          
                          // 检查是否需要完善用户信息
                          const needCompleteInfo = user.nickname === '微信用户' || !user.nickname || user.nickname.trim() === '';
                          
                          if (needCompleteInfo) {
                            // 如果需要完善信息，直接跳转到基本信息页面
                            uni.navigateTo({
                              url: '/pages/user/info',
                              success: () => {
                                // 跳转成功后提示
                                setTimeout(() => {
                                  uni.showToast({
                                    title: '请完善您的昵称和头像',
                                    icon: 'none',
                                    duration: 2000
                                  });
                                }, 500);
                              },
                              fail: () => {
                                // 如果跳转失败，则跳转到首页
                                uni.switchTab({
                                  url: '/pages/index/index',
                                });
                                setTimeout(() => {
                                  uni.showModal({
                                    title: '完善信息',
                                    content: '检测到您的昵称为默认值，建议前往个人中心完善昵称和头像',
                                    showCancel: true,
                                    cancelText: '稍后',
                                    confirmText: '去完善',
                                    success: (modalRes) => {
                                      if (modalRes.confirm) {
                                        uni.navigateTo({
                                          url: '/pages/user/info'
                                        });
                                      }
                                    }
                                  });
                                }, 1000);
                              }
                            });
                          } else {
                            // 不需要完善信息，直接前往首页
                            uni.switchTab({
                              url: '/pages/index/index',
                            });
                          }
                          console.log('---微信登录成功---, 最终 userInfo:', user);
                          this.$toast('登录成功', 'success');
                        } else {
                          this.$toast('该账号无权限登录', 'error');
                        }
                      } else if (res.error) {
                        this.$toast(res.error.message || '登录失败，请重试', 'error');
                      }
                    })
                    .catch((err) => {
                      console.error('微信登录失败:', err);
                      this.$toast(err.error?.message || '登录失败，请重试', 'error');
                    })
                    .finally(() => {
                      this.logining = false;
                    });
                } else {
                  this.logining = false;
                  this.$toast('获取微信登录凭证失败', 'error');
                }
              },
              fail: (err) => {
                console.error('微信登录失败:', err);
                this.logining = false;
                this.$toast('微信登录失败，请重试', 'error');
              }
            });
          },
          fail: (err) => {
            console.error('获取用户信息失败:', err);
            this.logining = false;
            if (err.errMsg && err.errMsg.includes('deny')) {
              this.$toast('需要授权才能登录', 'error');
            } else {
              this.$toast('获取用户信息失败', 'error');
            }
          }
        });
      },
    },
    onBackPress() {
      var bl = false;
      var user_id = this.user.user_id;
      if (user_id == null || user_id < 1) {
        // this.$nav('/pages/index/index');
        bl = true;
      }
      return false;
    },
  };
</script>

<style lang="scss" scoped>
  @import 'styles/pagesB/index.scss';
</style>
