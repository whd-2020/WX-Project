<template>
  <view class="page_sound" id="page_sound" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <view class="page-content">
      <view class="sound-container">
        <view class="sound-title">系统音效设置</view>
        
        <view class="sound-list">
          <view class="sound-item">
            <view class="sound-item-left">
              <text class="sound-icon">🔊</text>
              <text class="sound-label">点击音效</text>
            </view>
            <view class="sound-item-right">
              <switch 
                :checked="clickSoundEnabled" 
                @change="toggleClickSound" 
                color="#2f80ff"
              />
            </view>
          </view>
          
          <view class="sound-item">
            <view class="sound-item-left">
              <text class="sound-icon">🎵</text>
              <text class="sound-label">背景音乐</text>
            </view>
            <view class="sound-item-right">
              <switch 
                :checked="bgmEnabled" 
                @change="toggleBgm" 
                color="#2f80ff"
              />
            </view>
          </view>
        </view>
        
        <view class="sound-tip">
          <text class="tip-text">全局生效，实时控制</text>
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
      clickSoundEnabled: true,
      bgmEnabled: true
    };
  },
  onLoad() {
    this.loadSettings();
  },
  methods: {
    loadSettings() {
      this.clickSoundEnabled = this.$audio.isClickSoundEnabled();
      this.bgmEnabled = this.$audio.isBgmEnabled();
    },
    toggleClickSound(e) {
      this.playClickSound();
      const enabled = e.detail.value;
      this.clickSoundEnabled = enabled;
      this.$audio.setClickSoundEnabled(enabled);
      this.$toast(enabled ? '点击音效已开启' : '点击音效已关闭');
    },
    toggleBgm(e) {
      this.playClickSound();
      const enabled = e.detail.value;
      this.bgmEnabled = enabled;
      this.$audio.setBgmEnabled(enabled);
      this.$toast(enabled ? '背景音乐已开启' : '背景音乐已关闭');
    }
  }
};
</script>

<style lang="scss" scoped>
.page_sound {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8f9fe 0%, #ffffff 100%);
}

.page-content {
  padding: 30rpx;
}

.sound-container {
  background-color: #fff;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
  padding: 40rpx;
}

.sound-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #2c3e50;
  text-align: center;
  margin-bottom: 40rpx;
}

.sound-list {
  display: flex;
  flex-direction: column;
}

.sound-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
  
  &:last-child {
    border-bottom: none;
  }
}

.sound-item-left {
  display: flex;
  align-items: center;
}

.sound-icon {
  font-size: 40rpx;
  margin-right: 20rpx;
}

.sound-label {
  font-size: 32rpx;
  color: #333;
}

.sound-tip {
  margin-top: 40rpx;
  padding: 20rpx;
  background: rgba(47, 128, 255, 0.05);
  border-radius: 12rpx;
}

.tip-text {
  font-size: 24rpx;
  color: #667eea;
}
</style>
