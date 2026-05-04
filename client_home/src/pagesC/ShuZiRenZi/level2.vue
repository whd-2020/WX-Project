<template>
  <view class="rope-level-page">

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
      <!-- 顶部题目区 -->
      <view class="rope-area">
          <view class="question-content">
            <text class="question-content-text" :class="{ 'question-content-text--wrap': wrapTitleByComma }">{{ displayQuestionText }}</text>
            <div v-if="currentQuestion.question_content && currentQuestion.question_content.decorations" class="question-image">
              <span 
                v-for="(decoration, index) in (currentQuestion.question_type === 'drag_item' ? [currentQuestion.question_content.decorations[0]] : Array(displayIconCount).fill(currentQuestion.question_content.decorations[0]))" 
                :key="index"
                class="item-icon"
                :class="decoration.type"
              >
                {{ decoration.icon }}
              </span>
            </div>
          </view>
          <text class="question-content-measure">{{ rawQuestionText }}</text>

        <!-- 中间答题区 -->
        <view class="answer-area">
          <view ref="answerBox" class="answer-box">
            <text v-if="droppedItems.length === 0" class="placeholder">把答案拖到这里</text>
            <view v-else class="dropped-items">
              <text
                v-for="(item, index) in droppedItems"
                :key="index"
                class="dropped-item"
                @click="removeDroppedItem(index)"
              >
                {{ item }}
              </text>
            </view>
            <!-- 计数器 -->
            <view class="counter">
              {{ droppedItems.length }}/12
            </view>
          </view>
        </view>

        <!-- 提示信息 -->
        <view v-if="currentQuestion.question_type === 'drag_item'" class="tip-message">
          {{ getTipMessage() }}
        </view>

        <!-- 添加/移除切换按钮和清空按钮 -->
        <view v-if="currentQuestion.question_type === 'drag_item'" class="toggle-btn-container">
          <view class="toggle-btn" :class="{ 'toggle-btn-large': needToggleMode }" @click="toggleMode">
            {{ isAddMode ? '添加' : '移除' }}
          </view>
          <view class="clear-btn" @click="clearDroppedItems">
            清空
          </view>
        </view>

        <!-- 底部资源区 -->
        <view class="resource-area">

          <view
            v-if="currentQuestion.question_type === 'drag_number'"
            class="number-resources"
          >
            <view
              v-for="(num, index) in ['1', '2', '3', '4', '5', '6', '7', '8', '9']"
              :key="num"
              class="number-card"
              @click="handleNumberDrop(num)"
            >
              {{ num }}
            </view>
          </view>
          <view
            v-else-if="currentQuestion.question_type === 'drag_item'"
            class="item-resources"
          >
            <view
              v-for="(item, index) in items"
              :key="index"
              class="item-card"
              @click="toggleItem(item.icon)"
            >
              {{ item.icon }}
            </view>
          </view>
        </view>
      </view>

      <!-- 底部操作区 -->
      <view class="bottom-bar">
        <button
          class="btn-submit"
          :class="{ 'btn-disabled': !isAnswerCorrect }"
          type="primary"
          @click="submitAnswer"
          :disabled="!isAnswerCorrect"
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
        <view class="modal-time">用时：{{ formatTime(elapsedSeconds) }}</view>
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
  name: 'DigitalCogLevel2',
  data() {
    return {
      gamerId: null,
      trackId: 4,
      levelId: 2,
      routeLevelId: null,
      question: null,
      currentQuestionIndex: 0,
      droppedItems: [],
      showGamePopup: false,
      fullText: '今日族长正在思考要出什么题目给你……',
      displayText: '',
      typingTimer: null,
      showFullSpeech: false,
      showChildSpeech: false,
      childSpeechText: '',
      childSpeechTimer: null,
      showChildTip: false,
      childTipTimer: null,
      showSuccessModal: false,
      successMessage: '',
      successStarCount: 0,
      successTime: 0,
      showAllCompleteModal: false,
      showDefaultQuestionTip: false,
      defaultQuestionTipTimer: null,
      startTime: 0,
      elapsedSeconds: 0,
      elapsedTimer: null,
      touchData: null,
      draggingItem: null,
      dragOffset: { x: 0, y: 0 },
      dragPosition: { x: 0, y: 0 },
      originalPosition: { x: 0, y: 0 },
      returningItem: null,
      absorbedItem: null,
      isDragging: false,
      availableNumbers: [],
      numbers: ['1', '2', '3', '4', '5', '6', '7', '8', '9'],
      isAddMode: true,
      wrapTitleByComma: false,
    }
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

    if (!this.numbers || this.numbers.length === 0) {
      this.numbers = ['1', '2', '3', '4', '5', '6', '7', '8', '9'];
    }
    this.resetTimer();
    this.initAvailableNumbers();
    this.fetchQuestion();
  },
  onShow() {
    if (!this.numbers || this.numbers.length === 0) {
      this.numbers = ['1', '2', '3', '4', '5', '6', '7', '8', '9'];
    }
    this.initAvailableNumbers();
  },
  onReady() {
    if (!this.numbers || this.numbers.length === 0) {
      this.numbers = ['1', '2', '3', '4', '5', '6', '7', '8', '9'];
    }
    this.initAvailableNumbers();
  },
  computed: {
    currentQuestion() {
      return this.question || {}
    },
    isAnswerCorrect() {
      if (this.droppedItems.length === 0) return false

      if (this.currentQuestion.question_type === 'drag_number' && this.currentQuestion.correct_answer) {
        return this.droppedItems.join('') === this.currentQuestion.correct_answer.answer
      } else if (this.currentQuestion.question_type === 'drag_item' && this.currentQuestion.correct_answer) {
        return this.droppedItems.length === parseInt(this.currentQuestion.correct_answer.answer)
      }
      return false
    },
    numbersLength() {
      return this.numbers && this.numbers.length || 0
    },
    availableNumbersLength() {
      return this.availableNumbers && this.availableNumbers.length || 0
    },
    items() {
      if (this.question && this.question.question_content && this.question.question_content.decorations) {
        return this.question.question_content.decorations;
      }
      return [];
    },
    displayIconCount() {
      return parseInt(this.currentQuestion.correct_answer?.answer || 1);
    },
    needToggleMode() {
      const maxCount = parseInt(this.currentQuestion.correct_answer?.answer || 0);
      const currentCount = this.droppedItems.length;

      if (this.isAddMode) {
        return currentCount > maxCount;
      } else {
        return currentCount < maxCount;
      }
    },
    rawQuestionText() {
      return String(this.currentQuestion && this.currentQuestion.question_content && this.currentQuestion.question_content.options && this.currentQuestion.question_content.options[0] || '');
    },
    displayQuestionText() {
      if (!this.wrapTitleByComma) return this.rawQuestionText;
      return String(this.rawQuestionText).replace(/([，,])\s*/g, '$1\n');
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

    toggleSpeech() {
      this.showFullSpeech = !this.showFullSpeech;
    },

    openGamePopup() {
      this.initAvailableNumbers();
      this.showGamePopup = true;
      this.showChildTip = false;
      if (this.childTipTimer) {
        clearTimeout(this.childTipTimer);
        this.childTipTimer = null;
      }
      this.$nextTick(() => {
        this.updateTitleWrap();
      });
    },

    closeGamePopup() {
      this.showGamePopup = false;
    },

    closeSuccessModal() {
      this.showSuccessModal = false;
      this.startNextQuestion();
    },

    startNextQuestion() {
      this.droppedItems = [];
      this.initAvailableNumbers();
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

    completeLevel() {
      if (!this.gamerId || !this.levelId || !this.trackId) return;

      const totalTime = (Date.now() - this.startTime) / 1000.0;

      this.$post('/question_bank/complete_level', {
        gamerId: this.gamerId,
        levelId: this.routeLevelId || this.levelId,
        trackId: this.trackId,
        totalTime: totalTime,
        correctCount: 10
      }, (res) => {
        console.log('=== 完成关卡 ===');
        console.log('res:', res);
        if (res && res.result) {
          const result = res.result;
          console.log('stars:', result.stars);
          console.log('totalStars:', result.totalStars);
          console.log('bestTime:', result.bestTime);
          console.log('bestStars:', result.bestStars);
          console.log('completeCount:', result.completeCount);
          console.log('feedback:', result.feedback);
        }
      }, (err) => {
        console.error('完成关卡失败:', err);
      });
    },

    submitAnswer() {
      if (!this.question) return;

      let isCorrectFront = false;
      if (this.question.question_type === 'drag_number' && this.question.correct_answer) {
        const userAnswer = this.droppedItems.join('');
        const correctAnswer = this.question.correct_answer?.answer || '';
        isCorrectFront = userAnswer === correctAnswer;
      } else if (this.question.question_type === 'drag_item' && this.question.correct_answer) {
        isCorrectFront = this.droppedItems.length === parseInt(this.question.correct_answer.answer);
      }

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
          let errorMessage = '再试试，数字不对哦～';
          if (this.question.question_type === 'drag_item') {
            errorMessage = '再试试，数量不对哦～';
          }
          uni.showToast({
            title: errorMessage,
            icon: 'none',
          });
        }
        return;
      }

      let userAnswer = '';
      if (this.question.question_type === 'drag_number') {
        userAnswer = this.droppedItems.join('');
      } else if (this.question.question_type === 'drag_item') {
        userAnswer = this.droppedItems.length.toString();
      }
      const body = {
        gamerId: this.gamerId,
        questionId: this.question.question_id,
        levelId: this.routeLevelId || this.levelId,
        trackId: this.trackId,
        userAnswer: JSON.stringify({ answer: userAnswer }),
        answerTime: usedTime,
      };

      this.$post('/question_bank/submit_answer', body, (res) => {
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

        if (isCorrect) {
          const actualTime = usedTime;
          const starCount = this.calculateStars(true, actualTime);

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

          this.showGamePopup = false;
          this.successMessage = encouragement;
          this.successStarCount = starCount;
          this.successTime = Math.round(actualTime);
          this.showSuccessModal = true;
          // this.completeLevel();
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

    initAvailableNumbers() {
      if (!this.numbers || this.numbers.length === 0) {
        this.numbers = ['1', '2', '3', '4', '5', '6', '7', '8', '9'];
      }
      this.availableNumbers = this.numbers.map(num => ({
        num: num,
        originalIndex: this.numbers.indexOf(num)
      }));
    },

    fetchQuestion() {
      const gamerId = Number(this.gamerId);
      const levelId = Number(this.routeLevelId || this.levelId);
      const trackId = Number(this.trackId);

      console.log('=== 获取题目 ===');
      console.log('trackId:', trackId);
      console.log('levelId:', levelId);
      console.log('gamerId:', gamerId);

      if (!gamerId || Number.isNaN(levelId) || levelId <= 0 || Number.isNaN(trackId) || trackId <= 0) {
        const local = {
          question_id: 0,
          question_type: 'drag_number',
          question_title: '新手入门',
          question_content: {
            options: ['把数字拖到框里：1只小鸡'],
            decorations: [{
              type: 'XiaoJi',
              name: '小鸡',
              icon: '🐥'
            }]
          },
          correct_answer: { answer: '1' }
        };
        this.question = local;
        this.wrapTitleByComma = false;
        this.playTyping(this.question.question_content.options[0]);
        return;
      }
      const params = {
        gamerId,
        levelId,
        trackId,
      };
      this.$get('/question_bank/get_level_questions', params, (json) => {
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

        if (allThreeStars) {
          this.showAllCompleteModal = true;
          setTimeout(() => {
            this.showAllCompleteModal = false;
            uni.redirectTo({
              url: '/pages/track/comprehensive',
            });
          }, 3000);
          return;
        }

        if (!json.result || !json.result.questions || json.result.questions.length === 0) {
          const local = {
            question_id: 0,
            question_type: 'drag_number',
            question_title: '新手入门',
            question_content: {
              options: ['把数字拖到框里：1只小鸡'],
              decorations: [{
                type: 'XiaoJi',
                name: '小鸡',
                icon: '🐥'
              }]
            },
            correct_answer: { answer: '1' }
          };
          this.question = local;
          this.wrapTitleByComma = false;
          this.showDefaultQuestionTip = true;

          if (this.defaultQuestionTipTimer) {
            clearTimeout(this.defaultQuestionTipTimer);
          }
          this.defaultQuestionTipTimer = setTimeout(() => {
            this.showDefaultQuestionTip = false;
            this.defaultQuestionTipTimer = null;
          }, 2000);

          this.playTyping(local.question_content.options[0]);
          return;
        }

        this.showDefaultQuestionTip = false;
        if (this.defaultQuestionTipTimer) {
          clearTimeout(this.defaultQuestionTipTimer);
          this.defaultQuestionTipTimer = null;
        }
        const list = json.result.questions;
        const q = list[Math.floor(Math.random() * list.length)];

        try {
          if (q.question_content) {
            const content = JSON.parse(q.question_content);
            q.question_content = content;
          }
          if (q.correct_answer) {
            q.correct_answer = typeof q.correct_answer === 'string' ? JSON.parse(q.correct_answer) : q.correct_answer;
          }
        } catch (e) {
        }

        this.question = q;
        this.wrapTitleByComma = false;
        this.playTyping(this.question.question_content.options[0]);
        if (this.showGamePopup) {
          this.$nextTick(() => {
            this.updateTitleWrap();
          });
        }
      });
    },

    updateTitleWrap() {
      if (!this.showGamePopup) return;
      const title = String(this.rawQuestionText || '');
      if (!/[，,]/.test(title)) {
        this.wrapTitleByComma = false;
        return;
      }
      const sys = uni.getSystemInfoSync ? uni.getSystemInfoSync() : null;
      const windowWidth = sys && sys.windowWidth ? sys.windowWidth : 375;
      const paddingPx = (40 * 2 * windowWidth) / 750;
      const query = uni.createSelectorQuery().in(this);
      query.select('.question-content').boundingClientRect();
      query.select('.question-content-measure').boundingClientRect();
      query.exec((res) => {
        const titleRect = res && res[0] ? res[0] : null;
        const measureRect = res && res[1] ? res[1] : null;
        if (!titleRect || !measureRect) return;
        const available = Math.max(0, (titleRect.width || 0) - paddingPx);
        this.wrapTitleByComma = (measureRect.width || 0) > available;
      });
    },

    handleNumberDrop(num) {
      this.droppedItems = [num];
      this.availableNumbers = this.availableNumbers.filter(item => item.num !== num);
    },

    toggleMode() {
      this.isAddMode = !this.isAddMode;
    },

    toggleItem(icon) {
      if (this.isAddMode) {
        if (this.droppedItems.length < 12) {
          this.droppedItems.push(icon);
        }
      } else {
      }
    },

    removeDroppedItem(index) {
      this.droppedItems.splice(index, 1);
    },

    clearDroppedItems() {
      this.droppedItems = [];
    },

    getTipMessage() {
      const maxCount = parseInt(this.currentQuestion.correct_answer?.answer || 0);
      const currentCount = this.droppedItems.length;

      if (this.isAddMode) {
        if (currentCount < maxCount) {
          return '请添加物品';
        } else if (currentCount > maxCount) {
          return '物品数量超过了，点击切换到移除模式';
        } else {
          return '数量正确，点击提交试试';
        }
      } else {
        if (currentCount < maxCount) {
          return '物品数量不够，点击切换到添加模式';
        } else if (currentCount > maxCount) {
          return '请移除多余的物品';
        } else {
          return '数量正确，点击提交试试';
        }
      }
    },

    goBack() {
      uni.navigateBack();
    }
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
  }
}
</script>


<style lang="scss" scoped>
.rope-level-page {
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
  animation: fadeIn 0.3s ease;
}

.toggle-btn-container {
  display: flex;
  justify-content: space-between;
  margin: 20rpx 40rpx;
}

.toggle-btn {
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
  min-width: 180rpx;
}

.toggle-btn-large {
  transform: scale(1.1);
  animation: pulse 1.5s infinite;
  box-shadow: 0 8rpx 24rpx rgba(76, 175, 80, 0.5);
}

@keyframes pulse {
  0% {
    transform: scale(1.1);
  }
  50% {
    transform: scale(1.15);
  }
  100% {
    transform: scale(1.1);
  }
}

.clear-btn {
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
  min-width: 180rpx;
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
  max-width: 420rpx;
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
  animation: hintBounce 1.2s ease-in-out infinite;
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

@keyframes hintBounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8rpx);
  }
}

.rope-area {
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

.rope-title {
  font-size: 30rpx;
  color: #2c2c2c;
  margin-bottom: 20rpx;
  padding: 0 40rpx;
  font-weight: 700;
  text-align: center;
  text-shadow: 0 1rpx 3rpx rgba(255, 255, 255, 0.9);
  letter-spacing: 1rpx;
  -webkit-font-smoothing: antialiased;
}

.question-content {
  font-size: 28rpx;
  color: #2c2c2c;
  margin-bottom: 20rpx;
  padding: 0 40rpx;
  text-align: center;
  text-shadow: 0 1rpx 3rpx rgba(255, 255, 255, 0.8);
  line-height: 1.5;
  -webkit-font-smoothing: antialiased;
}

.question-content-text {
  display: block;
}

.question-content-text--wrap {
  white-space: pre-line;
}

.question-content-measure {
  position: fixed;
  left: -9999px;
  top: -9999px;
  font-size: 28rpx;
  line-height: 1.5;
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
}

.question-image {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 20rpx 0;
  width: 100%;
  max-width: 100%;
  overflow: hidden;
  flex-wrap: wrap;
}

.item-icon {
  font-size: 48rpx;
  margin: 0 8rpx;
  animation: bounce 2s infinite;
}

.answer-area {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  margin: 30rpx 0;
}

.answer-box {
  width: 500rpx;
  height: 250rpx;
  border: 4rpx dashed #999;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  backdrop-filter: blur(5rpx);
  position: relative;
}

.answer-box:hover {
  border-color: #4CAF50;
  box-shadow: 0 0 20rpx rgba(76, 175, 80, 0.3);
}

.counter {
  position: absolute;
  bottom: 10rpx;
  right: 10rpx;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 5rpx 10rpx;
  border-radius: 15rpx;
  font-size: 20rpx;
  font-weight: bold;
  z-index: 10;
}

.placeholder {
  color: #999;
  font-size: 28rpx;
  text-shadow: 0 1rpx 3rpx rgba(255, 255, 255, 0.9);
}

.dropped-items {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
}

.dropped-item {
  font-size: 64rpx;
  margin: 0 10rpx;
  animation: bounce 2s infinite;
  cursor: pointer;
  transition: all 0.3s ease;
}

.dropped-item:hover {
  transform: scale(1.1);
}

.resource-area {
  width: 100%;
  margin: 30rpx 0;
  padding: 20rpx;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(5rpx);
  min-height: 200rpx;
  display: block;
}

.number-resources {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.item-resources {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}

.number-card {
  width: 100rpx;
  height: 100rpx;
  background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16rpx;
  font-size: 40rpx;
  font-weight: bold;
  margin: 10rpx;
  cursor: grab;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 4rpx 12rpx rgba(76, 175, 80, 0.3);
  user-select: none;
  touch-action: none;
  position: relative;
  overflow: hidden;
}

.number-card:active {
  cursor: grabbing;
  transform: scale(0.95) rotate(-2deg);
  box-shadow: 0 8rpx 20rpx rgba(76, 175, 80, 0.5);
  background: linear-gradient(135deg, #45a049 0%, #3d8b40 100%);
  animation: pulse 0.3s ease;
}

.number-card:hover {
  transform: scale(1.1) rotate(2deg);
  box-shadow: 0 8rpx 20rpx rgba(76, 175, 80, 0.5);
  background: linear-gradient(135deg, #4CAF50 0%, #388E3C 100%);
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(0.95);
  }
}

.item-card {
  font-size: 64rpx;
  margin: 0 20rpx;
  cursor: grab;
  transition: all 0.2s ease;
  animation: bounce 2s infinite;
  user-select: none;
}

.item-card:hover {
  transform: scale(1.2);
}

.bottom-bar {
  margin: 20rpx 0 0;
  display: flex;
  justify-content: space-between;
  position: relative;
  z-index: 1;
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
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 999;
  width: 600rpx;
  max-height: 90vh;
  min-height: 600rpx;
  padding: 40rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10rpx);
  -webkit-backdrop-filter: blur(10rpx);
  border: 1rpx solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
  overflow-y: auto;
}

.back-arrow {
  position: absolute;
  top: 30rpx;
  left: 30rpx;
  width: 60rpx;
  height: 60rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.2);
  z-index: 100;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-arrow:hover {
  transform: scale(1.1);
  box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.3);
}

.back-arrow::before {
  content: '';
  position: absolute;
  left: 22rpx;
  top: 20rpx;
  width: 20rpx;
  height: 20rpx;
  border-left: 4rpx solid #333;
  border-top: 4rpx solid #333;
  transform: rotate(-45deg);
}
</style>
