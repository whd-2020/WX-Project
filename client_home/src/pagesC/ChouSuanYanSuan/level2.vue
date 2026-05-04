<template>
  <view class="chouSuan-level-page">

    <!-- 背景图片 -->
    <image class="background-image" src="/static/img/rope/CaoYuanBeiJing.png" mode="aspectFill" />

    <!-- 默认题目提示 -->
    <view class="default-question-tip" v-if="showDefaultQuestionTip">
      <text class="tip-text">⚠️ 当前为默认题目，请联系运维人员</text>
    </view>

    <view class="scene">
      <!-- 左侧：族长 -->
      <view class="elder-area">
        <view class="speech-bubble" :class="{ 'expanded': showFullSpeech }" @click="toggleSpeech">
          <text class="speech-text">{{ displayText }}</text>
        </view>
        <image class="elder-img" src="/static/img/rope/LaoRen.png" mode="aspectFit" />
      </view>

      <!-- 右侧：小孩 -->
      <view class="child-area" @click="openGamePopup">
        <view class="child-speech-bubble" v-if="showChildSpeech">
          <text class="child-speech-text">{{ childSpeechText }}</text>
        </view>
        <view class="child-tip" v-if="showChildTip">
          <text class="tip-text">点击这里，来试试吧</text>
        </view>
        <image class="child-img" src="/static/img/rope/XiaoHai.png" mode="aspectFit" />
      </view>
    </view>

    <!-- 游戏弹窗 -->
    <view class="popup-mask" v-if="showGamePopup" @click="closeGamePopup"></view>
    <view class="game-popup" v-if="showGamePopup" @click.stop>
      <!-- 筹算木棍互动区域 -->
      <view class="chouSuan-area">
        <view class="chouSuan-title">{{ question && question.questionTitle ? question.questionTitle : '点击放置木棍' }}</view>

        <!-- 答题展示区域 -->
        <view class="answer-area">
          <view class="answer-box">
            <view class="answer-base">
              <view
                class="horizontal-stick-display"
                v-if="hasHorizontalStick"
                @click="removeHorizontalStick"
              >
              </view>
              <view class="vertical-sticks-container">
                <view
                  v-for="(stick, index) in verticalSticks"
                  :key="stick.id"
                  class="vertical-stick-display"
                  @click="removeVerticalStick(index)"
                >
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 提示信息 -->
        <view class="tip-message">
          {{ getTipMessage() }}
        </view>

        <!-- 清空按钮 -->
        <view class="toggle-btn-container">
          <view class="clear-btn" @click="resetAnswer">
            清空
          </view>
        </view>

        <!-- 底部资源区 -->
        <view class="resources-container">
          <view class="resource-area">
            <view class="stick-card horizontal-card" @click="handleHorizontalStickClick">
              <view class="stick-preview horizontal-stick-preview"></view>
            </view>
          </view>
          <view class="resource-area">
            <view class="stick-card vertical-card" @click="handleVerticalStickClick">
              <view class="stick-preview vertical-stick-preview"></view>
            </view>
          </view>
        </view>

        <!-- 木棍含义提示 -->
        <view class="stick-tip">
          <text class="stick-tip-text">横棍代表5，竖棍代表1</text>
        </view>

        <view class="chouSuan-info">
          <view class="number-info">
            <text>当前数字：{{ currentNumber }}</text>
          </view>
          <text class="time-text">用时：{{ formatTime(elapsedSeconds) }}</text>
        </view>
      </view>

      <!-- 底部操作区：只保留提交按钮 -->
      <view class="bottom-bar">
        <button
          class="btn-submit"
          :class="{ 'btn-disabled': !canSubmit }"
          type="primary"
          @click="submitAnswer"
          :disabled="!canSubmit"
        >提交答案</button>
      </view>
    </view>

    <!-- 自定义成功弹窗 -->
    <view class="success-modal" v-if="showSuccessModal" @click="closeSuccessModal">
      <view class="modal-content" @click.stop>
        <view class="modal-icon">✓</view>
        <view class="modal-title">{{ successMessage }}</view>
        <view class="modal-stars">
          <text
            v-for="(star, index) in 3"
            :key="index"
            class="star"
            :class="{ 'star-active': index < successStarCount }"
          >
            ⭐
          </text>
        </view>
        <view class="modal-time">用时：{{ formatTime(successTime) }}</view>
        <button class="modal-btn" @click="closeSuccessModal">继续挑战</button>
      </view>
    </view>

    <!-- 所有题目满3星提示弹窗 -->
    <view class="success-modal" v-if="showAllCompleteModal">
      <view class="modal-content all-complete-content" @click.stop>
        <view class="modal-icon">🎉</view>
        <view class="modal-title">小朋友你也太厉害了，前往下一关吧！</view>
        <view class="modal-stars">
          <text class="star star-active">⭐</text>
          <text class="star star-active">⭐</text>
          <text class="star star-active">⭐</text>
        </view>
        <view class="modal-desc">本关卡所有题目都已获得3颗星！</view>
        <view class="modal-tip">3秒后自动跳转...</view>
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
      gamerId: null,
      trackId: 2,
      levelId: 2,
      routeLevelId: 2,
      question: null,
      fullText: '今日族长正在思考要出什么题目给你……',
      displayText: '',
      typingTimer: null,
      hasHorizontalStick: false,
      verticalSticks: [],
      startTime: 0,
      elapsedSeconds: 0,
      elapsedTimer: null,
      showSuccessModal: false,
      successMessage: '',
      successStarCount: 0,
      successTime: 0,
      showAllCompleteModal: false,
      showDefaultQuestionTip: false,
      defaultQuestionTipTimer: null,
      showFullSpeech: false,
      showGamePopup: false,
      showChildSpeech: false,
      childSpeechText: '',
      childSpeechTimer: null,
      showChildTip: false,
      childTipTimer: null,
    };
  },
  onLoad(options) {
    if (options && options.gamer_id) {
      const gid = Number(options.gamer_id);
      if (!Number.isNaN(gid) && gid > 0) {
        this.gamerId = gid;
      }
    }
    if (options && options.level_id) {
      const lid = Number(options.level_id);
      if (!Number.isNaN(lid) && lid > 0) {
        this.levelId = lid;
        this.routeLevelId = lid;
      }
    }
    if (options && options.track_id) {
      const tid = Number(options.track_id);
      if (!Number.isNaN(tid) && tid > 0) {
        this.trackId = tid;
      }
    }
    this.resetTimer();
    this.fetchQuestion();
  },
  computed: {
    targetNumberText() {
      if (this.question && this.question.targetNumber) {
        return this.question.targetNumber;
      }
      return '';
    },
    currentNumber() {
      let num = 0;
      if (this.hasHorizontalStick) {
        num += 5;
      }
      num += this.verticalSticks.length;
      return num;
    },
    canSubmit() {
      if (!this.question || !this.question.targetNumber) {
        return false;
      }
      const target = Number(this.question.targetNumber);
      return this.currentNumber === target && this.currentNumber > 0;
    },
  },
  methods: {
    playTyping(text) {
      this.fullText = text;
      this.displayText = '';
      this.showFullSpeech = false;
      this.showChildSpeech = false;
      this.showChildTip = false;
      this.childSpeechText = '';
      if (this.childTipTimer) {
        clearTimeout(this.childTipTimer);
        this.childTipTimer = null;
      }
      if (this.typingTimer) {
        clearInterval(this.typingTimer);
      }
      let index = 0;
      this.typingTimer = setInterval(() => {
        if (index >= this.fullText.length) {
          clearInterval(this.typingTimer);
          this.typingTimer = null;
          this.displayChildSpeech();
          return;
        }
        this.displayText += this.fullText[index];
        index++;
      }, 80);
    },

    displayChildSpeech() {
      this.childSpeechText = '好哒爷爷~，我来试一试';
      this.showChildSpeech = true;
      if (this.childTipTimer) {
        clearTimeout(this.childTipTimer);
        this.childTipTimer = null;
      }
      this.showChildTip = false;
      this.childTipTimer = setTimeout(() => {
        if (!this.showGamePopup) {
          this.showChildTip = true;
        }
      }, 6000);
    },

    resetTimer() {
      this.startTime = Date.now();
      this.elapsedSeconds = 0;
      if (this.elapsedTimer) {
        clearInterval(this.elapsedTimer);
      }
      this.elapsedTimer = setInterval(() => {
        this.elapsedSeconds += 1;
      }, 1000);
    },

    fetchQuestion() {
      const gamerId = Number(this.gamerId);
      const levelId = Number(this.routeLevelId || this.levelId);
      const trackId = Number(this.trackId);

      if (!gamerId || Number.isNaN(levelId) || levelId <= 0 || Number.isNaN(trackId) || trackId <= 0) {
        const local = {
          question_id: 0,
          targetNumber: 1,
          title: '今日族长笑着对你说：用筹算木棍表示数字 1，你会怎么摆放呢？',
        };
        this.question = local;
        this.playTyping(local.title);
        return;
      }
      const params = {
        gamerId,
        levelId,
        trackId,
      };
      this.$get('/question_bank/get_level_questions', params, (json) => {
        console.log('=== 后端返回参数 ===');
        console.log('完整返回:', JSON.stringify(json, null, 2));
        console.log('json.result:', json.result);
        if (json.result) {
          console.log('questions:', json.result.questions);
          console.log('allThreeStars (驼峰):', json.result.allThreeStars);
          console.log('all_three_stars (下划线):', json.result.all_three_stars);
          console.log('isCompleted (驼峰):', json.result.isCompleted);
          console.log('is_completed (下划线):', json.result.is_completed);
          console.log('questionCount (驼峰):', json.result.questionCount);
          console.log('question_count (下划线):', json.result.question_count);
          console.log('message:', json.result.message);
        }

        if (json && json.error) {
          uni.showToast({
            title: json.error.message || '获取题目失败',
            icon: 'none',
          });
          return;
        }

        const allThreeStars = json.result && (
          json.result.allThreeStars === true ||
          json.result.all_three_stars === true
        );

        console.log('判断所有题目满3星标识:', {
          allThreeStars: allThreeStars,
          allThreeStars_camel: json.result?.allThreeStars,
          all_three_stars_snake: json.result?.all_three_stars
        });

        if (allThreeStars) {
          console.log('✅ 检测到所有题目都满3星，显示弹窗并准备跳转');
          this.showAllCompleteModal = true;
          console.log('弹窗状态 showAllCompleteModal:', this.showAllCompleteModal);

          setTimeout(() => {
            console.log('3秒后自动跳转到关卡选择页面');
            this.showAllCompleteModal = false;
            uni.redirectTo({
              url: '/pages/track/counting_rods',
              success: () => {
                console.log('跳转成功');
              },
              fail: (err) => {
                console.error('跳转失败:', err);
              }
            });
          }, 3000);
          return;
        }

        if (!json.result || !json.result.questions || json.result.questions.length === 0) {
          const local = {
            question_id: 0,
            targetNumber: 1,
            title: '今日族长笑着对你说：用筹算木棍表示数字 1，你会怎么摆放呢？',
          };
          this.question = local;
          this.showDefaultQuestionTip = true;

          if (this.defaultQuestionTipTimer) {
            clearTimeout(this.defaultQuestionTipTimer);
          }

          this.defaultQuestionTipTimer = setTimeout(() => {
            this.showDefaultQuestionTip = false;
            this.defaultQuestionTipTimer = null;
          }, 2000);

          this.playTyping(local.title);
          return;
        }

        this.showDefaultQuestionTip = false;
        if (this.defaultQuestionTipTimer) {
          clearTimeout(this.defaultQuestionTipTimer);
          this.defaultQuestionTipTimer = null;
        }
        const list = json.result.questions;
        const q = list[Math.floor(Math.random() * list.length)];
        let target = 1;
        let elderSpeech = '';
        let questionTitle = '';

        try {
          if (q.question_content) {
            const content = JSON.parse(q.question_content);
            if (content.options && Array.isArray(content.options) && content.options.length > 0) {
              elderSpeech = (q.question_title || '') + "," + content.options[0];
              questionTitle = content.options[0];
            }
            if (content.targetNumber) {
              target = Number(content.targetNumber);
            }
          }

          if (target === 1 && q.correct_answer) {
            try {
              const answer = typeof q.correct_answer === 'string' ? JSON.parse(q.correct_answer) : q.correct_answer;
              if (answer.answer) {
                const answerNum = Number(answer.answer);
                if (!Number.isNaN(answerNum) && answerNum > 0) {
                  target = answerNum;
                }
              } else if (answer.count) {
                const countNum = Number(answer.count);
                if (!Number.isNaN(countNum) && countNum > 0) {
                  target = countNum;
                }
              }
            } catch (e) {
            }
          }
        } catch (e) {
        }

        if (!elderSpeech || elderSpeech.trim() === '') {
          elderSpeech = `今日族长笑着对你说：用筹算木棍表示数字 ${target}，你会怎么摆放呢？`;
        }

        if (!questionTitle || questionTitle.trim() === '') {
          questionTitle = `点击放置木棍，表示数字${target}`;
        }

        this.question = {
          question_id: q.question_id,
          targetNumber: target,
          title: elderSpeech,
          questionTitle: questionTitle,
        };
        this.playTyping(this.question.title);
      });
    },

    addHorizontalStick() {
      if (this.hasHorizontalStick) return;
      this.hasHorizontalStick = true;
    },

    removeHorizontalStick() {
      if (!this.hasHorizontalStick) return;
      this.hasHorizontalStick = false;
    },

    addVerticalStick() {
      const maxVertical = this.hasHorizontalStick ? 4 : 5;
      if (this.verticalSticks.length >= maxVertical) return;

      const newStick = {
        id: Date.now() + '_' + this.verticalSticks.length
      };
      this.verticalSticks.push(newStick);
    },

    removeVerticalStick(index) {
      if (index >= 0 && index < this.verticalSticks.length) {
        this.verticalSticks.splice(index, 1);
      }
    },

    removeLastVerticalStick() {
      if (this.verticalSticks.length > 0) {
        this.verticalSticks.pop();
      }
    },

    toggleHorizontalStick() {
      this.hasHorizontalStick = !this.hasHorizontalStick;
    },

    toggleVerticalStick() {
      const maxVertical = this.hasHorizontalStick ? 4 : 5;
      if (this.verticalSticks.length > 0) {
        this.verticalSticks.pop();
      } else if (this.verticalSticks.length < maxVertical) {
        const newStick = {
          id: Date.now() + '_' + this.verticalSticks.length
        };
        this.verticalSticks.push(newStick);
      }
    },

    resetAnswer() {
      this.hasHorizontalStick = false;
      this.verticalSticks = [];
    },

    handleHorizontalStickClick() {
      if (!this.hasHorizontalStick) {
        this.hasHorizontalStick = true;
      }
    },

    handleVerticalStickClick() {
      const maxVertical = this.hasHorizontalStick ? 4 : 5;
      if (this.verticalSticks.length < maxVertical) {
        const newStick = {
          id: Date.now() + '_' + this.verticalSticks.length
        };
        this.verticalSticks.push(newStick);
      }
    },

    getTipMessage() {
      const target = Number(this.question?.targetNumber || 0);
      const current = this.currentNumber;

      if (current === 0) {
        return '小朋友试一下怎么摆放，加油~';
      }
      if (current < target) {
        return '还差一点点～去下面拿木棍放上来吧';
      } else if (current > target) {
        return '哎呀放多啦～点一下木棍就能拿走哦';
      }
      return '刚刚好！点“提交答案”看看吧～';
    },

    submitAnswer() {
      if (!this.question) return;
      if (!this.canSubmit) return;
      const count = this.currentNumber;
      const target = Number(this.question.targetNumber || 0);

      const isCorrectFront = count === target;

      const usedTime = (Date.now() - this.startTime) / 1000.0;
      const stars = this.calculateStars(isCorrectFront, usedTime);

      if (!this.gamerId || !this.question.question_id) {
        if (isCorrectFront) {
          const actualTime = usedTime;
          let encouragement = '';
          if (stars === 3) {
            encouragement = '太厉害了！1分钟内完成，获得3颗星！';
          } else if (stars === 2) {
            encouragement = '不错！1分半内完成，获得2颗星！';
          } else if (stars === 1) {
            encouragement = '很好！2分钟内完成，获得1颗星！';
          } else {
            encouragement = '答对了！继续努力，争取获得更多星星！';
          }

          this.showGamePopup = false;
          this.successMessage = encouragement;
          this.successStarCount = stars;
          this.successTime = Math.round(actualTime);
          this.showSuccessModal = true;
          this.$forceUpdate();
        } else {
          uni.showToast({
            title: '再试试，多放几根木棍～',
            icon: 'none',
          });
        }
        return;
      }

      const body = {
        gamerId: this.gamerId,
        questionId: this.question.question_id,
        levelId: this.routeLevelId || this.levelId,
        trackId: this.trackId,
        userAnswer: JSON.stringify({ answer: String(count) }),
        answerTime: usedTime,
      };

      this.$post('/question_bank/submit_answer', body, (res) => {
        console.log('=== 提交答案返回结果 ===');
        console.log('完整 res:', JSON.stringify(res, null, 2));
        console.log('res.result:', res.result);
        console.log('res.result?.isCorrect:', res.result?.isCorrect);

        let isCorrect = false;
        if (res && res.result) {
          const result = res.result;
          const isCorrectValue = result.isCorrect !== undefined ? result.isCorrect : result.is_correct;

          if (isCorrectValue === true || isCorrectValue === 1 || isCorrectValue === '1' || isCorrectValue === 'true') {
            isCorrect = true;
          } else if (isCorrectValue === false || isCorrectValue === 0 || isCorrectValue === '0' || isCorrectValue === 'false') {
            isCorrect = false;
          } else if (isCorrectValue != null) {
            isCorrect = Boolean(isCorrectValue);
          }
        }

        console.log('最终判断 isCorrect:', isCorrect);
        console.log('提交时的 usedTime:', usedTime);
        console.log('当前 elapsedSeconds:', this.elapsedSeconds);

        if (isCorrect) {
          const actualTime = usedTime;
          console.log('实际用时 actualTime:', actualTime, '秒');
          console.log('elapsedSeconds:', this.elapsedSeconds);

          const starCount = this.calculateStars(true, actualTime);
          console.log('计算出的星级 starCount:', starCount, '（用时:', actualTime, '秒）');

          let encouragement = '';
          if (starCount === 3) {
            encouragement = '太厉害了！1分钟内完成，获得3颗星！';
          } else if (starCount === 2) {
            encouragement = '不错！1分半内完成，获得2颗星！';
          } else if (starCount === 1) {
            encouragement = '很好！2分钟内完成，获得1颗星！';
          } else {
            encouragement = '答对了！继续努力，争取获得更多星星！';
          }

          console.log('显示的鼓励语:', encouragement);
          console.log('星级数量:', starCount);
          console.log('用时:', Math.round(actualTime));

          this.showGamePopup = false;
          this.successMessage = encouragement;
          this.successStarCount = starCount;
          this.successTime = Math.round(actualTime);
          this.showSuccessModal = true;

          console.log('弹窗数据设置完成:', {
            showSuccessModal: this.showSuccessModal,
            successMessage: this.successMessage,
            successStarCount: this.successStarCount,
            successTime: this.successTime
          });

          this.$forceUpdate();
        } else {
          uni.showToast({
            title: '有点小问题，再想一想～',
            icon: 'none',
          });
        }
      });
    },

    calculateStars(isCorrect, usedTime) {
      console.log('calculateStars 调用: isCorrect=', isCorrect, 'usedTime=', usedTime);
      if (!isCorrect) {
        console.log('答错了，返回0颗星');
        return 0;
      }
      if (usedTime <= 60) {
        console.log('用时≤60秒，返回3颗星');
        return 3;
      } else if (usedTime <= 120) {
        console.log('用时≤120秒，返回2颗星');
        return 2;
      }
      console.log('用时>120秒，返回1颗星');
      return 1;
    },

    formatTime(seconds) {
      if (seconds < 60) {
        return `${seconds} 秒`;
      }
      const minutes = Math.floor(seconds / 60);
      const remainingSeconds = seconds % 60;
      if (remainingSeconds === 0) {
        return `${minutes} 分`;
      }
      return `${minutes} 分${remainingSeconds} 秒`;
    },

    closeSuccessModal() {
      this.showSuccessModal = false;
      this.startNextQuestion();
    },

    closeAllCompleteModal() {
      this.showAllCompleteModal = false;
    },

    goToNextLevel() {
      this.showAllCompleteModal = false;
      uni.navigateBack({
        delta: 1
      });
    },

    startNextQuestion() {
      this.hasHorizontalStick = false;
      this.verticalSticks = [];
      this.resetTimer();
      this.showFullSpeech = false;
      this.showGamePopup = false;
      this.showChildSpeech = false;
      this.showChildTip = false;
      this.childSpeechText = '';
      if (this.childTipTimer) {
        clearTimeout(this.childTipTimer);
        this.childTipTimer = null;
      }
      this.fetchQuestion();
    },

    toggleSpeech() {
      this.showFullSpeech = !this.showFullSpeech;
    },

    openGamePopup() {
      this.showGamePopup = true;
      this.showChildTip = false;
      if (this.childTipTimer) {
        clearTimeout(this.childTipTimer);
        this.childTipTimer = null;
      }
    },

    closeGamePopup() {
      this.showGamePopup = false;
    },
  },
  onUnload() {
    if (this.typingTimer) {
      clearInterval(this.typingTimer);
      this.typingTimer = null;
    }
    if (this.elapsedTimer) {
      clearInterval(this.elapsedTimer);
      this.elapsedTimer = null;
    }
    if (this.defaultQuestionTipTimer) {
      clearTimeout(this.defaultQuestionTipTimer);
      this.defaultQuestionTipTimer = null;
    }
    if (this.childTipTimer) {
      clearTimeout(this.childTipTimer);
      this.childTipTimer = null;
    }
  },
};
</script>

<style lang="scss" scoped>
.chouSuan-level-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.background-image {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  z-index: 0;
  object-fit: cover;
}

.default-question-tip {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  padding: 20rpx 30rpx;
  margin: 20rpx 30rpx;
  border-radius: 12rpx;
  box-shadow: 0 4rpx 12rpx rgba(255, 107, 107, 0.3);
  animation: slideDown 0.3s ease;
  position: relative;
  z-index: 10;
}

@keyframes slideDown {
  from {
    transform: translateY(-20rpx);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.tip-text {
  color: #fff;
  font-size: 28rpx;
  font-weight: 500;
  text-align: center;
  display: block;
  line-height: 1.5;
}

.scene {
  position: relative;
  min-height: 450rpx;
  z-index: 1;
}

.elder-area {
  position: fixed;
  left: 20rpx;
  bottom: 200rpx;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.elder-img {
  width: 240rpx;
  height: 320rpx;
  z-index: 2;
}

.speech-bubble {
  margin-bottom: 20rpx;
  margin-left: 10rpx;
  padding: 24rpx 28rpx;
  background: linear-gradient(135deg, #fff59d 0%, #ffeb3b 50%, #ffc107 100%);
  border-radius: 28rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15), 0 2rpx 6rpx rgba(0, 0, 0, 0.1);
  font-size: 26rpx;
  color: #333;
  max-width: 480rpx;
  min-width: 300rpx;
  position: relative;
  z-index: 3;
  cursor: pointer;
  transition: all 0.3s ease;
}

.speech-bubble:not(.expanded) .speech-text {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: break-all;
}

.speech-bubble.expanded .speech-text {
  display: block;
}

.speech-bubble::before {
  content: '';
  position: absolute;
  top: 100%;
  bottom: auto;
  left: 40rpx;
  width: 0;
  height: 0;
  border-left: 16rpx solid transparent;
  border-right: 16rpx solid transparent;
  border-top: 16rpx solid #ffc107;
  filter: drop-shadow(0 2rpx 4rpx rgba(0, 0, 0, 0.15));
}

.speech-text {
  line-height: 1.8;
  font-weight: 500;
}

.child-area {
  position: fixed;
  right: 20rpx;
  bottom: 200rpx;
  z-index: 2;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: flex-end;
  cursor: pointer;
}

.child-img {
  width: 220rpx;
  height: 280rpx;
  z-index: 2;
}

.child-speech-bubble {
  margin-bottom: 20rpx;
  margin-right: 10rpx;
  padding: 20rpx 24rpx;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 50%, #90caf9 100%);
  border-radius: 24rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15), 0 2rpx 6rpx rgba(0, 0, 0, 0.1);
  font-size: 24rpx;
  color: #333;
  max-width: 300rpx;
  position: relative;
  z-index: 3;
  animation: fadeInUp 0.3s ease;
}

.child-speech-bubble::before {
  content: '';
  position: absolute;
  top: 100%;
  bottom: auto;
  right: 40rpx;
  width: 0;
  height: 0;
  border-left: 14rpx solid transparent;
  border-right: 14rpx solid transparent;
  border-top: 14rpx solid #90caf9;
  filter: drop-shadow(0 2rpx 4rpx rgba(0, 0, 0, 0.15));
}

.child-speech-text {
  line-height: 1.6;
  font-weight: 500;
}

.child-tip {
  position: absolute;
  top: -80rpx;
  right: 0;
  padding: 12rpx 20rpx;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  border-radius: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(255, 107, 107, 0.4);
  z-index: 4;
  animation: bounce 1s ease infinite;
}

.child-tip .tip-text {
  color: #fff;
  font-size: 22rpx;
  font-weight: 600;
  white-space: nowrap;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10rpx);
  }
}

.chouSuan-area {
  margin: 0;
  padding: 28rpx 0 16rpx;
  background: transparent;
  border-radius: 0;
  box-shadow: none;
  backdrop-filter: none;
  position: relative;
  z-index: 1;
  border: none;
}

.chouSuan-title {
  font-size: 36rpx;
  color: #2c2c2c;
  margin-bottom: 30rpx;
  padding: 0 40rpx;
  font-weight: 700;
  text-align: center;
  text-shadow: 0 1rpx 3rpx rgba(255, 255, 255, 0.9);
  letter-spacing: 1rpx;
  -webkit-font-smoothing: antialiased;
}

.answer-area {
  display: flex;
  justify-content: center;
  margin: 30rpx 0;
}

.answer-base {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30rpx;
  width: 280rpx;
  min-height: 220rpx;
}

.horizontal-stick-display {
  width: 160rpx;
  height: 28rpx;
  background: #8b4513;
  border-radius: 6rpx;
  margin-bottom: 15rpx;
  box-shadow: 0 3rpx 6rpx rgba(0,0,0,0.25);
  cursor: pointer;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.95);
    opacity: 0.8;
  }
}

.vertical-sticks-container {
  display: flex;
  gap: 16rpx;
  justify-content: center;
  margin-top: 10rpx;
}

.vertical-stick-display {
  width: 28rpx;
  height: 140rpx;
  background: #8b4513;
  border-radius: 6rpx;
  box-shadow: 0 3rpx 6rpx rgba(0,0,0,0.25);
  cursor: pointer;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.95);
    opacity: 0.8;
  }
}

.answer-box {
  width: 500rpx;
  height: 280rpx;
  border: 2rpx solid #ddd;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  transition: all 0.3s ease;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.tip-message {
  margin: 20rpx 0;
  padding: 15rpx 20rpx;
  background: rgba(76, 175, 80, 0.1);
  border: 2rpx solid #4CAF50;
  border-radius: 12rpx;
  font-size: 24rpx;
  color: #333;
  text-align: center;
  line-height: 1.5;
}

.toggle-btn-container {
  display: flex;
  justify-content: space-between;
  margin: 20rpx 40rpx;
  gap: 20rpx;
}

.toggle-btn {
  flex: 1;
  padding: 15rpx 40rpx;
  background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
  color: white;
  border-radius: 25rpx;
  font-size: 26rpx;
  font-weight: 600;
  text-align: center;
  box-shadow: 0 6rpx 16rpx rgba(76, 175, 80, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
  line-height: 1.5;
}

.toggle-btn:active {
  transform: scale(0.95);
}

.clear-btn {
  flex: 1;
  padding: 15rpx 40rpx;
  background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
  color: white;
  border-radius: 25rpx;
  font-size: 26rpx;
  font-weight: 600;
  text-align: center;
  box-shadow: 0 6rpx 16rpx rgba(244, 67, 54, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
  line-height: 1.5;
}

.clear-btn:active {
  transform: scale(0.95);
}

.resources-container {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
  padding: 0;
}

.resource-area {
  flex: 1;
  margin: 20rpx 0;
  padding: 20rpx;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: center;
  align-items: center;
}

.stick-card {
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  user-select: none;
}

.stick-card:active {
  transform: scale(0.95);
}

.stick-label {
  font-size: 24rpx;
  color: #666;
  font-weight: 500;
}

.stick-tip {
  margin: 10rpx 0;
  text-align: center;
}

.stick-tip-text {
  font-size: 24rpx;
  color: #666;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20rpx;
  margin: 30rpx 0;
}

.action-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
  padding: 24rpx 20rpx;
  border-radius: 16rpx;
  cursor: pointer;
  transition: all 0.3s;
  background: linear-gradient(135deg, #fff9e6 0%, #fff3cd 100%);
  border: 3rpx solid #ffc107;
  box-shadow: 0 4rpx 12rpx rgba(255, 193, 7, 0.2);
}

.action-btn:active {
  transform: scale(0.95);
  box-shadow: 0 2rpx 6rpx rgba(255, 193, 7, 0.3);
}

.btn-text {
  font-size: 26rpx;
  color: #333;
  font-weight: 600;
}

.stick-preview {
  background: #8b4513;
  border-radius: 6rpx;
  box-shadow: 0 3rpx 6rpx rgba(0,0,0,0.25);
}

.horizontal-stick-preview {
  width: 120rpx;
  height: 24rpx;
}

.vertical-stick-preview {
  width: 24rpx;
  height: 100rpx;
}

.stick-label {
  font-size: 30rpx;
  color: #333;
  font-weight: 600;
}

.stick-hint {
  font-size: 22rpx;
  color: #888;
  font-weight: 400;
}

.rule-tip {
  text-align: center;
  margin-bottom: 20rpx;
  padding: 0 20rpx;
}

.rule-tip text {
  font-size: 24rpx;
  color: #666;
  background: #f8f8f8;
  padding: 12rpx 20rpx;
  border-radius: 20rpx;
}

.reset-btn {
  display: flex;
  justify-content: center;
  margin: 15rpx 0 25rpx 0;
}

.reset-btn text {
  padding: 15rpx 40rpx;
  background: #f0f0f0;
  border-radius: 25rpx;
  font-size: 28rpx;
  color: #555;
  cursor: pointer;
  font-weight: 500;
}

.chouSuan-info {
  margin-top: 10rpx;
  padding: 0 40rpx;
  font-size: 26rpx;
  color: #2c2c2c;
  font-weight: 600;
  text-shadow: 0 1rpx 3rpx rgba(255, 255, 255, 0.8);
  -webkit-font-smoothing: antialiased;
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.number-info {
  text-align: center;
  font-size: 30rpx;
  color: #333;
  background: rgba(255, 255, 255, 0.8);
  padding: 12rpx 24rpx;
  border-radius: 16rpx;
  display: inline-block;
  align-self: center;
}

.chouSuan-info .time-text {
  text-align: center;
  font-size: 24rpx;
  color: #666;
  margin-top: 5rpx;
}

.bottom-bar {
  margin: 20rpx 0 0;
  display: flex;
  justify-content: space-between;
  position: relative;
  z-index: 1;
}

.btn-reset {
  flex: 1;
  margin: 0 10rpx;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  background: #f5f5f5;
  color: #666;
}

.btn-submit {
  flex: 1;
  margin: 0;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: none;
  background: linear-gradient(135deg, #ff8c42 0%, #ff6b35 50%, #ff5722 100%);
  color: #fff;
  box-shadow: 0 6rpx 20rpx rgba(255, 107, 53, 0.4);
  transition: all 0.3s ease;
  cursor: pointer;
}

.btn-submit:not(.btn-disabled):hover {
  transform: scale(1.05);
  box-shadow: 0 8rpx 24rpx rgba(255, 107, 53, 0.5);
}

.btn-submit:not(.btn-disabled):active {
  transform: scale(0.98);
  box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.3);
}

.btn-submit.btn-disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: scale(1);
  box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.2);
}

.btn-submit.btn-disabled:hover {
  transform: scale(1);
  box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.2);
}

.success-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
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
  width: 640rpx;
  max-width: 90%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 40rpx;
  padding: 80rpx 50rpx 60rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.3);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(100rpx);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-icon {
  width: 140rpx;
  height: 140rpx;
  background: #fff;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 100rpx;
  color: #4caf50;
  font-weight: bold;
  margin-bottom: 40rpx;
  box-shadow: 0 8rpx 24rpx rgba(76, 175, 80, 0.4);
  line-height: 1;
}

.modal-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #fff;
  text-align: center;
  line-height: 1.8;
  margin-bottom: 50rpx;
  text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.3);
  padding: 0 20rpx;
  word-break: break-all;
}

.modal-stars {
  display: flex;
  gap: 30rpx;
  margin-bottom: 40rpx;
  align-items: center;
  justify-content: center;
}

.star {
  font-size: 60rpx;
  opacity: 0.25;
  transition: all 0.4s ease;
  color: #ffd700;
  filter: grayscale(0.8);
}

.star-active {
  opacity: 1;
  transform: scale(1.3);
  color: #ffd700;
  filter: drop-shadow(0 6rpx 12rpx rgba(255, 215, 0, 0.8)) grayscale(0);
  animation: starPulse 0.6s ease;
}

@keyframes starPulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.4);
  }
  100% {
    transform: scale(1.3);
  }
}

.modal-time {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.95);
  margin-bottom: 50rpx;
  font-weight: 500;
}

.modal-btn {
  width: 100%;
  height: 88rpx;
  background: #fff;
  color: #667eea;
  border-radius: 44rpx;
  font-size: 32rpx;
  font-weight: 600;
  border: none;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.25);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-btn:active {
  transform: scale(0.95);
  box-shadow: 0 4rpx 10rpx rgba(0, 0, 0, 0.2);
}

.all-complete-content {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.all-complete-content .modal-icon {
  font-size: 120rpx;
  color: #ffd700;
}

.all-complete-content .modal-desc {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.95);
  margin-bottom: 30rpx;
  font-weight: 500;
}

.all-complete-content .modal-tip {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  text-align: center;
  font-weight: 400;
}

.popup-mask {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  z-index: 998;
}

.game-popup {
  position: fixed;
  left: 30rpx;
  right: 30rpx;
  top: 30rpx;
  bottom: 30rpx;
  z-index: 999;

  padding: 30rpx;
  border-radius: 28rpx;

  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(15rpx);
  -webkit-backdrop-filter: blur(15rpx);

  border: 2rpx solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.2);

  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  overflow: hidden;
}
</style>
