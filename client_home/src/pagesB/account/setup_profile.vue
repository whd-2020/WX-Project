<template>
  <view class="setup-profile-container">
    <!-- 返回箭头 -->
    <view class="back-arrow" @click="goBack"></view>
    <view class="header">
      <text class="title">完善个人信息</text>
      <text class="subtitle">请填写以下信息以完成注册</text>
    </view>

    <view class="form-section">
      <!-- 昵称输入 -->
      <view class="form-group">
        <text class="label">昵称</text>
        <input
          v-model="form.nickname"
          type="text"
          placeholder="请输入昵称"
          class="input-field"
          maxlength="20"
        />
      </view>

      <!-- 密码输入 -->
      <view class="form-group">
        <text class="label">设置密码</text>
        <input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          class="input-field"
        />
      </view>

      <!-- 确认密码 -->
      <view class="form-group">
        <text class="label">确认密码</text>
        <input
          v-model="form.confirmPassword"
          type="password"
          placeholder="请再次输入密码"
          class="input-field"
        />
      </view>

      <!-- 头像上传 -->
      <view class="form-group">
        <text class="label">头像</text>
        <view class="avatar-section">
          <image
            v-if="form.avatarUrl"
            :src="form.avatarUrl"
            class="avatar-preview"
          />
          <view v-else class="avatar-placeholder">
            <text>点击上传头像</text>
          </view>
          <button @click="chooseAvatar" class="upload-btn">选择头像</button>
        </view>
      </view>
    </view>

    <view class="button-section">
      <button @click="saveProfile" class="save-btn" :disabled="saving">
        {{ saving ? '保存中...' : '保存并继续' }}
      </button>
    </view>
  </view>
</template>

<script>
import { updateProfileApi } from '@/api/login.js';
import mixin from '@/libs/mixins/page.js';

export default {
  mixins: [mixin],
  data() {
    return {
      form: {
        nickname: '',
        password: '',
        confirmPassword: '',
        avatarUrl: ''
      },
      saving: false
    };
  },
  methods: {
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          this.form.avatarUrl = res.tempFilePaths[0];
        }
      });
    },
    async saveProfile() {
      if (!this.form.nickname.trim()) {
        this.$toast('请输入昵称', 'error');
        return;
      }
      if (!this.form.password) {
        this.$toast('请输入密码', 'error');
        return;
      }
      if (this.form.password !== this.form.confirmPassword) {
        this.$toast('两次输入的密码不一致', 'error');
        return;
      }
      if (this.form.password.length < 6) {
        this.$toast('密码长度至少6位', 'error');
        return;
      }

      this.saving = true;
      try {
        const data = {
          nickname: this.form.nickname,
          password: this.form.password,
          avatar: this.form.avatarUrl || ''
        };

        const res = await updateProfileApi(data);
        if (res.result && res.result.obj) {
          this.$toast('保存成功', 'success');
          // 更新本地用户信息
          this.$u.vuex('userInfo', res.result.obj);
          setTimeout(() => {
            uni.switchTab({
              url: '/pages/index/index'
            });
          }, 500);
        } else {
          this.$toast(res.error?.message || '保存失败', 'error');
        }
      } catch (err) {
        console.error('保存失败:', err);
        this.$toast('保存失败，请重试', 'error');
      } finally {
        this.saving = false;
      }
    }
  }
};
</script>

<style scoped lang="scss">
.setup-profile-container {
  padding: 0;
  background: #f5f5f5;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  text-align: center;
  padding: 30px 20px 20px;
  background: white;
  border-bottom: 1px solid #f0f0f0;

  .title {
    display: block;
    font-size: 24px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
  }

  .subtitle {
    display: block;
    font-size: 13px;
    color: #999;
    font-weight: 400;
  }
}

.form-section {
  background: white;
  padding: 20px 15px;
  margin: 10px 0;
  border-radius: 8px;
  flex: 1;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 18px;

  &:last-child {
    margin-bottom: 0;
  }

  .label {
    display: block;
    font-size: 14px;
    font-weight: 500;
    color: #333;
    margin-bottom: 8px;
  }

  .input-field {
    width: 100%;
    padding: 12px 14px;
    border: 1px solid #ddd;
    border-radius: 6px;
    font-size: 15px;
    box-sizing: border-box;
    background: #fafafa;
    color: #333;
    -webkit-appearance: none;
    appearance: none;
    min-height: 44px;

    &:focus {
      border-color: #007aff;
      background: white;
      outline: none;
    }

    &::placeholder {
      color: #ccc;
    }
  }
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #fafafa;
  border-radius: 6px;
  border: 1px solid #eee;

  .avatar-preview {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #ddd;
    flex-shrink: 0;
  }

  .avatar-placeholder {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    background: #e8e8e8;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    font-size: 12px;
    color: #999;
    flex-shrink: 0;
    border: 2px solid #ddd;
  }

  .upload-btn {
    padding: 10px 16px;
    background: #007aff;
    color: white;
    border: none;
    border-radius: 6px;
    font-size: 14px;
    font-weight: 500;
    -webkit-appearance: none;
    appearance: none;
    cursor: pointer;
    transition: background 0.2s;

    &:active {
      background: #0051d5;
    }
  }
}

.button-section {
  padding: 15px;
  background: white;
  border-top: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.save-btn {
  width: 100%;
  padding: 14px;
  background: #007aff;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  -webkit-appearance: none;
  appearance: none;
  cursor: pointer;
  transition: background 0.2s;

  &:active {
    background: #0051d5;
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}
</style>
