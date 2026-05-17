<template>
  <view class="abacus-level-page">
    <image class="background-image" src="/static/img/abacus/BeiJing3.png" mode="aspectFill" />
    <view class="default-question-tip" v-if="showDefaultQuestionTip">
      <text class="tip-text">⚠️ 当前为默认题目，请联系运维人员</text>
    </view>
    <view class="scene">
      <view class="elder-area">
        <view class="speech-bubble" :class="{ 'expanded': showFullSpeech }" @click="handleOpenGamePopup">
          <text class="speech-text">{{ displayText }}</text>
        </view>
      </view>
      <view class="child-area" @click="handleOpenGamePopup">
        <view class="child-tip" v-if="showChildTip">
          <text class="tip-text">点击这里，来试试吧</text>
        </view>
      </view>
    </view>
    <view class="popup-mask" v-if="showGamePopup" @click="handleCloseGamePopup"></view>
    <view class="game-popup" v-if="showGamePopup" @click.stop>
      <view class="abacus-area">
        <text class="abacus-title" :class="{ 'abacus-title--wrap': wrapTitleByComma }">{{ displayQuestionTitle }}</text>
        <text class="abacus-title-measure">{{ rawQuestionTitle }}</text>
        <view class="digit-labels-top">
          <view v-for="label in ['万', '千', '百', '十', '个']" :key="label" class="digit-label">{{ label }}</view>
        </view>
        <view class="abacus-container">
          <view class="abacus-frame-top"></view>
          <view class="abacus-frame-bottom"></view>
          <view class="abacus-frame-left"></view>
          <view class="abacus-frame-right"></view>
          <view class="abacus-beam"></view>
          <view class="abacus-columns">
            <view v-for="(col, index) in 5" :key="col" class="abacus-column">
              <view class="abacus-rod"></view>
              <view class="upper-bead-wrapper" :class="{ 'bead-hint': hintBeads[index] && hintBeads[index][0] }" @click="handleToggleBead(index, 'upper')">
                <view class="bead upper-bead" :class="{ active: beads[index][0].active }"></view>
              </view>
              <view class="lower-beads">
                <view v-for="(row, rIndex) in 4" :key="row" class="lower-bead-wrapper" :class="{ 'lower-bead-wrapper--disabled': isLowerBeadDimmed(index, rIndex), 'bead-hint': hintBeads[index] && hintBeads[index][rIndex + 1] }" @click="handleToggleBead(index, 'lower', rIndex)">
                  <view class="bead lower-bead" :class="{ active: beads[index][rIndex + 1].active }"></view>
                </view>
              </view>
            </view>
          </view>
        </view>
        <view class="abacus-info">
          <text>当前数字：{{ currentValue }}</text>
          <text class="time-text">用时：{{ formatTime(elapsedSeconds) }}</text>
        </view>
        <view class="abacus-hint">
          <text class="abacus-hint-text">{{ hintText }}</text>
        </view>
      </view>
      <view class="bottom-bar">
        <button class="btn-clear" @click="handleClearAbacus">清空</button>
        <button class="btn-submit" :class="{ 'btn-disabled': !canSubmit }" type="primary" @click="handleSubmitAnswer" :disabled="!canSubmit">提交答案</button>
      </view>
    </view>
    <view class="success-modal" v-if="showSuccessModal" @click="handleCloseSuccessModal">
      <view class="modal-content" @click.stop>
        <view class="modal-icon">✓</view>
        <view class="modal-title">{{ successMessage }}</view>
        <view class="modal-stars">
          <text v-for="(star, index) in 3" :key="index" class="star" :class="{ 'star-active': index < successStarCount }">⭐</text>
        </view>
        <view class="modal-time">用时：{{ formatTime(successTime) }}</view>
        <button class="modal-btn" @click="handleCloseSuccessModal">继续挑战</button>
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
  data() {
    return {
      gamerId: null,
      trackId: 3,
      levelId: 3,
      routeLevelId: 3,
      question: null,
      fullText: '今日族长正在思考要出什么题目给你……',
      displayText: '',
      typingTimer: null,
      beads: [
        [{type:'upper', active:false, value:5}, {type:'lower',active:false,value:1}, {type:'lower',active:false,value:1}, {type:'lower',active:false,value:1}, {type:'lower',active:false,value:1}],
        [{type:'upper', active:false, value:5}, {type:'lower',active:false,value:1}, {type:'lower',active:false,value:1}, {type:'lower',active:false,value:1}, {type:'lower',active:false,value:1}],
        [{type:'upper', active:false, value:5}, {type:'lower',active:false,value:1}, {type:'lower',active:false,value:1}, {type:'lower',active:false,value:1}, {type:'lower',active:false,value:1}],
        [{type:'upper', active:false, value:5}, {type:'lower',active:false,value:1}, {type:'lower',active:false,value:1}, {type:'lower',active:false,value:1}, {type:'lower',active:false,value:1}],
        [{type:'upper', active:false, value:5}, {type:'lower',active:false,value:1}, {type:'lower',active:false,value:1}, {type:'lower',active:false,value:1}, {type:'lower',active:false,value:1}],
      ],
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
      wrapTitleByComma: false,
      showWelcomeText: true,
      preloadedQuestion: null,
      hasInteracted: false,
      hintText: '',
      hintTimer: null,
      hintClearTimer: null,
      hintBeads: [
        [false, false, false, false, false],
        [false, false, false, false, false],
        [false, false, false, false, false],
        [false, false, false, false, false],
        [false, false, false, false, false],
      ],
    };
  },
  onLoad(options) {
    if (options && options.gamer_id) {
      const gid = Number(options.gamer_id);
      if (!Number.isNaN(gid) && gid > 0) this.gamerId = gid;
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
      if (!Number.isNaN(tid) && tid > 0) this.trackId = tid;
    }
    this.resetTimer();
    this.fetchQuestion(true);
    this.playTyping('欢迎成为账房小学徒！商铺已清点好新到货品的数量，根据任务给出的数字，用算盘准确拨出对应的数值吧～');
  },
  computed: {
    targetNumberText() {
      return this.question && this.question.targetNumber ? this.question.targetNumber : '';
    },
    canSubmit() {
      if (!this.question || !this.question.targetNumber) return false;
      return this.currentValue === Number(this.question.targetNumber);
    },
    currentValue() {
      const weights = [10000, 1000, 100, 10, 1];
      let total = 0;
      for (let col = 0; col < 5; col++) {
        let colValue = 0;
        for (let bead = 0; bead < 5; bead++) {
          if (this.beads[col][bead].active) {
            colValue += this.beads[col][bead].value;
          }
        }
        total += colValue * weights[col];
      }
      return total;
    },
    rawQuestionTitle() {
      return (this.question && this.question.questionTitle ? this.question.questionTitle : '点击算珠拨动') || '';
    },
    displayQuestionTitle() {
      if (!this.wrapTitleByComma) return this.rawQuestionTitle;
      return String(this.rawQuestionTitle).replace(/([，,])\s*/g, '$1\n');
    },
  },
  methods: {
    handleToggleSpeech() {
      this.playClickSound();
      this.showFullSpeech = !this.showFullSpeech;
    },
    handleOpenGamePopup() {
      this.playClickSound();
      this.showGamePopup = true;
      this.showChildTip = false;
      if (this.childTipTimer) { clearTimeout(this.childTipTimer); this.childTipTimer = null; }
      this.stopHintTimer();
      this.$nextTick(() => {
        this.updateTitleWrap();
      });
    },
    handleCloseGamePopup() {
      this.playClickSound();
      this.showGamePopup = false;
      this.stopHintTimer();
    },
    handleToggleBead(col, type, row = 0) {
      this.playClickSound();
      this.toggleBead(col, type, row);
    },
    handleClearAbacus() {
      this.playClickSound();
      this.clearAbacus();
    },
    handleSubmitAnswer() {
      this.playClickSound();
      this.submitAnswer();
    },
    handleCloseSuccessModal() {
      this.playClickSound();
      this.closeSuccessModal();
    },
    playTyping(text) {
      this.fullText = text;
      this.displayText = '';
      this.showFullSpeech = false;
      this.showChildSpeech = false;
      this.showChildTip = false;
      this.childSpeechText = '';
      if (this.childTipTimer) { clearTimeout(this.childTipTimer); this.childTipTimer = null; }
      if (this.typingTimer) { clearInterval(this.typingTimer); }
      let index = 0;
      this.typingTimer = setInterval(() => {
        if (index >= this.fullText.length) {
          clearInterval(this.typingTimer);
          this.typingTimer = null;
          
          if (this.showWelcomeText) {
            this.showWelcomeText = false;
            setTimeout(() => {
              if (this.preloadedQuestion) {
                this.question = this.preloadedQuestion;
                this.wrapTitleByComma = false;
                this.playTyping(this.question.title);
                if (this.showGamePopup) {
                  this.$nextTick(() => {
                    this.updateTitleWrap();
                  });
                }
              } else {
                this.fetchQuestion();
              }
            }, 1000);
          } else {
            this.displayChildSpeech();
          }
          return;
        }
        let char = this.fullText[index];
        if (char === '，' || char === ',') {
          this.displayText += char + '\n';
        } else {
          this.displayText += char;
        }
        index++;
      }, 80);
    },
    displayChildSpeech() {
      this.childSpeechText = '好哒爷爷~，我来试一试';
      this.showChildSpeech = true;
      if (this.childTipTimer) { clearTimeout(this.childTipTimer); this.childTipTimer = null; }
      this.showChildTip = false;
      this.childTipTimer = setTimeout(() => {
        if (!this.showGamePopup) this.showChildTip = true;
      }, 6000);
    },
    resetTimer() {
      this.startTime = Date.now();
      this.elapsedSeconds = 0;
      if (this.elapsedTimer) { clearInterval(this.elapsedTimer); }
      this.elapsedTimer = setInterval(() => { this.elapsedSeconds += 1; }, 1000);
    },
    fetchQuestion(preload = false) {
      const gamerId = Number(this.gamerId);
      const levelId = Number(this.routeLevelId || this.levelId);
      const trackId = Number(this.trackId);

      if (!gamerId || Number.isNaN(levelId) || levelId <= 0 || Number.isNaN(trackId) || trackId <= 0) {
        const local = {
          question_id: 0,
          targetNumber: 1,
          title: '今日族长笑着对你说：用算盘表示数字 1，你会怎么拨珠呢？',
        };
        if (preload) {
          this.preloadedQuestion = local;
        } else {
          this.question = local;
          this.wrapTitleByComma = false;
          this.playTyping(local.title);
        }
        return;
      }
      const params = { gamerId, levelId, trackId };
      this.$get('/question_bank/get_level_questions', params, (json) => {
        if (json && json.error) {
          uni.showToast({ title: json.error.message || '获取题目失败', icon: 'none' });
          return;
        }
        const allThreeStars = json.result && (json.result.allThreeStars === true || json.result.all_three_stars === true);
        if (allThreeStars) {
          this.showAllCompleteModal = true;
          setTimeout(() => {
            this.showAllCompleteModal = false;
            uni.redirectTo({ url: '/pages/track/abacus' });
          }, 200);
          return;
        }
        if (!json.result || !json.result.questions || json.result.questions.length === 0) {
          const local = {
            question_id: 0,
            targetNumber: 1,
            title: '今日族长笑着对你说：用算盘表示数字 1，你会怎么拨珠呢？',
          };
          if (preload) {
            this.preloadedQuestion = local;
          } else {
            this.question = local;
            this.wrapTitleByComma = false;
            this.showDefaultQuestionTip = true;
            if (this.defaultQuestionTipTimer) clearTimeout(this.defaultQuestionTipTimer);
            this.defaultQuestionTipTimer = setTimeout(() => { this.showDefaultQuestionTip = false; }, 2000);
            this.playTyping(local.title);
          }
          return;
        }
        if (!preload) {
          this.showDefaultQuestionTip = false;
          if (this.defaultQuestionTipTimer) clearTimeout(this.defaultQuestionTipTimer);
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
              elderSpeech = content.options[0];
              questionTitle = content.options[0];
            }
            if (content.targetNumber) target = Number(content.targetNumber);
          }
          if (q.correct_answer) {
            try {
              const answer = typeof q.correct_answer === 'string' ? JSON.parse(q.correct_answer) : q.correct_answer;
              if (answer.answer) {
                const answerNum = Number(answer.answer);
                if (!Number.isNaN(answerNum) && answerNum > 0) target = answerNum;
              } else if (answer.count) {
                const countNum = Number(answer.count);
                if (!Number.isNaN(countNum) && countNum > 0) target = countNum;
              }
            } catch (e) {}
          }
        } catch (e) {}

        if (!elderSpeech || elderSpeech.trim() === '') {
          elderSpeech = `今日族长笑着对你说：用算盘表示数字 ${target}，你会怎么拨珠呢？`;
        }
        if (!questionTitle || questionTitle.trim() === '') {
          questionTitle = `拨动算盘，表示数字${target}`;
        } else {
          questionTitle = questionTitle.replace(/表示数字\d+/, `表示数字${target}`);
        }

        const questionData = {
          question_id: q.question_id,
          targetNumber: target,
          title: elderSpeech.replace(/数字 \d+/, `数字 ${target}`),
          questionTitle: questionTitle,
        };
        
        if (preload) {
          this.preloadedQuestion = questionData;
        } else {
          this.question = questionData;
          this.wrapTitleByComma = false;
          this.playTyping(this.question.title);
          if (this.showGamePopup) {
            this.$nextTick(() => {
              this.updateTitleWrap();
            });
          }
        }
      });
    },

    updateTitleWrap() {
      if (!this.showGamePopup) return;
      const title = String(this.rawQuestionTitle || '');
      if (!/[，,]/.test(title)) {
        this.wrapTitleByComma = false;
        return;
      }
      const sys = uni.getSystemInfoSync ? uni.getSystemInfoSync() : null;
      const windowWidth = sys && sys.windowWidth ? sys.windowWidth : 375;
      const paddingPx = (40 * 2 * windowWidth) / 750;
      const query = uni.createSelectorQuery().in(this);
      query.select('.abacus-title').boundingClientRect();
      query.select('.abacus-title-measure').boundingClientRect();
      query.exec((res) => {
        const titleRect = res && res[0] ? res[0] : null;
        const measureRect = res && res[1] ? res[1] : null;
        if (!titleRect || !measureRect) return;
        const available = Math.max(0, (titleRect.width || 0) - paddingPx);
        this.wrapTitleByComma = (measureRect.width || 0) > available;
      });
    },
    toggleBead(col, type, row = 0) {
      if (!this.hasInteracted) {
        this.hasInteracted = true;
        this.startHintTimer();
      }
      if (type === 'upper') {
        this.beads[col][0].active = !this.beads[col][0].active;
      } else {
        const beadIndex = row + 1;
        const isActive = this.beads[col][beadIndex].active;
        if (!isActive) {
          for (let i = 1; i < beadIndex; i++) {
            if (!this.beads[col][i].active) return;
          }
          this.beads[col][beadIndex].active = true;
          this.updateHintTextRealtime();
          if (this.canSubmit) this.stopHintTimer();
          return;
        }
        for (let i = beadIndex + 1; i <= 4; i++) {
          if (this.beads[col][i].active) return;
        }
        this.beads[col][beadIndex].active = false;
      }
      this.updateHintTextRealtime();
      if (this.canSubmit) this.stopHintTimer();
    },
    isLowerBeadClickable(col, row) {
      const beadIndex = row + 1;
      const isActive = this.beads[col][beadIndex].active;
      if (!isActive) {
        for (let i = 1; i < beadIndex; i++) {
          if (!this.beads[col][i].active) return false;
        }
        return true;
      }
      for (let i = beadIndex + 1; i <= 4; i++) {
        if (this.beads[col][i].active) return false;
      }
      return true;
    },
    isLowerBeadDimmed(col, row) {
      const beadIndex = row + 1;
      if (this.beads[col][beadIndex].active) return false;
      return !this.isLowerBeadClickable(col, row);
    },
    getExpectedBeads(targetNumber) {
      const labels = ['万', '千', '百', '十', '个'];
      const weights = [10000, 1000, 100, 10, 1];
      const expected = [
        [false, false, false, false, false],
        [false, false, false, false, false],
        [false, false, false, false, false],
        [false, false, false, false, false],
        [false, false, false, false, false],
      ];

      const num = Number(targetNumber || 0);
      for (let col = 0; col < 5; col++) {
        const digit = Math.floor(num / weights[col]) % 10;
        const expectedUpper = digit >= 5;
        const expectedLowerCount = expectedUpper ? (digit - 5) : digit;
        expected[col][0] = expectedUpper;
        for (let i = 1; i <= expectedLowerCount; i++) expected[col][i] = true;
      }
      return { expected, labels };
    },
    clearHintBeads() {
      this.hintBeads = [
        [false, false, false, false, false],
        [false, false, false, false, false],
        [false, false, false, false, false],
        [false, false, false, false, false],
        [false, false, false, false, false],
      ];
    },
    updateHintOnce() {
      if (!this.showGamePopup) return;
      if (!this.hasInteracted) return;
      if (this.canSubmit) return;
      if (!this.question || !this.question.targetNumber) return;

      const { expected, labels } = this.getExpectedBeads(this.question.targetNumber);
      const missingHints = [
        [false, false, false, false, false],
        [false, false, false, false, false],
        [false, false, false, false, false],
        [false, false, false, false, false],
        [false, false, false, false, false],
      ];
      const extraHints = [
        [false, false, false, false, false],
        [false, false, false, false, false],
        [false, false, false, false, false],
        [false, false, false, false, false],
        [false, false, false, false, false],
      ];

      let missingCount = 0;
      let extraCount = 0;

      for (let col = 0; col < 5; col++) {
        const label = labels[col];

        const expectedUpperCount = expected[col][0] ? 1 : 0;
        let expectedLowerCount = 0;
        for (let i = 1; i <= 4; i++) if (expected[col][i]) expectedLowerCount += 1;

        const upperActive = Boolean(this.beads[col][0].active);
        const upperExpected = Boolean(expected[col][0]);
        if (upperExpected && !upperActive) {
          missingHints[col][0] = true;
          missingCount += 1;
        } else if (!upperExpected && upperActive) {
          extraHints[col][0] = true;
          extraCount += 1;
        }

        let lowerActiveCount = 0;
        for (let i = 1; i <= 4; i++) {
          if (this.beads[col][i].active) lowerActiveCount += 1;
        }

        if (expectedLowerCount > lowerActiveCount) {
          const need = expectedLowerCount - lowerActiveCount;
          for (let i = 1; i <= expectedLowerCount; i++) {
            if (!this.beads[col][i].active) missingHints[col][i] = true;
          }
          missingCount += need;
        } else if (lowerActiveCount > expectedLowerCount) {
          const extra = lowerActiveCount - expectedLowerCount;
          for (let i = expectedLowerCount + 1; i <= 4; i++) {
            if (this.beads[col][i].active) extraHints[col][i] = true;
          }
          extraCount += extra;
        }
      }

      const hasMissing = missingCount > 0;
      const hasExtra = extraCount > 0;
      if (!hasMissing && !hasExtra) return;

      this.hintBeads = hasMissing ? missingHints : extraHints;

      if (this.hintClearTimer) clearTimeout(this.hintClearTimer);
      this.hintClearTimer = setTimeout(() => {
        this.clearHintBeads();
      }, 2200);
    },
    updateHintTextRealtime() {
      if (!this.showGamePopup || !this.hasInteracted || this.canSubmit || !this.question || !this.question.targetNumber) {
        this.hintText = '';
        return;
      }

      const { expected, labels } = this.getExpectedBeads(this.question.targetNumber);
      const lines = [];

      for (let col = 0; col < 5; col++) {
        const expectedUpperCount = expected[col][0] ? 1 : 0;
        let expectedLowerCount = 0;
        for (let i = 1; i <= 4; i++) if (expected[col][i]) expectedLowerCount += 1;

        const currentUpperCount = this.beads[col][0].active ? 1 : 0;
        let currentLowerCount = 0;
        for (let i = 1; i <= 4; i++) if (this.beads[col][i].active) currentLowerCount += 1;

        if (currentUpperCount === expectedUpperCount && currentLowerCount === expectedLowerCount) continue;
        lines.push(`${labels[col]}位：梁上${expectedUpperCount}颗，梁下${expectedLowerCount}颗（当前上${currentUpperCount}下${currentLowerCount}）`);
      }

      this.hintText = lines.length > 0 ? `提示：\n${lines.join('\n')}` : '';
    },
    startHintTimer() {
      if (this.hintTimer) return;
      this.hintTimer = setInterval(() => {
        this.updateHintOnce();
      }, 3000);
    },
    stopHintTimer() {
      if (this.hintTimer) {
        clearInterval(this.hintTimer);
        this.hintTimer = null;
      }
      if (this.hintClearTimer) {
        clearTimeout(this.hintClearTimer);
        this.hintClearTimer = null;
      }
      this.hintText = '';
      this.clearHintBeads();
      this.hasInteracted = false;
    },
    resetBeads() {
      for (let col = 0; col < 5; col++) {
        for (let bead = 0; bead < 5; bead++) {
          this.beads[col][bead].active = false;
        }
      }
    },
    clearAbacus() {
      this.resetBeads();
      this.clearHintBeads();
      this.updateHintTextRealtime();
    },
    submitAnswer() {
      if (!this.question || !this.canSubmit) return;
      const count = this.currentValue;
      const target = Number(this.question.targetNumber || 0);
      const isCorrectFront = count === target;
      const usedTime = (Date.now() - this.startTime) / 1000.0;
      const stars = this.calculateStars(isCorrectFront, usedTime);

      if (!this.gamerId || !this.question.question_id) {
        if (isCorrectFront) {
          const actualTime = usedTime;
          let encouragement = '';
          if (stars === 3) encouragement = '太厉害了！1分钟内完成，获得3颗星！';
          else if (stars === 2) encouragement = '不错！1分半内完成，获得2颗星！';
          else if (stars === 1) encouragement = '很好！2分钟内完成，获得1颗星！';
          else encouragement = '答对了！继续努力，争取获得更多星星！';
          
          this.showGamePopup = false;
          this.successMessage = encouragement;
          this.successStarCount = stars;
          this.successTime = Math.round(actualTime);
          this.$audio.playTipSound('/static/audio/pass.mp3');
          this.showSuccessModal = true;
          this.$forceUpdate();
        } else {
          uni.showToast({ title: '再试试，多拨几颗算珠～', icon: 'none' });
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
        let isCorrect = false;
        if (res && res.result) {
          const result = res.result;
          const isCorrectValue = result.isCorrect !== undefined ? result.isCorrect : result.is_correct;
          if (isCorrectValue === true || isCorrectValue === 1 || isCorrectValue === '1' || isCorrectValue === 'true') isCorrect = true;
          else if (isCorrectValue === false || isCorrectValue === 0 || isCorrectValue === '0' || isCorrectValue === 'false') isCorrect = false;
          else if (isCorrectValue != null) isCorrect = Boolean(isCorrectValue);
        }
        
        if (isCorrect) {
          const actualTime = usedTime;
          const starCount = this.calculateStars(true, actualTime);
          let encouragement = '';
          if (starCount === 3) encouragement = '太厉害了！1分钟内完成，获得3颗星！';
          else if (starCount === 2) encouragement = '不错！1分半内完成，获得2颗星！';
          else if (starCount === 1) encouragement = '很好！2分钟内完成，获得1颗星！';
          else encouragement = '答对了！继续努力，争取获得更多星星！';
          
          this.showGamePopup = false;
          this.successMessage = encouragement;
          this.successStarCount = starCount;
          this.successTime = Math.round(actualTime);
          this.$audio.playTipSound('/static/audio/pass.mp3');
          this.showSuccessModal = true;
          this.$forceUpdate();
        } else {
          uni.showToast({ title: '有点小问题，再想一想～', icon: 'none' });
        }
      });
    },
    calculateStars(isCorrect, usedTime) {
      if (!isCorrect) return 0;
      if (usedTime <= 60) return 3;
      else if (usedTime <= 120) return 2;
      return 1;
    },
    formatTime(seconds) {
      if (seconds < 60) return `${seconds} 秒`;
      const minutes = Math.floor(seconds / 60);
      const remainingSeconds = seconds % 60;
      if (remainingSeconds === 0) return `${minutes} 分`;
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
      uni.navigateBack({ delta: 1 });
    },
    startNextQuestion() {
      this.resetBeads();
      this.resetTimer();
      this.stopHintTimer();
      this.showFullSpeech = false;
      this.showGamePopup = false;
      this.showChildSpeech = false;
      this.showChildTip = false;
      this.childSpeechText = '';
      if (this.childTipTimer) { clearTimeout(this.childTipTimer); this.childTipTimer = null; }
      this.fetchQuestion();
    },
    toggleSpeech() {
      this.showFullSpeech = !this.showFullSpeech;
    },
    openGamePopup() {
      this.showGamePopup = true;
      this.showChildTip = false;
      if (this.childTipTimer) { clearTimeout(this.childTipTimer); this.childTipTimer = null; }
      this.stopHintTimer();
      this.$nextTick(() => {
        this.updateTitleWrap();
      });
    },
    closeGamePopup() {
      this.showGamePopup = false;
      this.stopHintTimer();
    },
  },
  onUnload() {
    if (this.typingTimer) { clearInterval(this.typingTimer); this.typingTimer = null; }
    if (this.elapsedTimer) { clearInterval(this.elapsedTimer); this.elapsedTimer = null; }
    if (this.defaultQuestionTipTimer) { clearTimeout(this.defaultQuestionTipTimer); this.defaultQuestionTipTimer = null; }
    if (this.childTipTimer) { clearTimeout(this.childTipTimer); this.childTipTimer = null; }
    if (this.hintTimer) { clearInterval(this.hintTimer); this.hintTimer = null; }
    if (this.hintClearTimer) { clearTimeout(this.hintClearTimer); this.hintClearTimer = null; }
  },
};
</script>

<style lang="scss" scoped>
.abacus-level-page {
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
  from { transform: translateY(-20rpx); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
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
  left: 230rpx;
  top: 15rpx;
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
  margin-top: 20rpx;
  margin-left: 10rpx;
  padding: 24rpx 28rpx;
  background: linear-gradient(135deg, #fdf5e6 0%, #faebd7 50%, #f5deb3 100%);
  border-radius: 28rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15), 0 2rpx 6rpx rgba(0, 0, 0, 0.1);
  font-size: 26rpx;
  color: #5d4037;
  max-width: 480rpx;
  min-width: 300rpx;
  position: relative;
  z-index: 3;
  cursor: pointer;
  transition: all 0.3s ease;
}

.speech-bubble:not(.expanded) .speech-text {
  display: block;
  white-space: normal;
  word-break: break-all;
}

.speech-bubble.expanded .speech-text {
  display: block;
}

.speech-bubble::before {
  content: '';
  position: absolute;
  top: 100%;
  left: 40rpx;
  width: 0;
  height: 0;
  border-left: 16rpx solid transparent;
  border-right: 16rpx solid transparent;
  border-top: 16rpx solid #f5deb3;
  filter: drop-shadow(0 2rpx 4rpx rgba(0, 0, 0, 0.15));
}

.speech-text {
  line-height: 1.8;
  font-weight: 500;
  white-space: pre-wrap;
}

.child-area {
  position: fixed;
  right: 20rpx;
  top: 200rpx;
  z-index: 2;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
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
  right: 240px;
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
  from { opacity: 0; transform: translateY(20rpx); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10rpx); }
}

.abacus-area {
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

.abacus-title {
  font-size: 30rpx;
  color: #2c2c2c;
  margin-bottom: 20rpx;
  padding: 0 40rpx;
  font-weight: 700;
  text-align: center;
  text-shadow: 0 1rpx 3rpx rgba(255, 255, 255, 0.9);
  letter-spacing: 1rpx;
  -webkit-font-smoothing: antialiased;
  display: block;
  line-height: 1.4;
}

.abacus-title--wrap {
  white-space: pre-line;
}

.abacus-title-measure {
  position: fixed;
  left: -9999px;
  top: -9999px;
  font-size: 30rpx;
  font-weight: 700;
  letter-spacing: 1rpx;
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
}

.abacus-container {
  position: relative;
  width: 100%;
  height: 540rpx;
  margin: 6rpx 0;
  background: linear-gradient(135deg, #f5e6d3 0%, #e8d5c3 100%);
  border-radius: 24rpx;
  padding: 35rpx 40rpx 28rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.2), inset 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
}

.digit-labels-top {
  width: 100%;
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin-top: 6rpx;
  padding: 0 40rpx;
  box-sizing: border-box;
}

.abacus-frame-top {
  position: absolute;
  top: 0;
  left: 8rpx;
  right: 8rpx;
  height: 28rpx;
  background: linear-gradient(180deg, #a0522d 0%, #8b4513 50%, #654321 100%);
  border-radius: 16rpx 16rpx 0 0;
  box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.45);
}

.abacus-frame-bottom {
  position: absolute;
  bottom: 0;
  left: 10rpx;
  right: 10rpx;
  height: 22rpx;
  background: linear-gradient(180deg, #654321, #8b4513, #a0522d);
  border-radius: 0 0 12rpx 12rpx;
  box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.4);
}

.abacus-frame-left {
  position: absolute;
  left: 0;
  top: 5rpx;
  bottom: 0;
  width: 16rpx;
  background: linear-gradient(90deg, #a0522d, #8b4513, #654321);
  border-radius: 10rpx 0 0 12rpx;
}

.abacus-frame-right {
  position: absolute;
  right: 0;
  top: 5rpx;
  bottom: 0;
  width: 16rpx;
  background: linear-gradient(90deg, #654321, #8b4513, #a0522d);
  border-radius: 0 10rpx 12rpx 0;
}

.abacus-beam {
  position: absolute;
  top: 160rpx;
  left: 16rpx;
  right: 16rpx;
  height: 20rpx;
  background: linear-gradient(180deg, #654321, #8b4513, #a0522d);
  border-radius: 6rpx;
  box-shadow: 0 6rpx 12rpx rgba(0, 0, 0, 0.4);
  z-index: 5;
}

.abacus-columns {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: space-around;
  align-items: flex-start;
  padding-top: 10rpx;
}

.abacus-column {
  position: relative;
  width: 70rpx;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.abacus-rod {
  position: absolute;
  left: 50%;
  top: 0;
  bottom: 1rpx;
  width: 6rpx;
  transform: translateX(-50%);
  background: linear-gradient(90deg, #5d4037, #795548, #5d4037);
  border-radius: 3rpx;
  z-index: 1;
}

.upper-bead-wrapper {
  width: 100%;
  height: 120rpx;
  display: flex;
  justify-content: center;
  align-items: flex-end;
  padding-bottom: 40rpx;
  cursor: pointer;
  z-index: 10;
}

.lower-beads {
  width: 100%;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  gap: 16rpx;
  padding-top: 60rpx;
}

.lower-bead-wrapper {
  width: 100%;
  height: 50rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  z-index: 10;
}

.bead-hint {
  animation: beadHintPulse 1.1s cubic-bezier(0.22, 1, 0.36, 1) 0s 2;
  transform-origin: center;
  will-change: transform;
}

@keyframes beadHintPulse {
  0% { transform: scale(1); }
  55% { transform: scale(1.22); }
  100% { transform: scale(1); }
}

.lower-bead-wrapper--disabled {
  opacity: 0.45;
}

.bead {
  border-radius: 45%;
  background: linear-gradient(135deg, #fff9c4 0%, #ffd700 25%, #daa520 50%, #b8860b 100%);
  box-shadow: 0 8rpx 16rpx rgba(0, 0, 0, 0.35), inset 0 5rpx 10rpx rgba(255, 255, 255, 0.7), inset 0 -3rpx 6rpx rgba(0, 0, 0, 0.15);
  transition: transform 0.35s cubic-bezier(0.22, 1, 0.36, 1), opacity 0.25s ease, background 0.25s ease, box-shadow 0.25s ease;
  cursor: pointer;
  position: relative;
  z-index: 10;
}

.upper-bead {
  width: 58rpx;
  height: 46rpx;
  transform: translateY(-24rpx);
  background: linear-gradient(135deg, #8f3d3d 0%, #b85757 45%, #5c2a2a 100%);
  box-shadow: 0 8rpx 16rpx rgba(0, 0, 0, 0.28), inset 0 5rpx 10rpx rgba(255, 255, 255, 0.33), inset 0 -3rpx 6rpx rgba(0, 0, 0, 0.2);
}

.upper-bead.active {
  transform: translateY(40rpx) scale(1.06);
  background: linear-gradient(135deg, #b34343 0%, #d76464 45%, #6b2f2f 100%);
  box-shadow: 0 10rpx 20rpx rgba(0, 0, 0, 0.24), inset 0 4rpx 8rpx rgba(255, 255, 255, 0.38), 0 0 16rpx rgba(215, 100, 100, 0.22);
}

.upper-bead:not(.active) {
  opacity: 0.65;
}

.lower-bead {
  width: 56rpx;
  height: 44rpx;
  transform: translateY(24rpx);
}

.lower-bead.active {
  transform: translateY(-48rpx) scale(1.08);
  background: linear-gradient(135deg, #fffde7 0%, #ffe082 30%, #ffca28 60%, #ffb300 100%);
  box-shadow: 0 12rpx 24rpx rgba(255, 193, 7, 0.5), inset 0 4rpx 8rpx rgba(255, 255, 255, 0.8), 0 0 20rpx rgba(255, 235, 59, 0.4);
}

.lower-bead:not(.active) {
  opacity: 0.5;
}

.digit-label {
  font-size: 26rpx;
  color: #5d4037;
  font-weight: 800;
  position: relative;
  z-index: 20;
  text-shadow: 0 1rpx 2rpx rgba(255, 255, 255, 0.8);
  letter-spacing: 2rpx;
}

.abacus-info {
  margin-top: 20rpx;
  padding: 0 30rpx;
  font-size: 28rpx;
  color: #3e2723;
  font-weight: 700;
  text-shadow: 0 1rpx 3rpx rgba(255, 255, 255, 0.9);
  -webkit-font-smoothing: antialiased;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20rpx;
}

.abacus-hint {
  margin-top: 12rpx;
  padding: 0 30rpx;
  min-height: 90rpx;
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
}

.abacus-hint-text {
  font-size: 26rpx;
  color: #6b2f2f;
  font-weight: 700;
  width: 100%;
  white-space: pre-line;
  line-height: 1.35;
  text-align: left;
  text-shadow: 0 1rpx 2rpx rgba(255, 255, 255, 0.9);
}

.bottom-bar {
  margin: 20rpx 0 0;
  display: flex;
  justify-content: space-between;
  position: relative;
  z-index: 1;
  gap: 20rpx;
}

.btn-clear {
  width: 220rpx;
  height: 90rpx;
  line-height: 90rpx;
  border-radius: 45rpx;
  font-size: 32rpx;
  font-weight: 700;
  border: 2rpx solid rgba(107, 47, 47, 0.25);
  background: rgba(255, 255, 255, 0.65);
  color: #6b2f2f;
  box-shadow: 0 6rpx 18rpx rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
  cursor: pointer;
  letter-spacing: 2rpx;
}

.btn-clear:active {
  transform: scale(0.96);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.btn-submit {
  flex: 1;
  margin: 0;
  height: 90rpx;
  line-height: 90rpx;
  border-radius: 45rpx;
  font-size: 32rpx;
  font-weight: 700;
  border: none;
  background: linear-gradient(135deg, #ff9800 0%, #ff6d00 50%, #e65100 100%);
  color: #fff;
  box-shadow: 0 8rpx 24rpx rgba(255, 152, 0, 0.5);
  transition: all 0.3s ease;
  cursor: pointer;
  letter-spacing: 2rpx;
}

.btn-submit:not(.btn-disabled):hover {
  transform: scale(1.05);
  box-shadow: 0 10rpx 28rpx rgba(255, 152, 0, 0.6);
}

.btn-submit:not(.btn-disabled):active {
  transform: scale(0.96);
  box-shadow: 0 4rpx 12rpx rgba(255, 152, 0, 0.35);
}

.btn-submit.btn-disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: scale(1);
  box-shadow: 0 4rpx 12rpx rgba(158, 158, 158, 0.3);
  background: linear-gradient(135deg, #9e9e9e 0%, #757575 100%);
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
  from { opacity: 0; }
  to { opacity: 1; }
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
  from { transform: translateY(100rpx); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
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
  0% { transform: scale(1); }
  50% { transform: scale(1.4); }
  100% { transform: scale(1.3); }
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
  border-radius: 32rpx;
  background: rgba(250, 245, 235, 0.96);
  backdrop-filter: blur(20rpx);
  -webkit-backdrop-filter: blur(20rpx);
  border: 3rpx solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 16rpx 48rpx rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  overflow: hidden;
}

.abacus-area {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 20rpx;
}
</style>
