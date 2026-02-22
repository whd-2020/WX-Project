<template>
  <view class="track-page" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <tn-nav-bar>筹算</tn-nav-bar>
    <view class="track-content">
      <image 
        class="track-bg" 
        src="/static/images/games/basic_counting_rods.png" 
        mode="aspectFill"
      />
      <view class="track-info">
        <view class="track-title">筹算</view>
        <view class="track-desc">使用算筹进行计数和运算，感受古代数学工具</view>
        <view class="track-levels" v-if="loading">
          <view class="loading-text">加载中...</view>
        </view>
        <view class="track-levels" v-else>
          <view 
            class="level-item" 
            :class="{ 'level-locked': level.isUnlocked === 0 }"
            v-for="(level, index) in levelList" 
            :key="level.levelId || index" 
            @click="startLevel(level)"
          >
            <text class="level-name">{{ level.levelName || `关卡 ${index + 1}` }}</text>
            <text 
              class="level-status" 
              :class="{
                'status-unlocked': level.isUnlocked === 1 && level.isCompleted === 0,
                'status-completed': level.isCompleted === 1,
                'status-locked': level.isUnlocked === 0
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
      trackCode: 'counting_rods',
      trackId: 2, // 筹算赛道ID
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
      // 先根据 user_id 查询 gamer 记录
      this.$get('/gamer/get_obj', { user_id: userId }, (json) => {
        const gamerObj = json.result ? (json.result.obj || json.result) : null;
        if (gamerObj && gamerObj.gamer_id) {
          // 已有玩家记录
          this.gamerId = gamerObj.gamer_id;
          this.loadLevelProgress();
        } else {
          // 没有玩家记录时，自动创建一条
          // 统一使用 username 作为玩家展示名
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
      if (!this.gamerId) return;
      this.loading = true;
      try {
        const res = await getLevelProgressApi(this.gamerId, this.trackId);
        if (res.result && res.result.levels) {
          this.levelList = res.result.levels;
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
    startLevel(level) {
      if (level.isUnlocked === 0) {
        uni.showToast({
          title: '该关卡尚未解锁',
          icon: 'none'
        });
        return;
      }
      this.$navTo(`/pagesC/game_levels/details?track_code=${this.trackCode}&level_id=${level.levelId}&level_name=${level.levelName}`);
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
