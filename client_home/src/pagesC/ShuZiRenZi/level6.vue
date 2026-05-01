<template>
  <view class="digit-level-page">

    <image class="background-image" src="/static/img/rope/CaoYuanBeiJing.png" mode="aspectFill"></image>

    <view class="default-question-tip" v-if="showDefaultQuestionTip">
      <text class="tip-text">⚠️ 当前为默认题目，请联系运维人员</text>
    </view>

    <view class="scene">
      <view class="elder-area">
        <view class="speech-bubble" :class="{ 'expanded': showFullSpeech }" @click="toggleSpeech">
          <text class="speech-text">{{ displayText }}</text>
        </view>
        <image class="elder-img" src="/static/img/rope/LaoRen.png" mode="aspectFit"></image>
      </view>

      <view class="child-area" @click="openGamePopup">
        <view class="child-speech-bubble" v-if="showChildSpeech">
          <text class="child-speech-text">{{ childSpeechText }}</text>
        </view>
        <view class="child-tip" v-if="showChildTip">
          <text class="tip-text">点击这里，来试试吧</text>
        </view>
        <image class="child-img" src="/static/img/rope/XiaoHai.png" mode="aspectFit"></image>
      </view>
    </view>

    <view class="popup-mask" v-if="showGamePopup" @click="closeGamePopup"></view>
    <view class="game-popup" v-if="showGamePopup" @click.stop>
      <view class="game-area">
        <view class="question-content">
          {{ currentQuestion.question_content && currentQuestion.question_content.options && currentQuestion.question_content.options[0] || '' }}
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

        <view class="answer-area">
          <view class="answer-boxes">
            <view class="answer-box left-box" v-if="currentQuestion.question_type === 'drag_item'">
              <view class="box-label">左边=10</view>
              <view v-if="!droppedItems.left.length" class="placeholder">点击添加</view>
              <view v-else class="dropped-items">
                <span 
                  v-for="(item, index) in droppedItems.left" 
                  :key="index"
                  class="dropped-item bubble-dropped-item"
                  @click.stop="removeDroppedItem('left', index)"
                >
                  {{ item }}
                </span>
              </view>
              <view class="counter">数量: {{ droppedItems.left.length }}</view>
            </view>

            <view class="answer-box right-box" v-if="currentQuestion.question_type === 'drag_item'">
              <view class="box-label">右边=1</view>
              <view v-if="!droppedItems.right.length" class="placeholder">点击添加</view>
              <view v-else class="dropped-items">
                <span 
                  v-for="(item, index) in droppedItems.right" 
                  :key="index"
                  class="dropped-item"
                  @click.stop="removeDroppedItem('right', index)"
                >
                  {{ item }}
                </span>
              </view>
              <view class="counter">数量: {{ droppedItems.right.length }}</view>
            </view>

            <view class="total-counter" v-if="currentQuestion.question_type === 'drag_item'">
              总数: {{ droppedItems.left.length * 10 + droppedItems.right.length }}
            </view>

            <view class="answer-box number-answer-box" v-if="currentQuestion.question_type === 'drag_number'">
              <view class="box-label">选择对应数字</view>
              <view v-if="!droppedItems.left.length && !droppedItems.right.length" class="placeholder">点击选择数字</view>
              <view v-else class="dropped-items number-dropped-items">
                <span 
                  v-for="(item, index) in droppedItems.left" 
                  :key="'left-' + index"
                  class="dropped-item number-item"
                  @click.stop="removeDroppedItem('left', index)"
                >
                  {{ item }}
                </span>
                <span 
                  v-for="(item, index) in droppedItems.right" 
                  :key="'right-' + index"
                  class="dropped-item number-item"
                  @click.stop="removeDroppedItem('right', index)"
                >
                  {{ item }}
                </span>
              </view>
            </view>
          </view>
        </view>

        <view v-if="currentQuestion.question_type === 'drag_item'" class="tip-message">
          {{ getTipMessage() }}
        </view>

        <view v-if="currentQuestion.question_type === 'drag_number'" class="tip-message">
          {{ getNumberTipMessage() }}
        </view>

        <view v-if="currentQuestion.question_type === 'drag_item'" class="toggle-btn-container">
          <view class="toggle-btn" :class="{ 'toggle-btn-large': needToggleMode }" @click="toggleMode">
            {{ isAddMode ? '添加' : '移除' }}
          </view>
          <view class="btn-spacing"></view>
          <view class="clear-btn" @click="clearDroppedItems">
            清空
          </view>
        </view>

        <view class="resource-area">
          <view 
            v-if="currentQuestion.question_type === 'drag_number'" 
            class="number-resources"
          >
            <view 
              v-for="num in numbers" 
              :key="num"
              class="number-card"
              :class="{ 'number-card-smaller': selectedNumber && isNumberNeedScale(num) }"
              @click="handleNumberClick(num)"
            >
              {{ num }}
            </view>
          </view>
          <view 
            v-else-if="currentQuestion.question_type === 'drag_item'" 
            class="item-resources"
          >
            <view 
              class="item-card bubble-item"
              @click="toggleItem(currentIcon, 'left')"
            >
              <div class="bubble triangle-bubble">
                <div class="triangle-row">
                  <span class="bubble-icon">{{ currentIcon }}</span>
                </div>
                <div class="triangle-row">
                  <span class="bubble-icon">{{ currentIcon }}</span>
                  <span class="bubble-icon">{{ currentIcon }}</span>
                </div>
              </div>
            </view>
            <view 
              class="item-card single-item"
              @click="toggleItem(currentIcon, 'right')"
            >
              {{ currentIcon }}
            </view>
          </view>
        </view>

        <view class="bottom-bar">
          <button 
            class="btn-submit" 
            :class="{ 'btn-disabled': !isAnswerCorrect }"
            type="primary" 
            @click="submitAnswer"
            :disabled="!isAnswerCorrect"
          >
            提交
          </button>
        </view>
      </view>
    </view>

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
  name: 'DigitalCogLevel6',
  data() {
    return {
      gamerId: null,
      trackId: 6,
      levelId: 6,
      routeLevelId: null,
      question: null,
      currentQuestionIndex: 0,
      droppedItems: {
        left: [],
        right: []
      },
      selectedNumber: null,
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
      numbers: [],
      itemsPool: [
        { type: 'XiaoJi', name: '小鸡', icon: '🐥' },
        { type: 'YuGu', name: '鱼', icon: '🐟' },
        { type: 'GuoShi', name: '果实', icon: '🍎' }
      ],
      isAddMode: true,
      hasAnswered: false
    };
  },
  computed: {
    currentQuestion() {
      return this.question || {};
    },
    isAnswerCorrect() {
      if (!this.question) return false;
      if (this.question.question_type === 'drag_number') {
        const userAnswer = this.droppedItems.right[0] || '';
        const correctAnswer = this.question.correct_answer || {};
        return userAnswer === correctAnswer.answer;
      } else if (this.question.question_type === 'drag_item') {
        const correctAnswer = this.question.correct_answer || {};
        const userTotal = this.droppedItems.left.length * 10 + this.droppedItems.right.length;
        const correctTotal = parseInt(correctAnswer.answer || '0');
        
        
        return userTotal === correctTotal;
      }
      return false;
    },
    displayIconCount() {
      const correctAnswer = this.currentQuestion.correct_answer || {};
      return parseInt(correctAnswer.answer || 1);
    },
    items() {
      if (this.question && this.question.question_content && this.question.question_content.decorations) {
        return this.question.question_content.decorations;
      }
      return [];
    },
    needToggleMode() {
      const correctAnswer = this.currentQuestion.correct_answer || {};
      const currentLeft = this.droppedItems.left.length;
      const currentRight = this.droppedItems.right.length;
      const userTotal = currentLeft * 10 + currentRight;
      const correctTotal = parseInt(correctAnswer.answer || '0');

      if (this.isAddMode) {
        return userTotal > correctTotal;
      } else {
        return userTotal < correctTotal;
      }
    },
    currentIcon() {
      if (this.question && this.question.question_content && this.question.question_content.decorations && this.question.question_content.decorations[0]) {
        return this.question.question_content.decorations[0].icon;
      }
      return '🐥'; // 默认图标，以防数据缺失
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
    this.resetTimer();
    this.fetchQuestion();
  },
  onShow() {
    if (this.question) {
      this.generateRandomNumbers();
    }
  },
  onReady() {
    if (this.question) {
      this.generateRandomNumbers();
    }
  },
  methods: {
    handleNumberClick(num) {
      if (this.currentQuestion.question_type === 'drag_number') {
        this.droppedItems = { left: [], right: [] };
        this.droppedItems.right = [num];
        this.selectedNumber = num;
        this.hasAnswered = true;
      }
    },
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
      this.generateRandomNumbers();
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
    closeSuccessModal() {
      this.showSuccessModal = false;
      this.startNextQuestion();
    },
    startNextQuestion() {
      this.droppedItems = { left: [], right: [] };
      this.resetTimer();
      this.showFullSpeech = false;
      this.showGamePopup = false;
      this.showChildSpeech = false;
      this.showChildTip = false;
      this.childSpeechText = '';
      this.selectedNumber = null;
      this.hasAnswered = false;
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
        if (res && res.result) {
          const result = res.result;
        }
      }, (err) => {
      });
    },

    submitAnswer() {
      if (!this.question) return;

      if (this.isAnswerCorrect) {
        const usedTime = (Date.now() - this.startTime) / 1000.0;
        const stars = this.calculateStars(true, usedTime);



        if (!this.gamerId || !this.question.question_id) {
          this.showSuccess('太棒了！', stars, usedTime);
          return;
        }

        let userAnswer = '';
        if (this.question.question_type === 'drag_number') {
          const userAnswerNum = this.droppedItems.right[0] || '';
          userAnswer = JSON.stringify({ 
            answer: userAnswerNum,
            left_digit: '',
            right_digit: userAnswerNum
          });
        } else if (this.question.question_type === 'drag_item') {
          userAnswer = JSON.stringify({ 
            answer: String(this.droppedItems.left.length * 10 + this.droppedItems.right.length),
            left_count: this.droppedItems.left.length,
            right_count: this.droppedItems.right.length,
            item: 'XiaoJi'
          });
        }

        const body = {
          gamerId: this.gamerId,
          questionId: this.question.question_id,
          levelId: this.routeLevelId || this.levelId,
          trackId: this.trackId,
          userAnswer: userAnswer,
          answerTime: usedTime,
        };

        this.$post('/question_bank/submit_answer', body, (res) => {
          let isCorrect = false;
          if (res && res.result) {
            const result = res.result;
            const isCorrectValue = result.isCorrect !== undefined ? result.isCorrect : result.is_correct;
            if (isCorrectValue === true || isCorrectValue === 1 || isCorrectValue === '1' || isCorrectValue === 'true') {
              isCorrect = true;
            }
          }

          if (isCorrect) {
            this.showSuccess('太棒了！', stars, usedTime);
            // this.completeLevel();
          } else {
            uni.showToast({
              title: '再拖一次试试～',
              icon: 'none',
            });
            this.resetAnswer();
          }
        });
      } else {
        if (this.question.question_type === 'drag_number') {
          const userAnswer = parseInt(this.droppedItems.right[0] || '0');
          const correctAnswer = parseInt(this.question.correct_answer?.answer || '0');
          let message = '';
          if (userAnswer > correctAnswer) {
            message = '小朋友，你数多了，再看看吧～';
          } else if (userAnswer < correctAnswer) {
            message = '小朋友，你数少了，再看看吧～';
          } else {
            message = '再拖一次试试～';
          }
          uni.showToast({
            title: message,
            icon: 'none',
          });
        } else if (this.question.question_type === 'drag_item') {
          uni.showToast({
            title: '再拖一次试试～',
            icon: 'none',
          });
        }
      }
    },
    showSuccess(message, stars, time) {
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
      this.successTime = Math.round(time);
      this.showSuccessModal = true;
      this.$forceUpdate();
    },
    resetAnswer() {
      this.droppedItems = { left: [], right: [] };
    },
    calculateStars(isCorrect, usedTime) {
      if (!isCorrect) {
        return 0;
      }
      if (usedTime <= 60) {
        return 3;
      } else if (usedTime <= 120) {
        return 2;
      }
      return 1;
    },

    generateRandomNumbers() {
      const correctAnswer = this.currentQuestion.correct_answer || {};
      const answer = correctAnswer.answer || '0';
      const correctNum = parseInt(answer);
      const numbersSet = new Set();
      numbersSet.add(correctNum);
      
      while (numbersSet.size < 9) {
        const randomNum = Math.floor(Math.random() * 50) + 1;
        numbersSet.add(randomNum);
      }
      
      this.numbers = Array.from(numbersSet).map(num => num.toString());
      this.numbers.sort(() => Math.random() - 0.5);
    },
    fetchQuestion() {
      const gamerId = Number(this.gamerId);
      const levelId = Number(this.routeLevelId || this.levelId);
      const trackId = Number(this.trackId);



      if (!gamerId || Number.isNaN(levelId) || levelId <= 0 || Number.isNaN(trackId) || trackId <= 0) {
        const local = this.generateLocalQuestion();
        this.question = local;
        this.playTyping(local.question_content.options[0]);
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
              success: () => {
              },
              fail: (err) => {
              }
            });
          }, 3000);
          return;
        }

        if (!json.result || !json.result.questions || json.result.questions.length === 0) {
          const local = this.generateLocalQuestion();
          this.question = local;
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
            q.question_content = typeof q.question_content === 'string' ? JSON.parse(q.question_content) : q.question_content;
          }
          if (q.correct_answer) {
            q.correct_answer = typeof q.correct_answer === 'string' ? JSON.parse(q.correct_answer) : q.correct_answer;
          }
        } catch (e) {
        }

        this.question = q;
        this.playTyping(q.question_content.options[0]);
      });
    },
    generateLocalQuestion() {
      const questions = [
        {
          question_id: 1024,
          track_id: 6,
          level_id: 6,
          question_type: 'drag_item',
          question_title: '认识两位数',
          question_content: {
            options: ['用12只小鸡表示'],
            decorations: [{
              type: 'XiaoJi',
              name: '小鸡',
              icon: '🐥'
            }]
          },
          correct_answer: { answer: '12', left_count: 1, right_count: 2, item: 'XiaoJi' },
          difficulty: 3,
          sort_order: 1
        },
        {
          question_id: 1025,
          track_id: 6,
          level_id: 6,
          question_type: 'drag_item',
          question_title: '认识两位数',
          question_content: {
            options: ['用33只小鸡表示'],
            decorations: [{
              type: 'XiaoJi',
              name: '小鸡',
              icon: '🐥'
            }]
          },
          correct_answer: { answer: '33', left_count: 3, right_count: 3, item: 'XiaoJi' },
          difficulty: 3,
          sort_order: 2
        },
        {
          question_id: 1026,
          track_id: 6,
          level_id: 6,
          question_type: 'drag_item',
          question_title: '认识两位数',
          question_content: {
            options: ['用25只小鸡表示'],
            decorations: [{
              type: 'XiaoJi',
              name: '小鸡',
              icon: '🐥'
            }]
          },
          correct_answer: { answer: '25', left_count: 2, right_count: 5, item: 'XiaoJi' },
          difficulty: 3,
          sort_order: 3
        },
        {
          question_id: 1027,
          track_id: 6,
          level_id: 6,
          question_type: 'drag_item',
          question_title: '认识两位数',
          question_content: {
            options: ['用41只小鸡表示'],
            decorations: [{
              type: 'XiaoJi',
              name: '小鸡',
              icon: '🐥'
            }]
          },
          correct_answer: { answer: '41', left_count: 4, right_count: 1, item: 'XiaoJi' },
          difficulty: 3,
          sort_order: 4
        },
        {
          question_id: 1028,
          track_id: 6,
          level_id: 6,
          question_type: 'drag_item',
          question_title: '认识两位数',
          question_content: {
            options: ['用56只小鸡表示'],
            decorations: [{
              type: 'XiaoJi',
              name: '小鸡',
              icon: '🐥'
            }]
          },
          correct_answer: { answer: '56', left_count: 5, right_count: 6, item: 'XiaoJi' },
          difficulty: 3,
          sort_order: 5
        },
        {
          question_id: 1029,
          track_id: 6,
          level_id: 6,
          question_type: 'drag_number',
          question_title: '认识两位数',
          question_content: {
            options: ['爷爷抓了25只小鸡，左边拖整十，右边拖单个'],
            decorations: [{
              type: 'XiaoJi',
              name: '小鸡',
              icon: '🐥'
            }]
          },
          correct_answer: { answer: '25', left_digit: '2', right_digit: '5' },
          difficulty: 3,
          sort_order: 6
        },
        {
          question_id: 1030,
          track_id: 6,
          level_id: 6,
          question_type: 'drag_number',
          question_title: '认识两位数',
          question_content: {
            options: ['爷爷抓了41只小鸡，左边拖整十，右边拖单个'],
            decorations: [{
              type: 'XiaoJi',
              name: '小鸡',
              icon: '🐥'
            }]
          },
          correct_answer: { answer: '41', left_digit: '4', right_digit: '1' },
          difficulty: 3,
          sort_order: 7
        },
        {
          question_id: 1031,
          track_id: 6,
          level_id: 6,
          question_type: 'drag_number',
          question_title: '认识两位数',
          question_content: {
            options: ['爷爷抓了13只小鸡，左边拖整十，右边拖单个'],
            decorations: [{
              type: 'XiaoJi',
              name: '小鸡',
              icon: '🐥'
            }]
          },
          correct_answer: { answer: '13', left_digit: '1', right_digit: '3' },
          difficulty: 3,
          sort_order: 8
        },
        {
          question_id: 1032,
          track_id: 6,
          level_id: 6,
          question_type: 'drag_number',
          question_title: '认识两位数',
          question_content: {
            options: ['爷爷抓了37只小鸡，左边拖整十，右边拖单个'],
            decorations: [{
              type: 'XiaoJi',
              name: '小鸡',
              icon: '🐥'
            }]
          },
          correct_answer: { answer: '37', left_digit: '3', right_digit: '7' },
          difficulty: 3,
          sort_order: 9
        },
        {
          question_id: 1033,
          track_id: 6,
          level_id: 6,
          question_type: 'drag_number',
          question_title: '认识两位数',
          question_content: {
            options: ['爷爷抓了52只小鸡，左边拖整十，右边拖单个'],
            decorations: [{
              type: 'XiaoJi',
              name: '小鸡',
              icon: '🐥'
            }]
          },
          correct_answer: { answer: '52', left_digit: '5', right_digit: '2' },
          difficulty: 3,
          sort_order: 10
        }
      ];
      return questions[Math.floor(Math.random() * questions.length)];
    },
    toggleMode() {
      this.isAddMode = !this.isAddMode;
    },
    toggleItem(icon, position) {
      if (this.isAddMode) {
        this.droppedItems[position].push(icon);
      } else {
        if (this.droppedItems[position].length > 0) {
          this.droppedItems[position].pop();
        }
      }
    },
    removeDroppedItem(position, index) {
      this.droppedItems[position].splice(index, 1);
    },
    clearDroppedItems() {
      this.droppedItems = { left: [], right: [] };
    },
    getTipMessage() {
      const correctAnswer = this.currentQuestion.correct_answer || {};
      const currentLeft = this.droppedItems.left.length;
      const currentRight = this.droppedItems.right.length;
      const userTotal = currentLeft * 10 + currentRight;
      const correctTotal = parseInt(correctAnswer.answer || '0');
      
      if (this.isAddMode) {
        if (userTotal < correctTotal) {
          return '请添加物品';
        } else if (userTotal > correctTotal) {
          return '物品数量超过了，点击切换到移除模式试试';
        } else {
          return '数量正确，点击提交试试，也可以继续添加哦';
        }
      } else {
        if (userTotal < correctTotal) {
          return '物品数量不够，点击切换到添加模式';
        } else if (userTotal > correctTotal) {
          return '请移除多余的物品';
        } else {
          return '数量正确，点击提交试试';
        }
      }
    },
    goBack() {
      uni.navigateBack();
    },
    getNumberTipMessage() {
      const correctAnswer = this.currentQuestion.correct_answer || {};
      const userAnswer = this.droppedItems.right[0] || '';
      const correctNum = parseInt(correctAnswer.answer || '0');
      const userNum = parseInt(userAnswer || '0');

      if (!userAnswer) {
        return '请选择一个数字';
      } else if (userNum < correctNum) {
        return '小朋友，你数少了，再看看吧';
      } else if (userNum > correctNum) {
        return '小朋友，你数多了，再看看吧';
      } else {
        return '小朋友，你现在选择的对了，你点击提交试一下';
      }
    },
    isNumberNeedScale(num) {
      // 只有在玩家已经进行了第一次作答后，才给出样式变化
      if (!this.hasAnswered) {
        return false;
      }
      
      const correctAnswer = this.currentQuestion.correct_answer || {};
      const correctNum = parseInt(correctAnswer.answer || '0');
      const userNum = parseInt(this.selectedNumber || '0');
      const numValue = parseInt(num);

      if (userNum === correctNum) {
        return false;
      }

      if (userNum < correctNum) {
        return numValue > userNum;
      }

      if (userNum > correctNum) {
        return numValue < userNum;
      }

      return false;
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
};
</script>

<style lang="scss" scoped>
.digit-level-page {
  width: 100%;
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

.rope-title {
  font-size: 30rpx;
  color: #2c2c2c;
  margin-bottom: 20rpx;
  font-weight: 700;
  text-align: center;
  text-shadow: 0 1rpx 3rpx rgba(255, 255, 255, 0.9);
  letter-spacing: 1rpx;
  -webkit-font-smoothing: antialiased;
}

.question-content {
  font-size: 28rpx;
  color: #2c2c2c;
  margin-bottom: 15rpx;
  text-align: center;
  text-shadow: 0 1rpx 3rpx rgba(255, 255, 255, 0.8);
  line-height: 1.5;
  -webkit-font-smoothing: antialiased;
}

.question-image {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 10rpx;
  flex-wrap: wrap;
  max-width: 100%;
  overflow: hidden;
}

.item-icon {
  font-size: 48rpx;
  margin: 0 10rpx;
  animation: bounce 2s infinite;
}

.hint {
  font-size: 26rpx;
  color: #4CAF50;
  font-weight: bold;
  margin-top: 10rpx;
  text-align: center;
}

.answer-area {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  margin: 20rpx 0;
  position: relative;
}

.answer-boxes {
  display: flex;
  gap: 20rpx;
  width: 100%;
  max-width: 600rpx;
  justify-content: center;
}

.answer-box {
  width: 250rpx;
  height: 180rpx;
  border: 4rpx dashed #999;
  border-radius: 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  cursor: pointer;
}

.answer-box:hover {
  border-color: #4CAF50;
  box-shadow: 0 0 20rpx rgba(76, 175, 80, 0.3);
}

.left-box {
  border-color: #3498db;
}

.right-box {
  border-color: #e74c3c;
}

.box-label {
  font-size: 24rpx;
  font-weight: bold;
  color: #666;
  margin-bottom: 10rpx;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 90%;
}

.placeholder {
  color: #999;
  font-size: 24rpx;
  text-align: center;
}

.counter {
  font-size: 22rpx;
  font-weight: bold;
  color: #333;
  margin-top: 10rpx;
  text-align: center;
  background-color: rgba(255, 255, 255, 0.6);
  padding: 4rpx 16rpx;
  border-radius: 12rpx;
  border: 1rpx solid #ddd;
}

.dropped-items {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 5rpx;
}

.dropped-item {
  font-size: 32rpx;
  animation: bounce 1s;
}

.resource-area {
  width: 100%;
  margin-top: 20rpx;
  padding: 20rpx;
  background-color: rgba(255, 255, 255, 0.6);
  border-radius: 16rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
  min-height: 200rpx;
}

.number-resources {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15rpx;
  justify-items: center;
  width: 100%;
  max-width: 500rpx;
  margin: 0 auto;
}

.item-resources {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}

.number-card {
  width: 25vw;
  max-width: 110rpx;
  height: 25vw;
  max-height: 110rpx;
  background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20rpx;
  font-size: 44rpx;
  font-weight: bold;
  cursor: grab;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 6rpx 16rpx rgba(76, 175, 80, 0.4);
  user-select: none;
  touch-action: none;
  position: relative;
  overflow: hidden;
  min-width: 80rpx;
  min-height: 80rpx;
}

.number-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s ease;
}

.number-card:hover::before {
  left: 100%;
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

.number-card-smaller {
  animation: scaleAnimation 1s ease-in-out infinite;
  transform: scale(0.9);
}

@keyframes scaleAnimation {
  0% {
    transform: scale(0.9);
  }
  50% {
    transform: scale(0.8);
  }
  100% {
    transform: scale(0.9);
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

.single-item:hover {
  transform: scale(1.2);
}

.bubble-item {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bubble {
  position: relative;
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.8);
  border: 4rpx solid #3498db;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  padding: 10rpx;
  box-shadow: 0 4rpx 12rpx rgba(52, 152, 219, 0.3);
  transition: all 0.3s ease;
}

.bubble:hover {
  transform: scale(1.1);
  box-shadow: 0 8rpx 20rpx rgba(52, 152, 219, 0.5);
}

.triangle-bubble {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 10rpx;
}

.triangle-row {
  display: flex;
  justify-content: center;
  gap: 8rpx;
}

.triangle-row:first-child {
  margin-bottom: 8rpx;
}

.triangle-row:first-child .bubble-icon {
  transform: translateY(4rpx);
}

.bubble-icon {
  font-size: 32rpx;
  margin: 2rpx;
  animation: bounce 1s infinite;
}

.number-answer-box {
  width: 90%;
  max-width: 550rpx;
  height: 180rpx;
  border-color: #999;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.9);
  border: 4rpx dashed #999;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
}

.number-dropped-items {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 20rpx;
  flex-wrap: nowrap;
}

.number-item {
  font-size: 56rpx;
  font-weight: bold;
  color: #3498db;
  background: rgba(255, 255, 255, 0.9);
  padding: 12rpx 24rpx;
  border-radius: 20rpx;
  border: 3rpx solid #3498db;
  animation: bounce 1s;
  box-shadow: 0 4rpx 12rpx rgba(52, 152, 219, 0.3);
  max-width: 90%;
  box-sizing: border-box;
}

.number-item:hover {
  transform: scale(1.1);
  box-shadow: 0 4rpx 12rpx rgba(52, 152, 219, 0.3);
}

.bubble-dropped-item {
  font-size: 32rpx;
  margin: 4rpx;
  animation: bounce 1s infinite;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #3498db;
  box-shadow: 0 2rpx 6rpx rgba(52, 152, 219, 0.3);
}

.btn-spacing {
  width: 40rpx;
}

.total-counter {
  position: absolute;
  bottom: 20rpx;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(255, 255, 255, 0.9);
  padding: 10rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: bold;
  color: #333;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
  z-index: 10;
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

.clear-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 8rpx 20rpx rgba(244, 67, 54, 0.5);
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

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.item-card:hover {
  transform: scale(1.2);
}

.bottom-bar {
  margin: 20rpx 0 0;
  display: flex;
  justify-content: center;
  position: relative;
  z-index: 1;
}

.btn-submit {
  width: 200rpx;
  min-width: 200rpx;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: none;
  background: linear-gradient(135deg, #ff8c42 0%, #ff6b35 50%, #ff5722 100%);
  color: #fff;
  box-shadow: 0 8rpx 24rpx rgba(255, 107, 53, 0.5);
  transition: all 0.3s ease;
  cursor: pointer;
  margin-top: 20rpx;
  box-sizing: border-box;
  text-align: center;
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
  width: 90%;
  max-width: 680rpx;
  min-height: 600rpx;
  max-height: 80vh;
  padding: 30rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10rpx);
  -webkit-backdrop-filter: blur(10rpx);
  border: 2rpx solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.2);
  overflow-y: auto;
  box-sizing: border-box;
}

.game-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  gap: 30rpx;
  padding: 20rpx 0;
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
