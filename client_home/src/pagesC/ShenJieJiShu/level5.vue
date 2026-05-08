<template>
  <view class="rope-level-page">

    <!-- 背景图片 -->
    <image class="background-image" src="/static/img/rope/BeiJing1.png" mode="aspectFill" />

    <!-- 默认题目提示 -->
    <view class="default-question-tip" v-if="showDefaultQuestionTip">
      <text class="tip-text">⚠️ 当前为默认题目，请联系运维人员</text>
    </view>

    <view class="scene">
      <!-- 左侧：族长 -->
      <view class="elder-area">
        <view class="speech-bubble" :class="{ expanded: showFullSpeech }" @click="toggleSpeech">
          <text class="speech-text">{{ displayText }}</text>
        </view>
        <image class="elder-img" src="/static/img/rope/grandpa.png" mode="aspectFit" />
      </view>

      <!-- 右侧：小孩 -->
      <view class="child-area" @click="openGamePopup">
        <view class="child-speech-bubble" v-if="showChildSpeech">
          <text class="child-speech-text">{{ childSpeechText }}</text>
        </view>
        <view class="child-tip" v-if="showChildTip">
          <text class="tip-text">点击这里，来试试吧</text>
        </view>
        <image class="child-img" src="/static/img/rope/child.png" mode="aspectFit" />
      </view>
    </view>

    <!-- 游戏弹窗 -->
    <view class="popup-mask" v-if="showGamePopup" @click="closeGamePopup"></view>
    <view class="game-popup" v-if="showGamePopup" @click.stop>
      <view class="icon-display-area icon-display-area--left" v-if="cornerIcons[0]">
        <text class="question-icon-unicode">{{ cornerIcons[0] }}</text>
      </view>
      <view class="icon-display-area icon-display-area--right" v-if="isComboQuestion && cornerIcons[1]">
        <text class="question-icon-unicode">{{ cornerIcons[1] }}</text>
      </view>

      <!-- 绳结互动区域 -->
      <view class="rope-area" v-if="isComboQuestion">
        <view class="rope-title">
          <text class="rope-title-main" :class="{ 'rope-title--wrap': titleUsePreLine }">{{ displayQuestionTitle }}</text>
          <text class="rope-title-measure">{{ rawQuestionTitle }}</text>
          <br />
          <text style="font-size: 24rpx; font-weight: 400;">
            提示：点击绳子打小结表示1，<text style="font-weight: 700">长按打大结表示10</text>
          </text>
        </view>
        <view
          v-for="(rope, index) in ropes"
          :key="index"
          class="rope-section"
        >
          <view class="rope-header">
            <text class="rope-label">{{ rope.decoration ? getDecorationName(rope.decoration) : '未选择装饰物' }}</text>
            <text class="rope-count">{{ rope.smallKnots.length + rope.bigKnots.length * 10 }} 个</text>
          </view>
          <view class="rope-wrapper" :data-index="index" @click="handleRopeClick" @longpress="handleRopeLongPress">
            <image class="rope-image" src="/static/img/rope/ShengZi.png" mode="widthFix" />
            <view
              v-for="knot in rope.smallKnots"
              :key="knot.id"
              class="rope-knot"
              :class="{ 'knot-animating': knot.animating }"
              :style="{ left: knot.x + '%' }"
            >
              <image class="knot-image" src="/static/img/rope/ShengJie.png" mode="aspectFit" />
              <text class="knot-decoration">{{ getDecorationIcon(rope.decoration) }}</text>
            </view>
            <view
              v-for="big in rope.bigKnots"
              :key="big.id"
              class="rope-knot big-knot"
              :class="{ 'knot-animating': big.animating }"
              :style="{ left: big.x + '%' }"
            >
              <image class="knot-image big-knot-image" src="/static/img/rope/ShengJie.png" mode="aspectFit" />
              <text class="knot-decoration big-knot-decoration">{{ getDecorationIcon(rope.decoration) }}</text>
            </view>
          </view>
        </view>
      </view>
      <view class="rope-area" v-else>
        <view class="rope-title">
          <text class="rope-title-main" :class="{ 'rope-title--wrap': wrapTitleByComma }">{{ displayQuestionTitle }}</text>
          <text class="rope-title-measure">{{ rawQuestionTitle }}</text>
          <br />
          <text style="font-size: 24rpx; font-weight: 400;">
            提示：点击绳子打小结表示1，<text style="font-weight: 700">长按打大结表示10</text>
          </text>
        </view>
        <view
          class="rope-wrapper"
          @click="handleRopeClick"
          @longpress="handleRopeLongPress"
        >
          <image class="rope-image" src="/static/img/rope/ShengZi.png" mode="widthFix" />
          <view
            v-for="knot in smallKnots"
            :key="knot.id"
            class="rope-knot"
            :class="{ 'knot-animating': knot.animating }"
            :style="{ left: knot.x + '%' }"
          >
            <image class="knot-image" src="/static/img/rope/ShengJie.png" mode="aspectFit" />
            <text class="knot-decoration">{{ selectedDecorationIcon }}</text>
          </view>
          <view
            v-for="big in bigKnots"
            :key="big.id"
            class="rope-knot big-knot"
            :class="{ 'knot-animating': big.animating }"
            :style="{ left: big.x + '%' }"
          >
            <image class="knot-image big-knot-image" src="/static/img/rope/ShengJie.png" mode="aspectFit" />
            <text class="knot-decoration big-knot-decoration">{{ selectedDecorationIcon }}</text>
          </view>
        </view>
        <view class="rope-info">
          <view class="rope-info-col rope-info-left">
            <text>小结：{{ smallKnots.length }} 个</text>
            <text>大结：{{ bigKnots.length }} 个</text>
          </view>
          <view class="rope-info-col rope-info-right">
            <text>当前表示数字：{{ currentValue }}</text>
            <text>用时：{{ formatTime(elapsedSeconds) }}</text>
          </view>
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
        >
          提交答案
        </button>
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

    <!-- 错误弹窗 -->
    <view class="error-modal" v-if="showErrorModal" @click.stop>
      <view class="error-content" @click.stop>
        <view class="error-icon">✗</view>
        <view class="error-title">有点小问题哦～</view>
        <view class="error-message">再想一想，你可以的！</view>
        <view class="error-buttons">
          <button class="error-btn retry-btn" @click="retryQuestion">重新尝试</button>
          <button class="error-btn next-btn" @click="skipQuestion">换一题</button>
        </view>
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
      trackId: 1,
      levelId: 5,
      question: null,
      currentQuestionId: null,
      fullText: '今日族长正在思考要出什么题目给你……',
      displayText: '',
      typingTimer: null,
      smallKnots: [],
      bigKnots: [],
      // 组合题目的多根绳子
      ropes: [],
      startTime: 0,
      elapsedSeconds: 0,
      elapsedTimer: null,
      showSuccessModal: false,
      successMessage: '',
      successStarCount: 0,
      successTime: 0,
      // 答错弹窗相关
      showErrorModal: false,
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
      currentDecorations: [],
      selectedDecoration: null,
      wrapTitleByComma: false,
      // 已尝试的题目ID列表
      triedQuestionIds: [],
    };
  },
  onLoad(options) {
    // 从路由参数中接收 gamer_id / level_id / track_id（如果有）
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
    // 是否为组合题目
    isComboQuestion() {
      return this.question && this.question.question_type === 'rope_decoration_combo';
    },
    // 获取目标数字文本
    targetNumberText() {
      if (this.question && this.question.targetNumber) {
        return this.question.targetNumber;
      }
      return '';
    },
    // 当前用小结+大结表示的数值
    currentValue() {
      return this.smallKnots.length + this.bigKnots.length * 10;
    },
    // 判断是否可以提交
    canSubmit() {
      if (!this.question) {
        return false;
      }
      if (this.isComboQuestion) {
        return this.ropes.length > 0 && this.ropes.every(r => (r.smallKnots && r.smallKnots.length > 0) || (r.bigKnots && r.bigKnots.length > 0));
      }
      if (!this.question.targetNumber) {
        return false;
      }
      const target = Number(this.question.targetNumber);
      const current = this.currentValue;
      return Number(current) === Number(target);
    },
    // 选中装饰物的图标
    selectedDecorationIcon() {
      if (!this.selectedDecoration) return '';
      const deco = this.currentDecorations.find(d => d.type === this.selectedDecoration);
      return deco ? deco.icon : '';
    },
    cornerIcons() {
      let icons = [];
      if (this.isComboQuestion) {
        let types = [];
        if (Array.isArray(this.ropes) && this.ropes.length > 0) {
          types = this.ropes.map(r => r.decoration).filter(Boolean);
        } else if (this.question && this.question.correct_answer && Array.isArray(this.question.correct_answer.items)) {
          types = this.question.correct_answer.items.map(i => i.decoration).filter(Boolean);
        }
        icons = types.map(t => this.getDecorationIcon(t)).filter(Boolean);
        if (icons.length === 0 && Array.isArray(this.currentDecorations)) {
          icons = this.currentDecorations.map(d => d.icon).filter(Boolean);
        }
        return icons.slice(0, 2);
      }
      if (Array.isArray(this.currentDecorations) && this.currentDecorations.length >= 2) {
        icons = this.currentDecorations.slice(0, 2).map(d => d.icon).filter(Boolean);
        return icons;
      }
      const single = this.selectedDecorationIcon || (this.currentDecorations && this.currentDecorations[0] ? this.currentDecorations[0].icon : '');
      return single ? [single] : [];
    },
    rawQuestionTitle() {
      const fallback = this.isComboQuestion ? '用不同的绳子记录不同的物品' : '点击绳子打结';
      return (this.question && this.question.questionTitle ? this.question.questionTitle : fallback) || '';
    },
    titleUsePreLine() {
      return this.wrapTitleByComma || /[；;]/.test(String(this.rawQuestionTitle || ''));
    },
    displayQuestionTitle() {
      const raw = String(this.rawQuestionTitle || '');
      if (this.isComboQuestion && /[；;]/.test(raw)) {
        return raw
          .split(/[；;]/)
          .map(s => s.trim())
          .filter(Boolean)
          .join('\n');
      }
      if (!this.wrapTitleByComma) return raw;
      return raw.replace(/([，,])\s*/g, '$1\n');
    },
  },
  methods: {
    // 打字机效果
    playTyping(text) {
      this.fullText = text;
      this.displayText = '';
      this.showFullSpeech = false; // 重置展开状态
      // 重置小孩说话和提示
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
          // 打字机效果完成后，显示小孩说话
          this.displayChildSpeech();
          return;
        }
        this.displayText += this.fullText[index];
        index++;
      }, 80);
    },

    // 显示小孩说话
    displayChildSpeech() {
      this.childSpeechText = '好哒爷爷~，我来试一试';
      this.showChildSpeech = true;
      // 清除之前的提示定时器
      if (this.childTipTimer) {
        clearTimeout(this.childTipTimer);
        this.childTipTimer = null;
      }
      // 清除之前的提示显示
      this.showChildTip = false;
      // 6秒后如果弹窗还没打开，显示提示
      this.childTipTimer = setTimeout(() => {
        if (!this.showGamePopup) {
          this.showChildTip = true;
        }
      }, 6000);
    },

    // 重置并启动计时器
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

    getDecorationName(type) {
      const deco = this.currentDecorations.find(d => d.type === type);
      return deco ? deco.name : '';
    },

    getDecorationIcon(type) {
      const deco = this.currentDecorations.find(d => d.type === type);
      return deco ? deco.icon : '';
    },

    // 从后端获取本关的一道随机题（优先本地随机）
    fetchQuestion() {
      const gamerId = Number(this.gamerId);
      const levelId = Number(this.levelId);
      const trackId = Number(this.trackId);

      if (!gamerId || Number.isNaN(levelId) || levelId <= 0 || Number.isNaN(trackId) || trackId <= 0) {
        const local = {
          question_id: 0,
          question_type: 'rope_decoration',
          title: '今日族长笑着对你说：用大结表示10，小结表示1，你会怎么用绳结表示数字 10 呢？',
          correct_answer: { decoration: 'BeiKe', count: 10 },
          decorations: [{ type: 'BeiKe', name: '贝壳', icon: '🐚' }],
        };
        this.question = local;
        this.currentQuestionId = local.question_id;
        this.currentDecorations = local.decorations;
        this.selectedDecoration = local.decorations[0].type;
        this.wrapTitleByComma = false;
        this.playTyping(local.title);
        return;
      }

      const params = { gamerId, levelId, trackId };
      this.$get('/question_bank/get_level_questions', params, (json) => {
        if (json && json.error) {
          uni.showToast({
            title: json.error.message || '获取题目失败',
            icon: 'none',
          });
          return;
        }

        const allThreeStars = json.result && (json.result.all_three_stars || json.result.allThreeStars);
        if (allThreeStars) {
          setTimeout(() => {
            this.showAllCompleteModal = true;
            setTimeout(() => {
              this.goToNextLevel();
            }, 3000);
          }, 1000);
          return;
        }

        if (!json.result || !json.result.questions || json.result.questions.length === 0) {
          const local = {
            question_id: 0,
            question_type: 'rope_decoration',
            title: '今日族长笑着对你说：用大结表示10，小结表示1，你会怎么用绳结表示数字 10 呢？',
            correct_answer: { decoration: 'BeiKe', count: 10 },
            decorations: [{ type: 'BeiKe', name: '贝壳', icon: '🐚' }],
          };
          this.question = local;
          this.currentQuestionId = local.question_id;
          this.currentDecorations = local.decorations;
          this.selectedDecoration = local.decorations[0].type;
          this.wrapTitleByComma = false;
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
        let availableList = list.filter(item => !this.triedQuestionIds.includes(item.question_id));

        if (availableList.length === 0) {
          this.triedQuestionIds = [];
          availableList = list;
        }

        if (availableList.length === 0) {
          availableList = list;
        }

        const q = availableList[Math.floor(Math.random() * availableList.length)];

        let content = {};
        let answer = {};
        let elderSpeech = '';
        let questionTitle = '';
        let target = 10;
        let decorations = [{ type: 'BeiKe', name: '贝壳', icon: '🐚' }];

        try {
          content = typeof q.question_content === 'string' ? JSON.parse(q.question_content) : q.question_content;
          answer = typeof q.correct_answer === 'string' ? JSON.parse(q.correct_answer) : q.correct_answer;

          if (content.options && content.options[0]) {
            elderSpeech = (q.question_title || '') + "," + content.options[0];
            questionTitle = content.options[0];
          }

          if (content.decorations && Array.isArray(content.decorations)) {
            decorations = content.decorations;
          }

          if (answer.count) {
            target = Number(answer.count);
          } else if (answer.answer) {
            target = Number(answer.answer);
          }
        } catch (e) {
          console.error('解析题目JSON失败', e);
        }

        if (!elderSpeech || elderSpeech.trim() === '') {
          elderSpeech = '请完成题目';
        }

        if (!questionTitle || questionTitle.trim() === '') {
          questionTitle = '请完成题目';
        }

        this.question = {
          question_id: q.question_id,
          question_type: q.question_type,
          title: elderSpeech,
          questionTitle: questionTitle,
          targetNumber: target,
          correct_answer: answer,
          decorations: decorations,
        };
        this.currentQuestionId = q.question_id;

        if (!this.triedQuestionIds.includes(q.question_id)) {
          this.triedQuestionIds.push(q.question_id);
        }

        this.currentDecorations = this.question.decorations;

        if (this.question.question_type === 'rope_decoration_combo') {
          this.ropes = Array.isArray(answer.items)
            ? answer.items.map(item => ({
                decoration: item.decoration,
                smallKnots: [],
                bigKnots: [],
              }))
            : [];
          if (this.currentDecorations.length > 0) {
            this.selectedDecoration = this.currentDecorations[0].type;
          }
        } else {
          this.smallKnots = [];
          this.bigKnots = [];
          if (this.currentDecorations.length > 0) {
            this.selectedDecoration = this.currentDecorations[0].type;
          }
        }

        this.wrapTitleByComma = false;
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
      query.select('.rope-title').boundingClientRect();
      query.select('.rope-title-measure').boundingClientRect();
      query.exec((res) => {
        const titleRect = res && res[0] ? res[0] : null;
        const measureRect = res && res[1] ? res[1] : null;
        if (!titleRect || !measureRect) return;
        const available = Math.max(0, (titleRect.width || 0) - paddingPx);
        this.wrapTitleByComma = (measureRect.width || 0) > available;
      });
    },

    // 计算点击位置（公共逻辑）
    computeClickPercent(e, callback) {
      const query = uni.createSelectorQuery().in(this);
      query
        .select('.rope-wrapper')
        .boundingClientRect((rect) => {
          if (!rect) return;
          const x = e.detail.x - rect.left;
          let percent = 0;
          if (rect.width > 0) {
            percent = (x / rect.width) * 100;
          }
          percent = Math.max(5, Math.min(95, percent));
          callback(percent);
        })
        .exec();
    },

    // 单一题目：点击绳子打小结（1）或解结
    handleSingleRopeClick(e) {
      this.computeClickPercent(e, (percent) => {
        const threshold = 5;
        const idx = this.smallKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (idx !== -1) {
          this.smallKnots.splice(idx, 1);
          return;
        }
        const bigIdx = this.bigKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (bigIdx !== -1) {
          this.bigKnots.splice(bigIdx, 1);
          return;
        }
        if (this.smallKnots.length + this.bigKnots.length < 12) {
          const newKnot = {
            id: Date.now() + '_' + this.smallKnots.length,
            x: percent,
            animating: true,
          };
          this.smallKnots.push(newKnot);
          setTimeout(() => {
            const idx2 = this.smallKnots.findIndex((k) => k.id === newKnot.id);
            if (idx2 !== -1) {
              this.$set(this.smallKnots[idx2], 'animating', false);
            }
          }, 300);
        }
      });
    },

    // 单一题目：长按绳子打大结（10）或解结
    handleSingleRopeLongPress(e) {
      this.computeClickPercent(e, (percent) => {
        const threshold = 5;
        const bigIdx = this.bigKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (bigIdx !== -1) {
          this.bigKnots.splice(bigIdx, 1);
          return;
        }
        const idx = this.smallKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (idx !== -1) {
          this.smallKnots.splice(idx, 1);
          return;
        }
        if (this.smallKnots.length + this.bigKnots.length < 12) {
          const newBig = {
            id: Date.now() + '_big_' + this.bigKnots.length,
            x: percent,
            animating: true,
          };
          this.bigKnots.push(newBig);
          setTimeout(() => {
            const idx2 = this.bigKnots.findIndex((k) => k.id === newBig.id);
            if (idx2 !== -1) {
              this.$set(this.bigKnots[idx2], 'animating', false);
            }
          }, 300);
        }
      });
    },

    // 组合题目：点击某根绳子打小结（1）或解结
    handleRopeClick(e) {
      const ropeIndex = e.currentTarget.dataset.index;
      if (ropeIndex === undefined) {
        this.handleSingleRopeClick(e);
        return;
      }

      const rope = this.ropes[ropeIndex];
      if (!rope || !rope.decoration) {
        return;
      }

      this.computeClickPercent(e, (percent) => {
        const threshold = 5;
        const idx = rope.smallKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (idx !== -1) {
          rope.smallKnots.splice(idx, 1);
          return;
        }
        const bigIdx = rope.bigKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (bigIdx !== -1) {
          rope.bigKnots.splice(bigIdx, 1);
          return;
        }
        if (rope.smallKnots.length + rope.bigKnots.length < 12) {
          const newKnot = {
            id: Date.now() + '_' + rope.smallKnots.length,
            x: percent,
            animating: true,
          };
          rope.smallKnots.push(newKnot);
          setTimeout(() => {
            const idx2 = rope.smallKnots.findIndex((k) => k.id === newKnot.id);
            if (idx2 !== -1) {
              this.$set(rope.smallKnots, idx2, { ...rope.smallKnots[idx2], animating: false });
            }
          }, 300);
        }
      });
    },

    // 组合题目：长按某根绳子打大结（10）或解结
    handleRopeLongPress(e) {
      const ropeIndex = e.currentTarget.dataset.index;
      if (ropeIndex === undefined) {
        this.handleSingleRopeLongPress(e);
        return;
      }

      const rope = this.ropes[ropeIndex];
      if (!rope || !rope.decoration) {
        return;
      }

      this.computeClickPercent(e, (percent) => {
        const threshold = 5;
        const bigIdx = rope.bigKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (bigIdx !== -1) {
          rope.bigKnots.splice(bigIdx, 1);
          return;
        }
        const idx = rope.smallKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (idx !== -1) {
          rope.smallKnots.splice(idx, 1);
          return;
        }
        if (rope.smallKnots.length + rope.bigKnots.length < 12) {
          const newBig = {
            id: Date.now() + '_big_' + rope.bigKnots.length,
            x: percent,
            animating: true,
          };
          rope.bigKnots.push(newBig);
          setTimeout(() => {
            const idx2 = rope.bigKnots.findIndex((k) => k.id === newBig.id);
            if (idx2 !== -1) {
              this.$set(rope.bigKnots, idx2, { ...rope.bigKnots[idx2], animating: false });
            }
          }, 300);
        }
      });
    },

    // 提交答案
    submitAnswer() {
      const actualTime = Math.round((Date.now() - this.startTime) / 1000);

      let userAnswer;
      if (this.isComboQuestion) {
        userAnswer = {
          items: this.ropes.map(rope => ({
            decoration: rope.decoration,
            count: rope.smallKnots.length + rope.bigKnots.length * 10,
          })),
        };
      } else {
        userAnswer = {
          decoration: this.selectedDecoration,
          count: this.currentValue,
        };
      }

      const params = {
        gamerId: this.gamerId,
        questionId: this.question.question_id,
        trackId: this.trackId,
        levelId: this.levelId,
        userAnswer: JSON.stringify(userAnswer),
        answerTime: actualTime,
      };

      this.$post('/question_bank/submit_answer', params, (json) => {
        let isCorrect = false;
        if (json && json.result) {
          const result = json.result;
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
          const starCount = this.calculateStars(true, actualTime);

          let encouragement = '';
          if (starCount === 3) {
            encouragement = '太厉害了！1分钟内完成，获得3颗星！';
          } else if (starCount === 2) {
            encouragement = '不错！2分钟内完成，获得2颗星！';
          } else if (starCount === 1) {
            encouragement = '很好！完成了题目，获得1颗星！';
          } else {
            encouragement = '答对了！继续努力，争取获得更多星星！';
          }

          this.showGamePopup = false;
          this.successMessage = encouragement;
          this.successStarCount = starCount;
          this.successTime = Math.round(actualTime);
          this.showSuccessModal = true;
        } else {
          this.showGamePopup = false;
          this.showErrorModal = true;
        }
      });
    },

    retryQuestion() {
      this.showErrorModal = false;
      if (this.isComboQuestion) {
        this.ropes.forEach(rope => {
          rope.smallKnots = [];
          rope.bigKnots = [];
        });
      } else {
        this.smallKnots = [];
        this.bigKnots = [];
      }
      this.resetTimer();
    },

    skipQuestion() {
      this.showErrorModal = false;
      this.showGamePopup = false;
      this.smallKnots = [];
      this.bigKnots = [];
      this.ropes = [];
      this.selectedDecoration = null;
      this.currentDecorations = [];
      this.resetTimer();
      this.showFullSpeech = false;
      this.showChildSpeech = false;
      this.showChildTip = false;
      this.childSpeechText = '';
      if (this.childTipTimer) {
        clearTimeout(this.childTipTimer);
        this.childTipTimer = null;
      }
      this.fetchNextQuestion();
    },

    fetchNextQuestion() {
      const gamerId = Number(this.gamerId);
      const levelId = Number(this.levelId);
      const trackId = Number(this.trackId);

      if (!gamerId || Number.isNaN(levelId) || levelId <= 0 || Number.isNaN(trackId) || trackId <= 0) {
        const local = {
          question_id: 0,
          question_type: 'rope_decoration',
          title: '今日族长笑着对你说：用大结表示10，小结表示1，你会怎么用绳结表示数字 10 呢？',
          correct_answer: { decoration: 'BeiKe', count: 10 },
          decorations: [{ type: 'BeiKe', name: '贝壳', icon: '🐚' }],
        };
        this.question = local;
        this.currentQuestionId = local.question_id;
        this.currentDecorations = local.decorations;
        this.selectedDecoration = local.decorations[0].type;
        this.wrapTitleByComma = false;
        this.playTyping(local.title);
        return;
      }

      const params = { gamerId, levelId, trackId };
      this.$get('/question_bank/get_next_question', params, (json) => {
        const allThreeStars = json.result && (json.result.all_three_stars || json.result.allThreeStars);
        if (allThreeStars) {
          setTimeout(() => {
            this.showAllCompleteModal = true;
            setTimeout(() => {
              this.goToNextLevel();
            }, 3000);
          }, 1000);
          return;
        }

        if (json.result && json.result.questions && json.result.questions.length > 0) {
          const list = json.result.questions;
          const q = list[0];

          let content = {};
          let answer = {};
          let elderSpeech = '';
          let questionTitle = '';
          try {
            content = typeof q.question_content === 'string' ? JSON.parse(q.question_content) : q.question_content;
            answer = typeof q.correct_answer === 'string' ? JSON.parse(q.correct_answer) : q.correct_answer;

            if (content.options && content.options[0]) {
              elderSpeech = (q.question_title || '') + "," + content.options[0];
              questionTitle = content.options[0];
            }
          } catch (e) {
            console.error('解析题目JSON失败', e);
          }

          if (!elderSpeech || elderSpeech.trim() === '') {
            elderSpeech = '请完成题目';
          }

          if (!questionTitle || questionTitle.trim() === '') {
            questionTitle = '请完成题目';
          }

          this.question = {
            question_id: q.question_id,
            question_type: q.question_type,
            title: elderSpeech,
            questionTitle: questionTitle,
            correct_answer: answer,
            decorations: content.decorations || [],
          };
          this.currentQuestionId = q.question_id;

          if (!this.triedQuestionIds.includes(q.question_id)) {
            this.triedQuestionIds.push(q.question_id);
          }

          this.currentDecorations = this.question.decorations;

          if (this.question.question_type === 'rope_decoration_combo') {
            this.ropes = Array.isArray(answer.items)
              ? answer.items.map(item => ({
                  decoration: item.decoration,
                  smallKnots: [],
                  bigKnots: [],
                }))
              : [];
            if (this.currentDecorations.length > 0) {
              this.selectedDecoration = this.currentDecorations[0].type;
            }
          } else {
            this.smallKnots = [];
            this.bigKnots = [];
            if (this.currentDecorations.length > 0) {
              this.selectedDecoration = this.currentDecorations[0].type;
            }
          }

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

    // 根据是否答对 + 用时计算星级
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

    // 格式化时间显示
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

    // 关闭成功弹窗并刷新题目
    closeSuccessModal() {
      this.showSuccessModal = false;
      this.startNextQuestion();
    },

    // 关闭所有题目满3星弹窗
    closeAllCompleteModal() {
      this.showAllCompleteModal = false;
    },

    // 切到下一题：清空绳结、重置计时器并重新拉题
    startNextQuestion() {
      this.smallKnots = [];
      this.bigKnots = [];
      this.ropes = [];
      this.selectedDecoration = null;
      this.currentDecorations = [];
      this.triedQuestionIds = [];
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

    // 切换说话气泡展开/收起
    toggleSpeech() {
      this.showFullSpeech = !this.showFullSpeech;
    },

    // 打开游戏弹窗
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

    // 关闭游戏弹窗
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

/* 默认题目提示样式 */
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
  width: 350rpx;
  height: 470rpx;
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
  width: 350rpx;
  height: 340rpx;
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

.rope-title-main {
  display: block;
  line-height: 1.4;
}

.rope-title--wrap {
  white-space: pre-line;
}

.rope-title-measure {
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

.rope-wrapper {
  position: relative;
  width: 100%;
  height: 120rpx;
  margin: 10rpx 0;
  cursor: pointer;
  /* 与按钮同宽：game-popup 内容区 520rpx */
}

.icon-display-area {
  position: absolute;
  top: 30rpx;
  left: 30rpx;
  width: 80rpx;
  height: 80rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
  z-index: 20;
  pointer-events: none;
  animation: bounce 1.8s ease-in-out infinite;
  will-change: transform;
}

.question-icon-unicode {
  font-size: 50rpx;
}

.rope-image {
  position: absolute;
  left: 0;
  top: 50%;
  width: 100%;
  height: 50rpx;
  transform: translateY(-50%);
  object-fit: contain;
  object-position: center;
}

/* 绳结与绳子精确对齐：使用相同垂直参考线，微调上移贴合绳子 */
.rope-knot {
  position: absolute;
  top: 50%;
  left: 0;
  width: 12%;
  min-width: 64rpx;
  max-width: 88rpx;
  height: 80rpx;
  transform: translate(-50%, calc(-50% - 14rpx));
  transform-origin: center center;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}

.knot-image {
  width: 100%;
  height: 100%;
  transform: rotate(90deg);
  object-fit: contain;
}

/* 绳结装饰物样式 */
.knot-decoration {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 32rpx;
  z-index: 10;
  pointer-events: none;
  filter: drop-shadow(0 2rpx 4rpx rgba(0, 0, 0, 0.2));
}

/* 大结装饰物样式 */
.big-knot-decoration {
  font-size: 48rpx;
}

/* 绳结出现动画：从小到大的打结过程（.knot-image 的 transform 独立于 .rope-knot） */
.rope-knot.knot-animating .knot-image {
  animation: knotTieIn 0.4s ease-out forwards;
}

@keyframes knotTieIn {
  0% {
    transform: rotate(90deg) scale(0.2);
    opacity: 0;
  }
  50% {
    transform: rotate(90deg) scale(1.15);
    opacity: 1;
  }
  100% {
    transform: rotate(90deg) scale(1);
    opacity: 1;
  }
}

.rope-info {
  margin-top: 10rpx;
  padding: 0 40rpx;
  font-size: 26rpx;
  color: #2c2c2c;
  font-weight: 600;
  text-shadow: 0 1rpx 3rpx rgba(255, 255, 255, 0.8);
  -webkit-font-smoothing: antialiased;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 30rpx;
}

.rope-info-col {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.rope-info-right {
  align-items: flex-end;
  text-align: right;
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
  /* 醒目的橙色 */
  background: linear-gradient(135deg, #ff8c42 0%, #ff6b35 50%, #ff5722 100%);
  color: #fff;
  box-shadow: 0 6rpx 20rpx rgba(255, 107, 53, 0.4);
  transition: all 0.3s ease;
  cursor: pointer;
}

/* hover效果 - 轻微缩放和阴影变化 */
.btn-submit:not(.btn-disabled):hover {
  transform: scale(1.05);
  box-shadow: 0 8rpx 24rpx rgba(255, 107, 53, 0.5);
}

/* 点击效果 - 轻微缩放和阴影变化 */
.btn-submit:not(.btn-disabled):active {
  transform: scale(0.98);
  box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.3);
}

/* 禁用状态 - 半透明 */
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

/* 成功弹窗样式 */
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
  z-index: 20000;
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

/* 所有题目满3星弹窗特殊样式 */
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

/* 游戏弹窗遮罩层 */
.popup-mask {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 10000;
}

/* 游戏弹窗 */
.game-popup {
  position: fixed;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 10001;

  width: 600rpx;
  padding: 40rpx;
  border-radius: 24rpx;

  /* 核心：磨砂玻璃背景 - 降低模糊，提高背景不透明度 */
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10rpx);
  -webkit-backdrop-filter: blur(10rpx);

  border: 1rpx solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
  position: relative;
}

/* 第二关额外：大结样式（10） */
.big-knot {
  width: 18%;
  min-width: 90rpx;
  max-width: 120rpx;
  height: 110rpx;
}

.big-knot-image {
  transform: rotate(90deg) scale(1.2);
}

.icon-display-area--right {
  left: auto;
  right: 30rpx;
}

.rope-section {
  margin-bottom: 20rpx;
}

.rope-header {
  display: flex;
  justify-content: space-between;
  padding: 0 20rpx;
  margin-bottom: 8rpx;
  font-size: 26rpx;
  font-weight: 600;
}

.rope-label {
  color: #5a4a3a;
}

.rope-count {
  color: #ff6b35;
}

/* 错误弹窗 */
.error-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 20000;
}

.error-content {
  width: 80%;
  max-width: 500rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 60rpx 40rpx 40rpx;
  text-align: center;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.2);
}

.error-icon {
  width: 100rpx;
  height: 100rpx;
  line-height: 100rpx;
  margin: 0 auto 30rpx;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  color: #fff;
  font-size: 60rpx;
  border-radius: 50%;
  box-shadow: 0 4rpx 12rpx rgba(255, 107, 107, 0.3);
}

.error-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 20rpx;
}

.error-message {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 40rpx;
}

.error-buttons {
  display: flex;
  gap: 20rpx;
  justify-content: center;
}

.error-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  border: none;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 600;
}

.retry-btn {
  background: linear-gradient(135deg, #4caf50 0%, #66bb6a 100%);
  color: #fff;
  box-shadow: 0 4rpx 12rpx rgba(76, 175, 80, 0.3);
}

.next-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
}
</style>

