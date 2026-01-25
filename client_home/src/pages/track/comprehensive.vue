<template>
  <view class="track-page" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <tn-nav-bar>综合挑战</tn-nav-bar>
    <view class="track-content">
      <image 
        class="track-bg" 
        src="/static/images/games/level_challenge.png" 
        mode="aspectFill"
      />
      <view class="track-info">
        <view class="track-title">综合挑战</view>
        <view class="track-desc">综合运用三种计数方式，挑战更高难度</view>
        <view class="track-levels">
          <view class="level-item" v-for="(level, index) in levelList" :key="index" @click="startLevel(level)">
            <text class="level-name">{{ level.name || `关卡 ${index + 1}` }}</text>
            <text class="level-status">{{ level.status }}</text>
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
      trackCode: 'comprehensive',
      levelList: [
        { name: '关卡一', status: '已解锁' },
        { name: '关卡二', status: '已解锁' },
        { name: '关卡三', status: '未解锁' },
      ]
    };
  },
  methods: {
    startLevel(level) {
      if (level.status === '未解锁') {
        uni.showToast({
          title: '该关卡尚未解锁',
          icon: 'none'
        });
        return;
      }
      // 跳转到游戏关卡详情页
      this.$navTo(`/pagesC/game_levels/details?track_code=${this.trackCode}&level_name=${level.name}`);
    }
  }
};
</script>

<style lang="scss" scoped>
.track-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.track-content {
  position: relative;
  width: 100%;
}

.track-bg {
  width: 100%;
  height: 400rpx;
}

.track-info {
  padding: 40rpx;
  background-color: #fff;
  margin-top: -20rpx;
  border-radius: 20rpx 20rpx 0 0;
}

.track-title {
  font-size: 48rpx;
  font-weight: 700;
  color: #333;
  margin-bottom: 20rpx;
}

.track-desc {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 40rpx;
}

.track-levels {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.level-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  background-color: #f8f8f8;
  border-radius: 12rpx;
  transition: all 0.3s;
  
  &:active {
    background-color: #eeeeee;
    transform: scale(0.98);
  }
}

.level-name {
  font-size: 32rpx;
  font-weight: 500;
  color: #333;
}

.level-status {
  font-size: 24rpx;
  color: #999;
  padding: 8rpx 16rpx;
  background-color: #e8e8e8;
  border-radius: 8rpx;
}
</style>
