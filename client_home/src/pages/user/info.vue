<template>
  <view class="page_user_info" id="page_user_info" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <!-- 返回箭头 -->
    <view class="back-arrow" @click="goBack"></view>
    <!-- 导航栏 -->
    <view class="page-content">
      <view class="user-info-container">
        <!-- 头像区域 -->
        <view class="avatar-section" @click="changeAvatar">
          <view class="avatar-wrapper">
            <!-- 圆形头像容器 -->
            <view class="avatar-container">
              <!-- 文字头像或图片头像 -->
              <view v-if="!avatarUrl" class="text-avatar">
                {{ getInitials(nickname) }}
              </view>
              <image v-else class="image-avatar" :src="avatarUrl" mode="aspectFill"></image>
              <!-- 相机图标覆盖 -->
              <view class="avatar-overlay">
                <text class="camera-icon">📷</text>
              </view>
            </view>
          </view>
          <view class="avatar-tip">点击更换头像</view>
        </view>

        <!-- 信息列表 -->
        <view class="info-list">
          <!-- 昵称 -->
          <view class="info-item">
            <view class="info-label">
              <text class="user-icon">👤</text>
              <text class="label-text">昵称</text>
            </view>
            <input
              type="text"
              v-model="nickname"
              placeholder="请输入昵称"
              class="info-input"
              @input="onNicknameInput"
            />
          </view>
        </view>

        <!-- 保存按钮 -->
        <view class="save-section">
          <view class="save-btn" :class="{ disabled: !hasChanges }" @click="saveUserInfo">
            <text>保存修改</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import mixin from '@/libs/mixins/page.js';
import { mapState } from 'vuex';

export default {
  mixins: [mixin],
  data() {
    return {
      oauth: {
        signIn: true,
        user_group: '',
      },
      nickname: '',
      avatarUrl: '',
      uploadedAvatar: '', // 上传后的头像路径
      originalNickname: '',
      originalAvatar: '',
    };
  },
  computed: {
    ...mapState({
      userInfo: state => state.app.userInfo
    }),
    hasChanges() {
      return this.nickname !== this.originalNickname || this.uploadedAvatar !== '';
    }
  },
  onShow() {
    this.loadUserInfo();
  },
  methods: {
    goBack() {
      const pages = getCurrentPages();
      if (pages.length > 1) {
        uni.navigateBack();
      } else {
        // 如果是第一个页面，跳转到首页
        uni.reLaunch({
          url: '/pages/index/index'
        });
      }
    },
    loadUserInfo() {
      console.log('加载用户信息:', this.userInfo);
      if (this.userInfo) {
        this.nickname = this.userInfo.nickname || '';
        this.avatarUrl = this.$fullImgUrl(this.userInfo.avatar);
        this.originalNickname = this.nickname;
        this.originalAvatar = this.avatarUrl;
        this.uploadedAvatar = '';
      }
    },
    onNicknameInput(e) {
      this.nickname = e.detail.value;
    },
    changeAvatar() {
      const _self = this;
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: function (res) {
          const tempFilePaths = res.tempFilePaths;
          uni.showLoading({ title: '上传中...' });
          uni.uploadFile({
            url: _self.$fullUrl('~/api/user/upload?'),
            filePath: tempFilePaths[0],
            name: 'file',
            header: {
              'x-auth-token': _self.userInfo.token,
            },
            formData: {},
            success: function (uploadFileRes) {
              uni.hideLoading();
              const result = JSON.parse(uploadFileRes.data);
              console.log('上传结果:', result);

              if (result.result && result.result.url) {
                _self.uploadedAvatar = result.result.url;
                _self.avatarUrl = _self.$fullImgUrl(result.result.url);
                _self.$toast('头像已选择，请点击保存');
              } else {
                _self.$toast('上传失败');
              }
            },
            fail: function(err) {
              uni.hideLoading();
              console.error('上传失败:', err);
              _self.$toast('上传失败');
            }
          });
        },
        fail: function (e) {
          console.log('选择图片失败:', e);
        },
      });
    },
    saveUserInfo() {
      if (!this.hasChanges) {
        return;
      }

      if (!this.nickname || !this.nickname.trim()) {
        this.$toast('请输入昵称');
        return;
      }

      uni.showLoading({ title: '保存中...' });

      const updateData = {
        nickname: this.nickname.trim(),
      };

      if (this.uploadedAvatar) {
        updateData.avatar = this.uploadedAvatar;
      }

      this.$post(
        '~/api/user/set?user_id=' + this.userInfo.user_id,
        updateData,
        (res) => {
          uni.hideLoading();
          console.log('保存成功:', res);

          // 更新 store
          this.$store.commit('app/setUserInfo', {
            ...this.userInfo,
            nickname: this.nickname.trim(),
            avatar: this.uploadedAvatar || this.userInfo.avatar
          });

          // 重置状态
          this.originalNickname = this.nickname;
          this.uploadedAvatar = '';

          this.$toast('保存成功');
        },
        (err) => {
          uni.hideLoading();
          console.error('保存失败:', err);
          this.$toast('保存失败');
        }
      );
    },
    // 获取昵称首字母作为文字头像
    getInitials(name) {
      if (!name || !name.trim()) {
        return '用';
      }
      // 取昵称的第一个字符
      return name.trim().charAt(0);
    },
  },
};
</script>
<style lang="scss" scoped>
.page_user_info {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8f9fe 0%, #ffffff 100%);
}



.page-content {
  padding: 30rpx;
}

.user-info-container {
  background-color: #fff;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
  min-height: 600rpx;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0 50rpx;
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  position: relative;
}

.avatar-wrapper {
  position: relative;
  margin-bottom: 20rpx;
}

.avatar-container {
  position: relative;
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  overflow: hidden;
  background-color: #f0f0f0;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.text-avatar {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 64rpx;
  font-weight: 600;
  color: #667eea;
  background-color: #f8f9fe;
}

.image-avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.avatar-overlay {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 60rpx;
  height: 60rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4rpx solid #fff;
  box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.4);
  z-index: 10;
}

.camera-icon {
  font-size: 32rpx;
}

.avatar-tip {
  font-size: 26rpx;
  color: #999;
}

.info-list {
  padding: 0 40rpx;
}

.info-item {
  padding: 40rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.info-label {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.user-icon {
  font-size: 32rpx;
  margin-right: 12rpx;
}

.label-text {
  font-size: 28rpx;
  color: #666;
  margin-left: 12rpx;
  font-weight: 500;
}

.info-input {
  width: 100%;
  font-size: 32rpx;
  padding: 24rpx;
  background-color: #f8f9fe;
  border-radius: 12rpx;
  color: #333;
  border: 2rpx solid transparent;
  transition: all 0.3s;
  min-height: 88rpx;
  box-sizing: border-box;
  word-break: break-word;
}

.info-input:focus {
  background-color: #fff;
  border-color: #667eea;
}

.save-section {
  padding: 50rpx 40rpx 60rpx;
}

.save-btn {
  width: 100%;
  height: 96rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 34rpx;
  font-weight: 600;
  box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.4);
  transition: all 0.3s;
}

.save-btn:active {
  transform: scale(0.98);
  box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
}

.save-btn.disabled {
  opacity: 0.5;
  background: #e0e0e0;
  box-shadow: none;
}
// 返回箭头样式
.back-arrow {
  position: fixed;
  top: 20rpx;
  left: 20rpx;
  width: 60rpx;
  height: 60rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
  z-index: 1000;
  cursor: pointer;
  transition: all 0.3s ease;

  &:active {
    transform: scale(0.9);
    background: rgba(255, 255, 255, 1);
  }

  &::before {
    content: '';
    width: 20rpx;
    height: 20rpx;
    border-left: 3rpx solid #333;
    border-bottom: 3rpx solid #333;
    transform: rotate(45deg);
    margin-left: 6rpx;
  }
}
</style>
