<template>
  <view id="account_login" class="user_account account_login">
    <!-- 登录弹窗 -->
    <view class="login-modal">
      <view class="modal-content" @click.stop>
        <!-- 关闭按钮 -->
        <view class="close-btn">
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
          <button class="wechat-login-btn" @click="wechatLogin" :disabled="logining">
            <text v-if="!logining">微信一键登录</text>
            <text v-else>登录中...</text>
          </button>

          <text class="privacy-tip">登录即表示同意用户协议和隐私政策</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
  import mixin from '@/libs/mixins/page.js';
  import {wechatLoginApi} from '@/api/login.js';

  export default {
    mixins: [mixin],
    data() {
      return {
        logining: false,
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
                        if (this.allow_user.includes(user.user_group)) {
                          // 缓存token
                          this.$u.vuex('token', user.token);
                          // 存储用户信息
                          this.$u.vuex('userInfo', user);
                          // 设置权限集
                          this.$u.vuex('userGroup', user.user_group);

                          console.log('---微信登录成功---, 最终 userInfo:', user);
                          this.$toast('登录成功', 'success');

                          // 根据 isNewUser 判断跳转目标
                          setTimeout(() => {
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
                          }, 800);
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

  .privacy-tip {
    font-size: 11px;
    color: #bbb;
    margin-top: 24px;
    line-height: 1.6;
  }
}
</style>
