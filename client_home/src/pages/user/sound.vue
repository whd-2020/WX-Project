<template>
  <view class="page_sound" id="page_sound">
    <view class="sound-wrap">
      <view class="sound-card">
        <view class="sound-head">
          <view class="sound-badge">
            <text class="tn-icon-sound"></text>
          </view>
          <view class="sound-head-text">
            <view class="sound-title">系统音效</view>
            <view class="sound-subtitle">控制提示音与背景音乐开关</view>
          </view>
        </view>

        <view class="sound-list">
          <view class="sound-item">
            <text class="sound-item-text">关闭提示音</text>
            <switch :checked="disableTipSound" color="#2f80ff" @change="onToggleTipSound" />
          </view>
          <view class="sound-item">
            <text class="sound-item-text">关闭背景音乐</text>
            <switch :checked="disableBgm" color="#2f80ff" @change="onToggleBgm" />
          </view>
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
        disableTipSound: false,
        disableBgm: false,
      };
    },
    onShow() {
      this.disableTipSound = !!uni.getStorageSync('disable_tip_sound');
      this.disableBgm = !!uni.getStorageSync('disable_bgm');
    },
    methods: {
      onToggleTipSound(e) {
        this.disableTipSound = !!(e && e.detail && e.detail.value);
        uni.setStorageSync('disable_tip_sound', this.disableTipSound ? 1 : 0);
      },
      onToggleBgm(e) {
        this.disableBgm = !!(e && e.detail && e.detail.value);
        uni.setStorageSync('disable_bgm', this.disableBgm ? 1 : 0);
      },
    },
  };
</script>

<style lang="scss" scoped>
  .page_sound {
    min-height: 100vh;
    box-sizing: border-box;
    padding: 60rpx 24rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(180deg, #f8fbff 0%, #f6f7fb 60%, #f6f7fb 100%);
  }

  .sound-wrap {
    width: 100%;
    max-width: 680rpx;
  }

  .sound-card {
    background: #ffffff;
    border-radius: 24rpx;
    padding: 26rpx 28rpx 28rpx;
    box-shadow: 0 16rpx 44rpx rgba(32, 77, 143, 0.12);
  }

  .sound-head {
    display: flex;
    align-items: center;
    padding-bottom: 18rpx;
  }

  .sound-badge {
    width: 72rpx;
    height: 72rpx;
    border-radius: 36rpx;
    background: rgba(47, 128, 255, 0.12);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16rpx;
    color: #2f80ff;
    font-size: 36rpx;
    line-height: 1;
  }

  .sound-head-text {
    flex: 1;
  }

  .sound-title {
    font-size: 34rpx;
    font-weight: 600;
    color: #111111;
    line-height: 1.2;
  }

  .sound-subtitle {
    margin-top: 8rpx;
    font-size: 24rpx;
    color: #8d8d8d;
    line-height: 1.2;
  }

  .sound-list {
    margin-top: 12rpx;
    display: flex;
    flex-direction: column;
    gap: 14rpx;
  }

  .sound-item {
    background: #f7f9ff;
    border: 1rpx solid rgba(47, 128, 255, 0.10);
    border-radius: 18rpx;
    padding: 22rpx 20rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .sound-item-text {
    font-size: 30rpx;
    color: #222222;
  }
</style>
