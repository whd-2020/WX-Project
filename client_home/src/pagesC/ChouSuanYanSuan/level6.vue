<template>
  <view class="chouSuan-level-page">

    <!-- 背景图片 -->
    <image class="background-image" src="/static/img/counting_rods/BeiJing2.png" mode="aspectFill" />

    <!-- 默认题目提示 -->
    <view class="default-question-tip" v-if="showDefaultQuestionTip">
      <text class="tip-text">⚠️ 当前为默认题目，请联系运维人员</text>
    </view>

    <view class="scene">
      <!-- 右侧：椅子 -->
      <view class="child-area" @click="openGamePopup">
        <view class="speech-bubble" :class="{ 'expanded': showFullSpeech }" @click="toggleSpeech">
          <text class="speech-text">{{ displayText }}</text>
        </view>
        <view class="child-tip" v-if="showChildTip">
          <text class="tip-text">点击这里，来试试吧</text>
        </view>
        <image class="child-img" src="/static/img/counting_rods/YiZi.png" mode="aspectFit" />
      </view>
    </view>

    <!-- 游戏弹窗 -->
    <view class="popup-mask" v-if="showGamePopup" @click="closeGamePopup"></view>
    <view class="game-popup" v-if="showGamePopup" @click.stop>
      <!-- 筹算木棍互动区域 -->
      <view class="chouSuan-area">
        <text class="chouSuan-title" :class="{ 'chouSuan-title--wrap': wrapTitleByComma }">{{ displayQuestionTitle }}</text>
        <text class="chouSuan-title-measure">{{ rawQuestionTitle }}</text>

        <!-- 答题展示区域 -->
        <view class="answer-area">
          <view class="answer-box">
            <!-- Icon展示区域（单icon时显示在左上角） -->
            <template v-if="questionIcons.length === 1 && !isComboMode">
              <view class="icon-display-area single-icon">
                <text class="question-icon-unicode">{{ questionIcons[0] }}</text>
              </view>
            </template>

            <!-- 组合题双答题区 -->
            <template v-if="showComboArea">
              <!-- 第一个物品区域 -->
              <view class="combo-item-area" 
                :class="{ 'selected': selectedArea === 'first' }"
                @click="selectArea('first')"
              >
                <view class="area-icon">
                  <text class="question-icon-unicode">{{ questionIcons[0] || '🎯' }}</text>
                </view>
                <view class="answer-base">
                  <view
                    class="horizontal-stick-display"
                    v-if="firstItemHasHorizontalStick"
                    @click.stop="removeFirstItemHorizontalStick"
                  >
                  </view>
                  <view class="vertical-sticks-container">
                    <view
                      v-for="(stick, index) in firstItemVerticalSticks"
                      :key="'first-' + stick.id"
                      class="vertical-stick-display"
                      @click.stop="removeFirstItemVerticalStick(index)"
                    >
                    </view>
                  </view>
                </view>
                <view class="area-value">{{ firstItemNumber }}</view>
              </view>

              <!-- 第二个物品区域 -->
              <view class="combo-item-area" 
                :class="{ 'selected': selectedArea === 'second' }"
                @click="selectArea('second')"
              >
                <view class="area-icon">
                  <text class="question-icon-unicode">{{ questionIcons[1] || '🎯' }}</text>
                </view>
                <view class="answer-base">
                  <view
                    class="horizontal-stick-display"
                    v-if="secondItemHasHorizontalStick"
                    @click.stop="removeSecondItemHorizontalStick"
                  >
                  </view>
                  <view class="vertical-sticks-container">
                    <view
                      v-for="(stick, index) in secondItemVerticalSticks"
                      :key="'second-' + stick.id"
                      class="vertical-stick-display"
                      @click.stop="removeSecondItemVerticalStick(index)"
                    >
                    </view>
                  </view>
                </view>
                <view class="area-value">{{ secondItemNumber }}</view>
              </view>
            </template>

            <!-- 单一答题区（单icon或单位数） -->
            <template v-else-if="showSingleArea">
              <view class="single-area">
                <!-- 如果有多icon，每个答题区显示自己的icon -->
                <template v-if="questionIcons.length > 1">
                  <view class="area-icon">
                    <text class="question-icon-unicode">{{ questionIcons[0] }}</text>
                  </view>
                </template>
                <view class="answer-base" @click="selectArea('ones')" :class="{ 'selected': selectedArea === 'ones' || selectedArea === 'single' }">
                  <view
                    class="horizontal-stick-display"
                    v-if="singleHasHorizontalStick"
                    @click.stop="removeSingleHorizontalStick"
                  >
                  </view>
                  <view class="vertical-sticks-container">
                    <view
                      v-for="(stick, index) in singleVerticalSticks"
                      :key="'single-' + stick.id"
                      class="vertical-stick-display"
                      @click.stop="removeSingleVerticalStick(index)"
                    >
                    </view>
                  </view>
                </view>
                <view class="area-value">{{ singleNumber }}</view>
              </view>
            </template>

            <!-- 双区域答题区（两位数） -->
            <template v-else>
              <!-- 第一个数字区域 -->
              <view class="digit-area tens-area" 
                :class="{ 'selected': selectedArea === 'tens' }"
                @click="selectArea('tens')"
              >
                <template v-if="questionIcons.length > 1">
                  <view class="area-icon">
                    <text class="question-icon-unicode">{{ questionIcons[0] }}</text>
                  </view>
                </template>
                <view class="digit-label">十位</view>
                <view class="answer-base">
                  <view
                    class="horizontal-stick-display"
                    v-if="tensHasHorizontalStick"
                    @click.stop="removeTensHorizontalStick"
                  >
                  </view>
                  <view class="vertical-sticks-container">
                    <view
                      v-for="(stick, index) in tensVerticalSticks"
                      :key="'tens-' + stick.id"
                      class="vertical-stick-display"
                      @click.stop="removeTensVerticalStick(index)"
                    >
                    </view>
                  </view>
                </view>
                <view class="area-value">{{ tensNumber }}</view>
              </view>

              <!-- 第二个数字区域 -->
              <view class="digit-area ones-area" 
                :class="{ 'selected': selectedArea === 'ones' }"
                @click="selectArea('ones')"
              >
                <template v-if="questionIcons.length > 1">
                  <view class="area-icon">
                    <text class="question-icon-unicode">{{ questionIcons[1] }}</text>
                  </view>
                </template>
                <view class="digit-label">个位</view>
                <view class="answer-base">
                  <view
                    class="horizontal-stick-display"
                    v-if="onesHasHorizontalStick"
                    @click.stop="removeOnesHorizontalStick"
                  >
                  </view>
                  <view class="vertical-sticks-container">
                    <view
                      v-for="(stick, index) in onesVerticalSticks"
                      :key="'ones-' + stick.id"
                      class="vertical-stick-display"
                      @click.stop="removeOnesVerticalStick(index)"
                    >
                    </view>
                  </view>
                </view>
                <view class="area-value">{{ onesNumber }}</view>
              </view>
            </template>
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
      levelId: 6,
      routeLevelId: 6,
      question: null,
      fullText: '今日族长正在思考要出什么题目给你……',
      displayText: '',
      typingTimer: null,
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
      selectedArea: 'single',
      questionIcons: [],
      isTwoDigit: false,
      isComboMode: false,
      targetNumbers: [],
      singleHasHorizontalStick: false,
      singleVerticalSticks: [],
      tensHasHorizontalStick: false,
      tensVerticalSticks: [],
      onesHasHorizontalStick: false,
      onesVerticalSticks: [],
      firstItemHasHorizontalStick: false,
      firstItemVerticalSticks: [],
      secondItemHasHorizontalStick: false,
      secondItemVerticalSticks: [],
      wrapTitleByComma: false,
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
    showSingleArea() {
      return !this.isTwoDigit && !this.isComboMode;
    },
    showComboArea() {
      return this.isComboMode;
    },
    singleNumber() {
      let num = 0;
      if (this.singleHasHorizontalStick) {
        num += 5;
      }
      num += this.singleVerticalSticks.length;
      return num;
    },
    tensNumber() {
      let num = 0;
      if (this.tensHasHorizontalStick) {
        num += 5;
      }
      num += this.tensVerticalSticks.length;
      return num;
    },
    onesNumber() {
      let num = 0;
      if (this.onesHasHorizontalStick) {
        num += 5;
      }
      num += this.onesVerticalSticks.length;
      return num;
    },
    firstItemNumber() {
      let num = 0;
      if (this.firstItemHasHorizontalStick) {
        num += 5;
      }
      num += this.firstItemVerticalSticks.length;
      return num;
    },
    secondItemNumber() {
      let num = 0;
      if (this.secondItemHasHorizontalStick) {
        num += 5;
      }
      num += this.secondItemVerticalSticks.length;
      return num;
    },
    currentNumber() {
      if (this.isComboMode) {
        return `${this.firstItemNumber} ${this.secondItemNumber}`;
      }
      if (this.isTwoDigit) {
        return this.tensNumber * 10 + this.onesNumber;
      }
      return this.singleNumber;
    },
    canSubmit() {
      if (!this.question) {
        return false;
      }
      if (this.isComboMode) {
        if (!this.targetNumbers || this.targetNumbers.length < 2) {
          return false;
        }
        return this.firstItemNumber === this.targetNumbers[0] && 
               this.secondItemNumber === this.targetNumbers[1] &&
               this.firstItemNumber > 0 && 
               this.secondItemNumber > 0;
      }
      if (!this.question.targetNumber) {
        return false;
      }
      const target = Number(this.question.targetNumber);
      return this.currentNumber === target && this.currentNumber > 0;
    },
    rawQuestionTitle() {
      return (this.question && this.question.questionTitle ? this.question.questionTitle : '点击放置木棍') || '';
    },
    displayQuestionTitle() {
      if (!this.wrapTitleByComma) return this.rawQuestionTitle;
      return String(this.rawQuestionTitle).replace(/([，,])\s*/g, '$1\n');
    },
  },
  methods: {
    selectArea(area) {
      this.selectedArea = area;
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

    fetchQuestion() {
      const gamerId = Number(this.gamerId);
      const levelId = Number(this.routeLevelId || this.levelId);
      const trackId = Number(this.trackId);

      if (!gamerId || Number.isNaN(levelId) || levelId <= 0 || Number.isNaN(trackId) || trackId <= 0) {
        const local = {
          question_id: 0,
          targetNumber: 1,
          title: '今日族长笑着对你说：用筹算木棍表示数字 1，你会怎么摆放呢？',
          questionTitle: '点击放置木棍，表示数字1',
          icons: ['🎯'],
        };
        this.question = local;
        this.isTwoDigit = false;
        this.isComboMode = false;
        this.targetNumbers = [];
        this.questionIcons = local.icons || [];
        this.wrapTitleByComma = false;
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
              url: '/pages/track/counting_rods',
            });
          }, 3000);
          return;
        }

        if (!json.result || !json.result.questions || json.result.questions.length === 0) {
          const local = {
            question_id: 0,
            targetNumber: 1,
            title: '今日族长笑着对你说：用筹算木棍表示数字 1，你会怎么摆放呢？',
            questionTitle: '点击放置木棍，表示数字1',
            icons: ['🎯'],
          };
          this.question = local;
          this.isTwoDigit = false;
          this.isComboMode = false;
          this.targetNumbers = [];
          this.questionIcons = local.icons || [];
          this.wrapTitleByComma = false;
          this.showDefaultQuestionTip = true;
          if (this.defaultQuestionTipTimer) {
            clearTimeout(this.defaultQuestionTipTimer);
          }
          this.defaultQuestionTipTimer = setTimeout(() => {
            this.showDefaultQuestionTip = false;
          }, 2000);
          this.playTyping(local.title);
          return;
        }

        this.showDefaultQuestionTip = false;
        const list = json.result.questions;
        const q = list[Math.floor(Math.random() * list.length)];
        let target = 1;
        let targetNumbers = [];
        let elderSpeech = '';
        let questionTitle = '';
        let icons = [];
        let isComboMode = false;

        try {
          if (q.question_content) {
            const content = JSON.parse(q.question_content);
            if (content.options && Array.isArray(content.options) && content.options.length > 0) {
              elderSpeech = content.options[0];
              questionTitle = content.options[0];
            }
            if (content.targetNumber) {
              target = Number(content.targetNumber);
            }
            if (content.decorations && Array.isArray(content.decorations)) {
              icons = content.decorations.map(d => d.icon).filter(icon => icon);
            }
          }

          if (q.correct_answer) {
            try {
              const answer = typeof q.correct_answer === 'string' ? JSON.parse(q.correct_answer) : q.correct_answer;
              if (answer.items && Array.isArray(answer.items)) {
                targetNumbers = answer.items.map(item => Number(item.count)).filter(n => !Number.isNaN(n) && n > 0);
                if (targetNumbers.length >= 2) {
                  isComboMode = true;
                }
              } else if (answer.answer) {
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

        if (isComboMode && targetNumbers.length >= 2) {
          if (!elderSpeech || elderSpeech.trim() === '') {
            elderSpeech = `今日族长笑着对你说：用筹算木棍表示数字 ${targetNumbers.join(' 和 ')}，你会怎么摆放呢？`;
          }
          if (!questionTitle || questionTitle.trim() === '') {
            questionTitle = `${q.question_title || '摆放木棍'}`;
          }
        } else {
          if (!elderSpeech || elderSpeech.trim() === '') {
            elderSpeech = `今日族长笑着对你说：用筹算木棍表示数字 ${target}，你会怎么摆放呢？`;
          }
          if (!questionTitle || questionTitle.trim() === '') {
            questionTitle = `点击放置木棍，表示数字${target}`;
          }
        }

        if (icons.length === 0) {
          icons = ['🎯'];
        }

        const isTwoDigit = !isComboMode && target >= 10;

        this.question = {
          question_id: q.question_id,
          targetNumber: target,
          title: elderSpeech,
          questionTitle: questionTitle,
        };
        this.isTwoDigit = isTwoDigit;
        this.isComboMode = isComboMode;
        this.targetNumbers = targetNumbers;
        this.questionIcons = icons;
        this.wrapTitleByComma = false;
        
        if (isComboMode) {
          this.selectedArea = 'first';
        } else if (isTwoDigit) {
          this.selectedArea = 'tens';
        } else {
          this.selectedArea = 'single';
        }
        
        this.playTyping(this.question.title);
        if (this.showGamePopup) {
          this.$nextTick(() => {
            this.updateTitleWrap();
          });
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
      query.select('.chouSuan-title').boundingClientRect();
      query.select('.chouSuan-title-measure').boundingClientRect();
      query.exec((res) => {
        const titleRect = res && res[0] ? res[0] : null;
        const measureRect = res && res[1] ? res[1] : null;
        if (!titleRect || !measureRect) return;
        const available = Math.max(0, (titleRect.width || 0) - paddingPx);
        this.wrapTitleByComma = (measureRect.width || 0) > available;
      });
    },

    removeSingleHorizontalStick() {
      if (!this.singleHasHorizontalStick) return;
      this.singleHasHorizontalStick = false;
    },

    removeSingleVerticalStick(index) {
      if (index >= 0 && index < this.singleVerticalSticks.length) {
        this.singleVerticalSticks.splice(index, 1);
      }
    },

    removeTensHorizontalStick() {
      if (!this.tensHasHorizontalStick) return;
      this.tensHasHorizontalStick = false;
    },

    removeTensVerticalStick(index) {
      if (index >= 0 && index < this.tensVerticalSticks.length) {
        this.tensVerticalSticks.splice(index, 1);
      }
    },

    removeOnesHorizontalStick() {
      if (!this.onesHasHorizontalStick) return;
      this.onesHasHorizontalStick = false;
    },

    removeOnesVerticalStick(index) {
      if (index >= 0 && index < this.onesVerticalSticks.length) {
        this.onesVerticalSticks.splice(index, 1);
      }
    },

    removeFirstItemHorizontalStick() {
      if (!this.firstItemHasHorizontalStick) return;
      this.firstItemHasHorizontalStick = false;
    },

    removeFirstItemVerticalStick(index) {
      if (index >= 0 && index < this.firstItemVerticalSticks.length) {
        this.firstItemVerticalSticks.splice(index, 1);
      }
    },

    removeSecondItemHorizontalStick() {
      if (!this.secondItemHasHorizontalStick) return;
      this.secondItemHasHorizontalStick = false;
    },

    removeSecondItemVerticalStick(index) {
      if (index >= 0 && index < this.secondItemVerticalSticks.length) {
        this.secondItemVerticalSticks.splice(index, 1);
      }
    },

    resetAnswer() {
      this.singleHasHorizontalStick = false;
      this.singleVerticalSticks = [];
      this.tensHasHorizontalStick = false;
      this.tensVerticalSticks = [];
      this.onesHasHorizontalStick = false;
      this.onesVerticalSticks = [];
      this.firstItemHasHorizontalStick = false;
      this.firstItemVerticalSticks = [];
      this.secondItemHasHorizontalStick = false;
      this.secondItemVerticalSticks = [];
    },

    handleHorizontalStickClick() {
      if (this.isComboMode) {
        if (this.selectedArea === 'first') {
          if (!this.firstItemHasHorizontalStick) {
            this.firstItemHasHorizontalStick = true;
          }
        } else {
          if (!this.secondItemHasHorizontalStick) {
            this.secondItemHasHorizontalStick = true;
          }
        }
      } else if (!this.isTwoDigit) {
        if (!this.singleHasHorizontalStick) {
          this.singleHasHorizontalStick = true;
        }
      } else {
        if (this.selectedArea === 'tens') {
          if (!this.tensHasHorizontalStick) {
            this.tensHasHorizontalStick = true;
          }
        } else {
          if (!this.onesHasHorizontalStick) {
            this.onesHasHorizontalStick = true;
          }
        }
      }
    },

    handleVerticalStickClick() {
      if (this.isComboMode) {
        if (this.selectedArea === 'first') {
          const maxVertical = this.firstItemHasHorizontalStick ? 4 : 5;
          if (this.firstItemVerticalSticks.length < maxVertical) {
            this.firstItemVerticalSticks.push({
              id: Date.now() + '_first_' + this.firstItemVerticalSticks.length
            });
          }
        } else {
          const maxVertical = this.secondItemHasHorizontalStick ? 4 : 5;
          if (this.secondItemVerticalSticks.length < maxVertical) {
            this.secondItemVerticalSticks.push({
              id: Date.now() + '_second_' + this.secondItemVerticalSticks.length
            });
          }
        }
      } else if (!this.isTwoDigit) {
        const maxVertical = this.singleHasHorizontalStick ? 4 : 5;
        if (this.singleVerticalSticks.length < maxVertical) {
          this.singleVerticalSticks.push({
            id: Date.now() + '_single_' + this.singleVerticalSticks.length
          });
        }
      } else {
        if (this.selectedArea === 'tens') {
          const maxVertical = this.tensHasHorizontalStick ? 4 : 5;
          if (this.tensVerticalSticks.length < maxVertical) {
            this.tensVerticalSticks.push({
              id: Date.now() + '_tens_' + this.tensVerticalSticks.length
            });
          }
        } else {
          const maxVertical = this.onesHasHorizontalStick ? 4 : 5;
          if (this.onesVerticalSticks.length < maxVertical) {
            this.onesVerticalSticks.push({
              id: Date.now() + '_ones_' + this.onesVerticalSticks.length
            });
          }
        }
      }
    },

    getTipMessage() {
      if (this.isComboMode) {
        const firstTarget = this.targetNumbers[0] || 0;
        const secondTarget = this.targetNumbers[1] || 0;
        const firstCurrent = this.firstItemNumber;
        const secondCurrent = this.secondItemNumber;
        
        if (firstCurrent === 0 && secondCurrent === 0) {
          return '小朋友试一下怎么摆放，加油~';
        }
        const firstOk = firstCurrent === firstTarget;
        const secondOk = secondCurrent === secondTarget;
        
        if (firstOk && secondOk) {
          return '刚刚好！点“提交答案”看看吧～';
        }

        if (firstCurrent > firstTarget || secondCurrent > secondTarget) {
          return '哎呀放多啦～点一下木棍就能拿走哦';
        }
        return '还差一点点～去下面拿木棍放上来吧';
      }
      
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
      
      let userAnswerStr = '';
      let isCorrectFront = false;
      
      if (this.isComboMode) {
        const firstCount = this.firstItemNumber;
        const secondCount = this.secondItemNumber;
        const firstTarget = this.targetNumbers[0] || 0;
        const secondTarget = this.targetNumbers[1] || 0;
        isCorrectFront = firstCount === firstTarget && secondCount === secondTarget;
        userAnswerStr = JSON.stringify({ 
          items: [
            { decoration: 'item1', count: firstCount },
            { decoration: 'item2', count: secondCount }
          ]
        });
      } else {
        const count = this.currentNumber;
        const target = Number(this.question.targetNumber || 0);
        isCorrectFront = count === target;
        userAnswerStr = JSON.stringify({ answer: String(count) });
      }
      
      const usedTime = (Date.now() - this.startTime) / 1000.0;
      const stars = this.calculateStars(isCorrectFront, usedTime);

      if (!this.gamerId || !this.question.question_id) {
        if (isCorrectFront) {
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
          this.successTime = Math.round(usedTime);
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
        userAnswer: userAnswerStr,
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
          this.successTime = Math.round(usedTime);
          this.showSuccessModal = true;
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
      if (!isCorrect) return 0;
      if (usedTime <= 60) return 3;
      if (usedTime <= 120) return 2;
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

    startNextQuestion() {
      this.resetAnswer();
      this.questionIcons = [];
      this.question = null;
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
      this.$nextTick(() => {
        this.updateTitleWrap();
      });
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
  max-width: 430rpx;
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
  width: 350rpx;
  height: 600rpx;
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

@keyframes iconFloatCorner {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10rpx);
  }
}

@keyframes iconFloatCenter {
  0%, 100% {
    transform: translateY(-50%);
  }
  50% {
    transform: translateY(-50%) translateY(-10rpx);
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
  display: block;
  line-height: 1.4;
}

.chouSuan-title--wrap {
  white-space: pre-line;
}

.chouSuan-title-measure {
  position: fixed;
  left: -9999px;
  top: -9999px;
  font-size: 36rpx;
  font-weight: 700;
  letter-spacing: 1rpx;
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
}

.answer-area {
  display: flex;
  justify-content: center;
  margin: 30rpx 0;
}

.answer-box {
  display: flex;
  align-items: flex-start;
  justify-content: space-around;
  background-color: #fff;
  transition: all 0.3s ease;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  padding: 20rpx;
  position: relative;
  min-height: 280rpx;
  min-width: 500rpx;
  border: 2rpx solid #ddd;
  border-radius: 16rpx;
}

.icon-display-area {
  position: absolute;
  top: 15rpx;
  left: 20rpx;
  width: 80rpx;
  height: 80rpx;
  border: 2rpx solid #ff6b35;
  border-radius: 12rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #fff;
  z-index: 10;
  animation: iconFloatCorner 1.8s ease-in-out infinite;
  will-change: transform;
}

.icon-display-area.single-icon {
  top: 50%;
  left: 20rpx;
  transform: none;
  animation-name: iconFloatCenter;
}

.question-icon-unicode {
  font-size: 50rpx;
  color: #333;
  text-align: center;
}

.single-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;
  position: relative;
  flex: 1;
}

.single-area .area-icon {
  position: absolute;
  top: -10rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 60rpx;
  border: 2rpx solid #ff6b35;
  border-radius: 12rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #fff;
}

.area-value {
  font-size: 32rpx;
  font-weight: 700;
  color: #333;
  margin-top: 10rpx;
}

.digit-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;
  border-radius: 12rpx;
  transition: all 0.3s ease;
  border: 2rpx dashed transparent;
  cursor: pointer;
  position: relative;

  &.selected {
    border-color: #ff6b35;
    background-color: rgba(255, 107, 53, 0.08);
    box-shadow: 0 0 20rpx rgba(255, 107, 53, 0.2);
  }
}

.combo-item-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;
  border-radius: 12rpx;
  transition: all 0.3s ease;
  border: 2rpx dashed transparent;
  cursor: pointer;
  position: relative;

  &.selected {
    border-color: #ff6b35;
    background-color: rgba(255, 107, 53, 0.08);
    box-shadow: 0 0 20rpx rgba(255, 107, 53, 0.2);
  }
  
  &:first-child {
    border-right: 2rpx solid #ddd;
  }
}

.digit-area .area-icon {
  position: absolute;
  top: -10rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 60rpx;
  border: 2rpx solid #ff6b35;
  border-radius: 12rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #fff;
  z-index: 5;
}

.tens-area {
  border-right: 2rpx solid #ddd;
}

.digit-label {
  font-size: 24rpx;
  color: #666;
  font-weight: 600;
  margin-bottom: 10rpx;
}

.answer-base {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;
  width: 160rpx;
  min-height: 160rpx;
  border: 2rpx dashed #ccc;
  border-radius: 12rpx;
  cursor: pointer;
  transition: all 0.2s ease;

  &.selected {
    border-color: #ff6b35;
    border-style: solid;
    background-color: rgba(255, 107, 53, 0.05);
  }
}

.horizontal-stick-display {
  width: 100rpx;
  height: 20rpx;
  background: #8b4513;
  border-radius: 6rpx;
  margin-bottom: 12rpx;
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
  gap: 10rpx;
  justify-content: center;
  margin-top: 8rpx;
}

.vertical-stick-display {
  width: 20rpx;
  height: 90rpx;
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
  
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}
</style>
