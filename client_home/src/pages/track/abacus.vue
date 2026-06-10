<template>
  <view class="track-page">
    <view class="track-content">
      <image
        class="track-bg"
        src="/static/images/games/ZhuSuanQiMeng.png"
        mode="aspectFill"
      />
      <view class="track-info">
        <view class="track-title">珠算</view>
        <view class="desc-row">
          <view class="track-desc">操作算盘进行计算，学习传统计算方式</view>
          <view class="info-btn" @click="showIntroCard = true">
            <text class="star-icon">⭐</text>
          </view>
        </view>
        <view class="track-levels" v-if="loading">
          <view class="loading-text">加载中...</view>
        </view>
        <view class="track-levels" v-else>
          <view
            class="level-item"
            :class="{ 'level-locked': level.isUnlocked === 0 }"
            v-for="(level, index) in levelList"
            :key="level.levelId || index"
            @click="startLevel(index)"
          >
            <text class="level-name">{{ `关卡${['一','二','三','四','五'][index] || index + 1}：${level.levelName}` }}</text>
            <text
              class="level-status"
              :class="{
                'status-unlocked': level.isUnlocked === 1 && level.isCompleted === 0,
                'status-completed': level.isCompleted === 1,
                'status-locked': level.isUnlocked === 0,
                'status-stars': level.status && level.status.includes('星')
              }"
            >
              {{ level.status }}
            </text>
          </view>
        </view>
      </view>
    </view>

    <view class="modal-overlay" v-if="showIntroCard" @click="closeIntroCard">
      <view class="intro-card" :class="{ 'card-visible': showIntroCard }" @click.stop>
        <view class="card-wood-top"></view>
        <view class="card-header">
          <view class="card-header-inner">
            <text class="card-title">📜 珠算入门</text>
          </view>
        </view>
        <view class="card-content">
          <text class="intro-text">欢迎来到明代账房算术世界！这里是商号最核心的记账之地。为方便小朋友启蒙学习，我们采用简易十进制算盘：横梁下方珠子往上拨代表1，横梁上方珠子往下拨代表5。快坐到柜台前，面对算盘，根据任务给出的数字拨动算珠，感受传承千年的计算智慧吧！</text>
        </view>
        <view class="card-wood-bottom"></view>
      </view>
    </view>
  </view>
</template>

<script>
import mixin from '@/libs/mixins/page.js';
import { getLevelProgressApi } from '@/api/game.js';
import store from '@/store';

export default {
  mixins: [mixin],
  data() {
    return {
      trackCode: 'abacus',
      trackId: 3,
      levelList: [],
      loading: true,
      gamerId: null,
      showIntroCard: false
    };
  },
  onLoad(options) {
    this.getGamerId();
  },
  onShow() {
    if (this.gamerId) {
      this.loadLevelProgress();
    }
    // 只在第一次进入时显示介绍卡片
    const hasSeenIntro = uni.getStorageSync('abacus_intro_shown');
    if (!hasSeenIntro) {
      setTimeout(() => {
        this.showIntroCard = true;
      }, 300);
    }
  },
  methods: {
    getFixedLevelName(levelNo) {
      const names = {
        1: '初识感知关',
        2: '个位数基础练习关',
        3: '账房小学徒任务关',
        4: '两位数进阶练习关',
        5: '账房先生任务关'
      };
      return names[levelNo] || `关卡 ${levelNo}`;
    },

    getGamerId() {
      const token = store.state.app.token;
      const userInfo = store.state.app.userInfo || {};
      
      if (!token) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        });
        setTimeout(() => {
          uni.navigateTo({
            url: '/pagesB/account/login'
          });
        }, 1500);
        return;
      }
      const userId = userInfo.user_id;
      if (!userId) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        });
        setTimeout(() => {
          uni.navigateTo({
            url: '/pagesB/account/login'
          });
        }, 1500);
        return;
      }
      this.$get('/gamer/get_obj', { user_id: userId }, (json) => {
        const gamerObj = json.result ? (json.result.obj || json.result) : null;
        if (gamerObj && gamerObj.gamer_id) {
          this.gamerId = gamerObj.gamer_id;
          this.loadLevelProgress();
        } else {
          const screenName = userInfo.username || ('玩家' + userId);
          const data = {
            player_screen_name: screenName,
            gold_coin_balance: 0,
            user_id: userId
          };
          this.$post('~/api/gamer/add?', data, (res) => {
            if (res.result) {
              this.$get('/gamer/get_obj', { user_id: userId }, (json2) => {
                const gamerObj2 = json2.result ? (json2.result.obj || json2.result) : null;
                if (gamerObj2 && gamerObj2.gamer_id) {
                  this.gamerId = gamerObj2.gamer_id;
                  this.loadLevelProgress();
                }
              });
            } else if (res.error) {
              uni.showToast({
                title: res.error.message || '创建玩家信息失败',
                icon: 'none'
              });
            }
          });
        }
      });
    },
    async loadLevelProgress() {
      if (!this.gamerId) {
        return;
      }

      this.loading = true;
      try {
        const res = await getLevelProgressApi(this.gamerId, this.trackId);
        if (res.result && res.result.levels) {
          const tempList = res.result.levels.map((level) => {
            const isCompleted = Number(level.isCompleted ?? level.is_completed ?? 0) === 1;
            const isUnlocked = Number(level.isUnlocked ?? level.is_unlocked ?? 0) === 1;
            const totalStarsRaw = level.totalStars ?? level.total_stars;
            const totalStars = Number.isNaN(Number(totalStarsRaw)) ? 0 : Number(totalStarsRaw);
            const levelId = Number(level.levelId ?? level.level_id ?? level.levelOrder ?? level.level_order ?? 0);
            const levelOrder = Number(level.levelOrder ?? level.level_order ?? levelId);

            return {
              ...level,
              levelId,
              levelOrder,
              levelName: this.getFixedLevelName(levelOrder),
              isCompleted: isCompleted ? 1 : 0,
              isUnlocked: isUnlocked ? 1 : 0,
              totalStars,
              status: level.status || (totalStars > 0 ? `${totalStars}星` : (isUnlocked ? '已解锁' : '未解锁')),
            };
          });

          this.levelList = tempList.sort((a, b) => Number(a.levelOrder) - Number(b.levelOrder));

          console.log('关卡列表数据:', this.levelList);
        } else {
          uni.showToast({
            title: res.error?.message || '获取关卡进度失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('获取关卡进度失败:', error);
        uni.showToast({
          title: '获取关卡进度失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },
    startLevel(index) {
      this.playClickSound();
      const level = this.levelList[index];
      if (!level) {
        console.error('关卡数据不存在', index, this.levelList);
        return;
      }

      console.log('点击关卡:', index, 'level数据:', level);

      if (Number(level.isUnlocked) === 0) {
        console.log('关卡未解锁，阻止跳转');
        uni.showModal({
          title: '提示',
          content: '小朋友，你还没有解锁这一关哦~',
          showCancel: false,
          confirmText: '知道了'
        });
        return;
      }

      console.log('关卡已解锁，准备跳转');
      const levelNo = Number(level.levelOrder || level.levelId || 0);
      this.$navTo(`/pagesC/ZhuSuanQiMeng/level${levelNo}?track_id=${this.trackId}&level_id=${level.levelId}&gamer_id=${this.gamerId}`);
    },

    closeIntroCard() {
      this.playClickSound();
      this.showIntroCard = false;
      // 标记已经展示过，下次不自动显示
      uni.setStorageSync('abacus_intro_shown', true);
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

.desc-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 40rpx;
}

.track-desc {
  flex: 1;
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
}

.info-btn {
  width: 60rpx;
  height: 60rpx;
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(255, 215, 0, 0.5);
  transition: all 0.3s ease;
  flex-shrink: 0;
  border: 3rpx solid #FFE4B5;

  &:active {
    transform: scale(0.9);
    box-shadow: 0 2rpx 8rpx rgba(255, 215, 0, 0.4);
  }
}

.star-icon {
  font-size: 36rpx;
  line-height: 1;
  animation: starPulse 2s ease-in-out infinite;
}

@keyframes starPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
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
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  transition: all 0.3s;
}

.status-unlocked {
  color: #666;
  background-color: #e8e8e8;
}

.status-completed {
  color: #fff;
  background-color: #4CAF50;
}

.status-locked {
  color: #999;
  background-color: #e0e0e0;
}

.status-stars {
  color: #ff9800;
  font-weight: 600;
}

.level-locked {
  opacity: 0.6;
}

.level-item.level-locked:active {
  transform: none;
}

.loading-text {
  text-align: center;
  padding: 40rpx;
  color: #999;
  font-size: 28rpx;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(ellipse at center, rgba(139, 105, 20, 0.35) 0%, rgba(0, 0, 0, 0.7) 60%, rgba(0, 0, 0, 0.8) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: overlayFadeIn 0.4s ease;
  backdrop-filter: blur(4px);
}

@keyframes overlayFadeIn {
  from {
    opacity: 0;
    background: rgba(0, 0, 0, 0);
  }
  to {
    opacity: 1;
    background: radial-gradient(ellipse at center, rgba(139, 105, 20, 0.35) 0%, rgba(0, 0, 0, 0.7) 60%, rgba(0, 0, 0, 0.8) 100%);
  }
}

.intro-card {
  width: 85%;
  max-width: 680rpx;
  background: linear-gradient(180deg, #FAF0E6 0%, #F4E4BC 30%, #E8D5A3 70%, #DCC48D 100%);
  border-radius: 16rpx;
  box-shadow: 
    0 0 0 6rpx #5D4037,
    0 0 0 10rpx #8B6914,
    0 0 0 14rpx #D4A574,
    0 24rpx 80rpx rgba(0, 0, 0, 0.4),
    inset 0 2rpx 4rpx rgba(255, 255, 255, 0.5);
  overflow: hidden;
  transform-style: preserve-3d;
  transform: perspective(1200px) rotateY(90deg) rotateX(-15deg) scale(0.75);
  opacity: 0;
  transition: all 0.7s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
}

.intro-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(139, 105, 20, 0.03) 2px,
    rgba(139, 105, 20, 0.03) 4px
  );
  pointer-events: none;
  z-index: 1;
}

.intro-card.card-visible {
  transform: perspective(1200px) rotateY(0deg) rotateX(0deg) scale(1);
  opacity: 1;
}

@keyframes cardRotateIn {
  0% {
    transform: perspective(1000px) rotateY(360deg) rotateX(0deg) scale(0.6);
    opacity: 0;
  }
  50% {
    transform: perspective(1000px) rotateY(180deg) rotateX(0deg) scale(0.95);
    opacity: 0.9;
  }
  80% {
    transform: perspective(1000px) rotateY(45deg) rotateX(0deg) scale(1.02);
    opacity: 1;
  }
  100% {
    transform: perspective(1000px) rotateY(0deg) rotateX(0deg) scale(1);
    opacity: 1;
  }
}

.intro-card.card-visible {
  animation: cardRotateIn 1.2s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  background: linear-gradient(180deg, #8B6914 0%, #6B4423 50%, #8B6914 100%);
  border-bottom: 4rpx solid #D4A574;
}

.card-header-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40rpx;
}

.card-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #F4E4BC;
  text-shadow: 2rpx 2rpx 4rpx rgba(0, 0, 0, 0.4);
  letter-spacing: 4rpx;
}

.close-btn {
  width: 56rpx;
  height: 56rpx;
  background: linear-gradient(180deg, #A08060 0%, #6B4423 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  border: 2rpx solid #D4A574;

  &:active {
    transform: scale(0.9);
  }
}

.close-icon {
  font-size: 36rpx;
  color: #F4E4BC;
  line-height: 1;
  font-weight: bold;
}

.card-content {
  padding: 40rpx;
  background: linear-gradient(180deg, #FAF0E6 0%, #F4E4BC 100%);
  min-height: 280rpx;
}

.intro-text {
  font-size: 28rpx;
  color: #5D4037;
  line-height: 1.8;
  text-align: justify;
  text-indent: 56rpx;
  font-family: '楷体', 'KaiTi', serif;
}

.card-wood-top,
.card-wood-bottom {
  height: 32rpx;
  background: linear-gradient(90deg, #4A3728 0%, #8B5A2B 20%, #A08060 50%, #8B5A2B 80%, #4A3728 100%);
  border-radius: 4rpx;
}

.card-wood-top {
  margin-top: 8rpx;
  box-shadow: inset 0 2rpx 4rpx rgba(255, 255, 255, 0.2), 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
}

.card-wood-bottom {
  margin-bottom: 8rpx;
  box-shadow: inset 0 -2rpx 4rpx rgba(255, 255, 255, 0.2), 0 -2rpx 4rpx rgba(0, 0, 0, 0.3);
}
</style>
