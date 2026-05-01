<template>
  <view class="track-page">
    <view class="track-content">
      <image
        class="track-bg"
        src="/static/img/index/ZhuSuanQiMeng.png"
        mode="aspectFill"
      />
      <view class="track-info">
        <view class="track-title">珠算</view>
        <view class="track-desc">操作算盘进行计算，学习传统计算方式</view>
        <view class="track-levels" v-if="loading">
          <view class="loading-text">加载中...</view>
        </view>
        <view class="track-leve ls" v-else>
          <view
            class="level-item"
            :class="{ 'level-locked': level.isUnlocked === 0 }"
            v-for="(level, index) in levelList"
            :key="level.levelId || index"
            @click="startLevel(index)"
          >
            <text class="level-name">{{ level.levelName || `关卡 ${index + 1}` }}</text>
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
  </view>
</template>

<script>
import mixin from '@/libs/mixins/page.js';
import { getLevelProgressApi } from '@/api/game.js';

export default {
  mixins: [mixin],
  data() {
    return {
      trackCode: 'abacus',
      trackId: 3, // 珠算赛道ID
      levelList: [],
      loading: true,
      gamerId: null
    };
  },
  onLoad(options) {
    this.getGamerId();
  },
  onShow() {
    if (this.gamerId) {
      this.loadLevelProgress();
    }
  },
  methods: {
    getGamerId() {
      if (!this.token) {
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
      const userInfo = this.userInfo || {};
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
            const totalStarsRaw = level.totalStars ?? level.total_stars;
            const totalStars = Number.isNaN(Number(totalStarsRaw)) ? 0 : Number(totalStarsRaw);

            return {
              ...level,
              isCompleted: isCompleted ? 1 : 0,
              totalStars,
            };
          });

          this.levelList = tempList.map((level, index) => {
            let shouldUnlock = false;

            if (index === 0) {
              shouldUnlock = true;
            } else if (index === 1) {
              const level1Stars = tempList[0].totalStars;
              shouldUnlock = level1Stars >= 9;
            } else if (index > 1) {
              const prevStars = tempList[index - 1].totalStars;
              shouldUnlock = prevStars > 20;
            }

            let status = '';
            if (shouldUnlock) {
              status = `${level.totalStars}星`;
            } else {
              status = '未解锁';
            }

            return {
              ...level,
              isUnlocked: shouldUnlock ? 1 : 0,
              status,
            };
          });

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
      const level = this.levelList[index];
      if (!level) {
        console.error('关卡数据不存在', index, this.levelList);
        return;
      }

      console.log('点击关卡:', index, 'level数据:', level);

      if (level.status && level.status.includes('未解锁')) {
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
      if (index === 0) {
        this.$navTo(`/pagesC/ZhuSuanQiMeng/level1?track_id=${this.trackId}&level_id=${level.levelId}&gamer_id=${this.gamerId}`);
      } else if (index === 1) {
        this.$navTo(`/pagesC/ZhuSuanQiMeng/level2?track_id=${this.trackId}&level_id=${level.levelId}&gamer_id=${this.gamerId}`);
      } else if (index === 2) {
        this.$navTo(`/pagesC/ZhuSuanQiMeng/level3?track_id=${this.trackId}&level_id=${level.levelId}&gamer_id=${this.gamerId}`);
      } else if (index === 3) {
        this.$navTo(`/pagesC/ZhuSuanQiMeng/level4?track_id=${this.trackId}&level_id=${level.levelId}&gamer_id=${this.gamerId}`);
      } else if (index === 4) {
        this.$navTo(`/pagesC/ZhuSuanQiMeng/level5?track_id=${this.trackId}&level_id=${level.levelId}&gamer_id=${this.gamerId}`);
      } else if (index === 5) {
        this.$navTo(`/pagesC/ZhuSuanQiMeng/level6?track_id=${this.trackId}&level_id=${level.levelId}&gamer_id=${this.gamerId}`);
      } else {
        this.$navTo(`/pagesC/game_levels/details?track_code=${this.trackCode}&level_id=${level.levelId}&level_name=${level.levelName}`);
      }
    },

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
</style>
