<template>
  <view class="splash-container">
    <!-- 返回箭头 -->
    <view class="back-arrow" @click="goBack"></view>
    <!-- 游戏首页背景图 -->
    <image 
      class="splash-bg" 
      :src="splashImage" 
      mode="aspectFill"
    />
    
    <!-- 进度条容器 - 放在底部 -->
    <view class="progress-container">
      <view class="progress-wrapper">
        <view 
          class="progress-bar" 
          :style="{ width: progress + '%' }"
        ></view>
      </view>
      <view class="progress-text">{{ Math.min(Math.round(progress), 100) }}%</view>
      
      <!-- 开始游戏按钮 - 进度完成后显示 -->
      <view 
        class="start-button" 
        v-if="isProgressComplete"
        @click="navigateToHome"
      >
        <text class="button-text">开始游戏</text>
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
      splashImage: '/static/images/games/game_home.png',
      progress: 0,
      progressTimer: null,
      isProgressComplete: false, // 进度是否完成
    };
  },
  onLoad() {
    this.startProgress();
  },
  onUnload() {
    if (this.progressTimer) {
      clearInterval(this.progressTimer);
    }
  },
  methods: {
    startProgress() {
      // 模拟加载进度，可以根据实际需求调整速度
      const duration = 3000; // 总时长3秒
      const interval = 30; // 每30ms更新一次
      const increment = (100 / duration) * interval;
      
      this.progressTimer = setInterval(() => {
        this.progress += increment;
        
        if (this.progress >= 100) {
          this.progress = 100;
          clearInterval(this.progressTimer);
          
          // 进度完成后，显示开始游戏按钮
          setTimeout(() => {
            this.isProgressComplete = true;
          }, 300);
        }
      }, interval);
    },
    
    navigateToHome() {
      // 跳转到首页（首页是tabBar页面，使用switchTab）
      uni.switchTab({
        url: '/pages/index/index',
        success: () => {
          console.log('跳转到首页成功');
        },
        fail: (err) => {
          console.error('跳转失败', err);
          // 如果switchTab失败，尝试使用redirectTo
          uni.redirectTo({
            url: '/pages/index/index'
          });
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.splash-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
  background-color: #fff;
  z-index: 9999;
  overflow: hidden;
}

.splash-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.progress-container {
  position: relative;
  z-index: 2;
  width: 80%;
  max-width: 600rpx;
  margin-bottom: 100rpx;
  padding: 0 40rpx;
}

.progress-wrapper {
  width: 100%;
  height: 12rpx;
  background-color: rgba(255, 255, 255, 0.4);
  border-radius: 6rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10rpx);
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #FF6B6B 0%, #FF8E53 50%, #FFA726 100%);
  border-radius: 6rpx;
  transition: width 0.05s linear;
  box-shadow: 0 0 20rpx rgba(255, 107, 107, 0.6);
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(
      90deg,
      transparent 0%,
      rgba(255, 255, 255, 0.3) 50%,
      transparent 100%
    );
    animation: shimmer 1.5s infinite;
  }
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

.progress-text {
  margin-top: 24rpx;
  text-align: center;
  font-size: 32rpx;
  color: #333;
  font-weight: 600;
  text-shadow: 0 2rpx 4rpx rgba(255, 255, 255, 0.9);
  letter-spacing: 2rpx;
}

.start-button {
  margin-top: 40rpx;
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 50%, #FFA726 100%);
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(255, 107, 107, 0.4);
  transition: all 0.3s ease;
  animation: buttonFadeIn 0.5s ease-out;
  
  &:active {
    transform: scale(0.98);
    box-shadow: 0 4rpx 12rpx rgba(255, 107, 107, 0.3);
  }
}

@keyframes buttonFadeIn {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.button-text {
  font-size: 36rpx;
  font-weight: 700;
  color: #fff;
  letter-spacing: 4rpx;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.2);
}
</style>
