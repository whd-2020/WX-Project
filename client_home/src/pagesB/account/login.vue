<template>
  <view id="account_login" class="user_account account_login">
    <!-- 登录弹窗 -->
    <view class="login-modal">
      <view class="modal-content" @click.stop>
        <!-- 关闭按钮 -->
        <view class="close-btn" @click="closeModal">
          <text class="close-icon">×</text>
        </view>

        <view class="modal-body">
          <!-- 微信图标 -->
          <view class="wechat-icon-wrapper">
            <view class="wechat-icon">
              <text class="icon-text">微信</text>
            </view>
          </view>

          <text class="modal-title">欢迎使用</text>
          <text class="modal-subtitle">请使用微信授权登录</text>

          <!-- 微信登录按钮 -->
          <button class="wechat-login-btn" @click="handleWechatLogin" :disabled="logining">
            <text v-if="!logining">微信一键登录</text>
            <text v-else>登录中...</text>
          </button>

          <!-- 隐私协议勾选框 -->
          <view class="privacy-check" :class="{ error: showPrivacyError }" @click="toggleAgree">
            <view class="checkbox" :class="{ checked: agreePrivacy, error: showPrivacyError }">
              <text v-if="agreePrivacy" class="check-icon">✓</text>
            </view>
            <view class="privacy-content">
              <text class="privacy-tip" :class="{ error: showPrivacyError }">登录即表示同意用户协议和隐私政策</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
  import mixin from '@/libs/mixins/page.js';
  import {wechatLoginApi} from '@/api/login.js';
  import store from '@/store';

  export default {
    mixins: [mixin],
    data() {
      return {
        logining: false,
        adminLogining: false,
        agreePrivacy: false,
        showPrivacyError: false,
        showAdminLogin: false,
        adminForm: {
          username: '',
          password: ''
        },
		allow_user: [
			'管理员',
			'游戏玩家'
		],
      };
    },
    onLoad() {
      console.log(this.$u.route);
    },
    methods: {
      /**
       * 关闭弹窗
       */
      closeModal() {
        this.playClickSound();
        uni.switchTab({
          url: '/pages/index/index'
        });
      },

      /**
       * 切换隐私协议同意状态
       */
      toggleAgree() {
        this.playClickSound();
        this.agreePrivacy = !this.agreePrivacy;
        this.showPrivacyError = false;
      },

      /**
       * 处理微信登录点击
       */
      handleWechatLogin() {
        this.playClickSound();
        if (!this.agreePrivacy) {
          this.showPrivacyError = true;
          return;
        }
        this.wechatLogin();
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
            
            // 获取用户信息
            let nickName = userRes.userInfo.nickName || '';
            let avatarUrl = userRes.userInfo.avatarUrl || '';

            // 验证昵称和头像，排除默认值"微信用户"
            if (!nickName || nickName.trim() === '' || nickName === '微信用户') {
              nickName = '';  // 使用空字符串，后端会保持原有昵称
            }
            if (!avatarUrl || avatarUrl.trim() === '') {
              avatarUrl = '';  // 使用空字符串，后端会保持原有头像
            }
            console.log('处理后的用户信息 - nickName:', nickName, ', avatarUrl:', avatarUrl);
            
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
                        console.log('登录用户数据:', user);
                        console.log('用户组:', user.user_group);
                        console.log('允许的用户组:', this.allow_user);
                        console.log('是否包含:', this.allow_user.includes(user.user_group));
                        if (this.allow_user.includes(user.user_group)) {
                          // 缓存token
                          console.log('准备设置token:', user.token);
                          this.$u.vuex('token', user.token);
                          // 存储用户信息
                          console.log('准备设置userInfo:', user);
                          this.$u.vuex('userInfo', user);
                          // 设置权限集
                          console.log('准备设置userGroup:', user.user_group);
                          this.$u.vuex('userGroup', user.user_group);

                          console.log('---微信登录成功---, 最终 userInfo:', user);
                          console.log('当前store状态:', JSON.stringify(store.state.app, null, 2));
                          this.$toast('登录成功', 'success');

                          // 使用nextTick确保状态完全更新后再跳转
                          setTimeout(() => {
                            console.log('跳转前store状态:', JSON.stringify(store.state.app, null, 2));
                            if (user.isNewUser) {
                              // 新用户跳转到设置页面完善信息
                              console.log('新用户，跳转到设置页面');
                              uni.navigateTo({
                                url: '/pagesB/account/setup_profile'
                              });
                            } else {
                              // 老用户直接进入首页
                              console.log('老用户，跳转到首页');
                              uni.switchTab({
                                url: '/pages/index/index'
                              });
                            }
                          }, 1000);
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

      /**
       * 管理员账号密码登录
       */
      adminLogin() {
        const { username, password } = this.adminForm;
        if (!username || !password) {
          this.$toast('请输入账号和密码', 'error');
          return;
        }

        this.adminLogining = true;
        
        // 模拟管理员登录
        if (username === 'admin' && password === '123456') {
          // 模拟成功登录
          setTimeout(() => {
            this.adminLogining = false;
            const user = {
              token: 'admin_token',
              username: '管理员',
              nickName: '管理员',
              user_group: '管理员',
              isNewUser: false
            };
            this.$u.vuex('token', user.token);
            this.$u.vuex('userInfo', user);
            this.$u.vuex('userGroup', user.user_group);
            this.$toast('登录成功', 'success');
            setTimeout(() => {
              uni.switchTab({
                url: '/pages/index/index'
              });
            }, 800);
          }, 1000);
        } else {
          setTimeout(() => {
            this.adminLogining = false;
            this.$toast('账号或密码错误', 'error');
          }, 1000);
        }
      },
    },

  };
</script>

<style lang="scss" scoped>
.user_account {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
}

.login-modal {
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px;
  box-sizing: border-box;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  position: relative;
  width: 100%;
  max-width: 340px;
  background: white;
  border-radius: 20px;
  overflow: visible;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
  animation: slideUp 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes slideUp {
  from {
    transform: translateY(80px) scale(0.9);
    opacity: 0;
  }
  to {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
}

.close-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.04);
  z-index: 10;
  transition: all 0.2s;

  &:active {
    background: rgba(0, 0, 0, 0.1);
    transform: scale(0.9);
  }

  .close-icon {
    font-size: 32px;
    color: #999;
    line-height: 1;
    font-weight: 200;
  }
}

.modal-body {
  padding: 60px 30px 35px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;

  .wechat-icon-wrapper {
    width: 80px;
    height: 80px;
    margin-bottom: 28px;

    .wechat-icon {
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, #09bb07 0%, #07c160 100%);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 8px 24px rgba(7, 193, 96, 0.3);
      position: relative;

      .icon-text {
        font-size: 24px;
        font-weight: 600;
        color: white;
        letter-spacing: 2px;
      }

      &::before {
        content: '';
        position: absolute;
        width: 100%;
        height: 100%;
        border-radius: 50%;
        background: linear-gradient(135deg, rgba(255,255,255,0.3) 0%, rgba(255,255,255,0) 100%);
        top: 0;
        left: 0;
      }
    }
  }

  .modal-title {
    font-size: 24px;
    font-weight: 700;
    color: #333;
    margin-bottom: 10px;
    letter-spacing: 1px;
  }

  .modal-subtitle {
    font-size: 14px;
    color: #999;
    margin-bottom: 36px;
  }

  .wechat-login-btn {
    width: 100%;
    padding: 16px 24px;
    background: linear-gradient(135deg, #09bb07 0%, #07c160 100%);
    color: white;
    border: none;
    border-radius: 50px;
    font-size: 17px;
    font-weight: 600;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 6px 20px rgba(7, 193, 96, 0.35);
    transition: all 0.3s;
    letter-spacing: 1px;

    &:active {
      transform: translateY(2px);
      box-shadow: 0 4px 12px rgba(7, 193, 96, 0.35);
    }

    &:disabled {
      opacity: 0.7;
      transform: none;
    }
  }

  .privacy-check {
    display: flex;
    flex-direction: row;
    align-items: flex-start;
    margin-top: 24px;
    padding: 16rpx;
    width: 100%;
    box-sizing: border-box;
    border-radius: 12rpx;
    background: #f8f8f8;
    transition: all 0.3s;

    &.error {
      background: #fff5f5;
      border: 2rpx solid #ffccc7;
    }

    .checkbox {
      width: 28rpx;
      height: 28rpx;
      border: 2rpx solid #ddd;
      border-radius: 6rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 16rpx;
      margin-top: 4rpx;
      flex-shrink: 0;
      transition: all 0.2s;

      &.checked {
        background: linear-gradient(135deg, #09bb07 0%, #07c160 100%);
        border-color: #07c160;
      }

      &.error {
        border-color: #ff4d4f;
      }

      .check-icon {
        font-size: 20rpx;
        color: white;
        font-weight: bold;
      }
    }

    .privacy-content {
      flex: 1;
      display: flex;
      flex-direction: column;

      .privacy-tip {
        font-size: 24rpx;
        color: #999;
        line-height: 1.5;
        text-align: left;
        transition: color 0.3s;

        &.error {
          color: #ff4d4f;
        }
      }

      .error-tip {
        font-size: 22rpx;
        color: #ff4d4f;
        margin-top: 8rpx;
        text-align: left;
        animation: shake 0.3s ease;
      }
    }
  }

  @keyframes shake {
    0%, 100% { transform: translateX(0); }
    25% { transform: translateX(-4rpx); }
    75% { transform: translateX(4rpx); }
  }

  .divider {
    display: flex;
    align-items: center;
    width: 100%;
    margin-top: 30px;
    margin-bottom: 20px;

    .divider-line {
      flex: 1;
      height: 1px;
      background: #e8e8e8;
    }

    .divider-text {
      padding: 0 20rpx;
      font-size: 24rpx;
      color: #999;
    }
  }

  .admin-login-btn {
    width: 100%;
    padding: 14px 24px;
    background: transparent;
    color: #666;
    border: 2rpx solid #d9d9d9;
    border-radius: 50px;
    font-size: 16px;
    font-weight: 500;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s;
    letter-spacing: 1px;

    &:active {
      background: #f5f5f5;
      border-color: #bfbfbf;
    }

    &:disabled {
      opacity: 0.5;
    }
  }

  .admin-login-form {
    width: 100%;
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #f0f0f0;

    .form-item {
      margin-bottom: 20px;

      .form-input {
        width: 100%;
        height: 80rpx;
        padding: 0 24rpx;
        background: #f8f8f8;
        border: 2rpx solid #e8e8e8;
        border-radius: 40rpx;
        font-size: 28rpx;
        box-sizing: border-box;
        transition: all 0.3s;

        &:focus {
          border-color: #07c160;
          background: #fff;
        }
      }

      .placeholder {
        color: #bbb;
      }
    }

    .submit-admin-btn {
      width: 100%;
      padding: 14px 24px;
      background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
      color: white;
      border: none;
      border-radius: 50px;
      font-size: 17px;
      font-weight: 600;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 16px rgba(24, 144, 255, 0.3);
      transition: all 0.3s;
      letter-spacing: 1px;

      &:active {
        transform: translateY(2px);
        box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
      }

      &:disabled {
        opacity: 0.7;
        transform: none;
      }
    }
  }
}
</style>
