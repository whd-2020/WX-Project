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
        <view class="speech-bubble" :class="{ expanded: showFullSpeech }" @click="toggleSpeech">
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
          <text class="tip-text">点击这里，来挑战吧</text>
        </view>
        <image class="child-img" src="/static/img/rope/XiaoHai.png" mode="aspectFit" />
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
        <!-- 组合题目：显示多根绳子 -->
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
            <!-- 小结（1） -->
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
            <!-- 大结（10） -->
            <view
              v-for="big in rope.bigKnots"
              :key="big.id"
              class="rope-knot big-knot"
              :class="{ 'knot-animating': big.animating }"
              :style="{ left: big.x + '%' }"
            >
              <image class="knot-image big-knot-image" src="/static/img/rope/ShengJie.png" mode="aspectFit" />
              <text class="knot-decoration">{{ getDecorationIcon(rope.decoration) }}</text>
            </view>
          </view>
        </view>
      </view>
      <view class="rope-area" v-else>
        <!-- 单一题目：显示一根绳子 -->
        <view class="rope-title">
          <text class="rope-title-main" :class="{ 'rope-title--wrap': titleUsePreLine }">{{ displayQuestionTitle }}</text>
          <text class="rope-title-measure">{{ rawQuestionTitle }}</text>
          <br />
          <text style="font-size: 24rpx; font-weight: 400;">
            提示：点击绳子打小结表示1，<text style="font-weight: 700">长按打大结表示10</text>
          </text>
        </view>
        <view class="rope-wrapper" @click="handleSingleRopeClick" @longpress="handleSingleRopeLongPress">
          <image class="rope-image" src="/static/img/rope/ShengZi.png" mode="widthFix" />
          <!-- 小结（1） -->
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
          <!-- 大结（10） -->
          <view
            v-for="big in bigKnots"
            :key="big.id"
            class="rope-knot big-knot"
            :class="{ 'knot-animating': big.animating }"
            :style="{ left: big.x + '%' }"
          >
            <image class="knot-image big-knot-image" src="/static/img/rope/ShengJie.png" mode="aspectFit" />
            <text class="knot-decoration">{{ selectedDecorationIcon }}</text>
          </view>
        </view>
        <view class="rope-info">
          <view class="rope-info-col rope-info-left">
            <text>小结：{{ smallKnots.length }} 个</text>
            <text>大结：{{ bigKnots.length }} 个</text>
          </view>
          <view class="rope-info-col rope-info-right">
            <text>当前表示数字：{{ currentValue }}</text>
            <text class="time-text">用时：{{ formatTime(elapsedSeconds) }}</text>
          </view>
        </view>
      </view>

      <!-- 底部操作区 -->
      <view class="bottom-bar">
        <button
          class="btn-submit"
          type="primary"
          @click="submitAnswer"
        >
          提交答案
        </button>
      </view>
    </view>

    <!-- 答错提示弹窗 -->
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
      trackId: 1,
      levelId: 6,
      question: null,
      currentQuestionId: null,
      fullText: '今日族长正在思考要出什么题目给你……',
      displayText: '',
      typingTimer: null,
      // 单一题目的绳结：小结（1）和大结（10）
      smallKnots: [],
      bigKnots: [],
      // 组合题目的多根绳子
      ropes: [],
      // 当前选中的装饰物类型
      selectedDecoration: null,
      // 当前题目的装饰物列表
      currentDecorations: [],
      wrapTitleByComma: false,
      startTime: 0,
      elapsedSeconds: 0,
      elapsedTimer: null,
      // 成功弹窗相关
      showSuccessModal: false,
      successMessage: '',
      successStarCount: 0,
      successTime: 0,
      // 答错弹窗相关
      showErrorModal: false,
      // 所有题目满3星弹窗
      showAllCompleteModal: false,
      // 是否显示默认题目提示
      showDefaultQuestionTip: false,
      // 默认题目提示定时器
      defaultQuestionTipTimer: null,
      // 是否展开显示全部说话内容
      showFullSpeech: false,
      // 是否显示游戏弹窗
      showGamePopup: false,
      // 小孩说话相关
      showChildSpeech: false,
      childSpeechText: '',
      childSpeechTimer: null,
      // 小孩提示相关
      showChildTip: false,
      childTipTimer: null,
      // 已尝试的题目ID列表，用于换一题功能
      triedQuestionIds: [],
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
    // 选中装饰物的名称
    selectedDecorationName() {
      if (!this.selectedDecoration) return '';
      const deco = this.currentDecorations.find(d => d.type === this.selectedDecoration);
      return deco ? deco.name : '';
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
    // 当前用小结+大结表示的数值
    currentValue() {
      return this.smallKnots.length + this.bigKnots.length * 10;
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

    // 显示小孩说话
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

    // 从后端获取题目
    fetchQuestion() {
      const gamerId = Number(this.gamerId);
      const levelId = Number(this.levelId);
      const trackId = Number(this.trackId);

      if (!gamerId || Number.isNaN(levelId) || levelId <= 0 || Number.isNaN(trackId) || trackId <= 0) {
        const local = {
          question_id: 0,
          question_type: 'rope_decoration',
          title: '今日族长笑着对你说：今天打到了1头鹿，用绳结+鹿皮标记记录下来吧',
          correct_answer: { decoration: 'LuPi', count: 1 },
          decorations: [{ type: 'LuPi', name: '鹿皮', icon: '🦌' }],
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
          // 过滤掉已经尝试过的题目
          let availableList = list.filter(item => !this.triedQuestionIds.includes(item.question_id));
          
          // 如果所有题目都尝试过了，清空尝试列表
          if (availableList.length === 0) {
            this.triedQuestionIds = [];
            availableList = list;
          }
          
          // 如果只有一个题目，就直接使用
          if (availableList.length === 0) {
            availableList = list;
          }
          
          const q = availableList[Math.floor(Math.random() * availableList.length)];

          // 解析题目内容
          let content = {};
          let answer = {};
          let elderSpeech = '';
          let questionTitle = '';
          try {
            content = typeof q.question_content === 'string' ? JSON.parse(q.question_content) : q.question_content;
            answer = typeof q.correct_answer === 'string' ? JSON.parse(q.correct_answer) : q.correct_answer;

            // 拼接：question_title + options[0]
            if (content.options && content.options[0]) {
              elderSpeech = (q.question_title || '') + "," + content.options[0];
              // 保存 options[0] 用于弹窗标题
              questionTitle = content.options[0];
            }
          } catch (e) {
            console.error('解析题目JSON失败', e);
          }

          // 如果拼接后为空，使用默认文案
          if (!elderSpeech || elderSpeech.trim() === '') {
            elderSpeech = '请完成题目';
          }

          // 如果 questionTitle 为空，使用默认标题
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
          
          // 将当前题目ID添加到已尝试列表
          if (!this.triedQuestionIds.includes(q.question_id)) {
            this.triedQuestionIds.push(q.question_id);
          }

          this.currentDecorations = this.question.decorations;

          // 初始化绳子
          if (this.question.question_type === 'rope_decoration_combo') {
            // 组合题目：根据答案初始化多根绳子，每根绳子预设对应的装饰物类型
            this.ropes = Array.isArray(answer.items)
              ? answer.items.map(item => ({
                  decoration: item.decoration,
                  smallKnots: [],
                  bigKnots: [],
                }))
              : [];
            // 默认选中第一个装饰物
            if (this.currentDecorations.length > 0) {
              this.selectedDecoration = this.currentDecorations[0].type;
            }
          } else {
            // 单一题目：默认选中唯一的装饰物
            this.knots = [];
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

    updateTitleWrap() {
      if (!this.showGamePopup) return;
      const title = String(this.rawQuestionTitle || '');
      if (/[；;]/.test(title) || !/[，,]/.test(title)) {
        this.wrapTitleByComma = false;
        return;
      }
      const query = uni.createSelectorQuery().in(this);
      query.select('.rope-title').boundingClientRect();
      query.select('.rope-title-measure').boundingClientRect();
      query.exec((res) => {
        const titleRect = res && res[0] ? res[0] : null;
        const measureRect = res && res[1] ? res[1] : null;
        if (!titleRect || !measureRect) return;
        const available = Math.max(0, titleRect.width || 0);
        this.wrapTitleByComma = (measureRect.width || 0) > available;
      });
    },

    // 选择装饰物
    selectDecoration(type) {
      this.selectedDecoration = type;
    },

    // 获取装饰物名称
    getDecorationName(type) {
      const deco = this.currentDecorations.find(d => d.type === type);
      return deco ? deco.name : '';
    },

    // 获取装饰物图标
    getDecorationIcon(type) {
      const deco = this.currentDecorations.find(d => d.type === type);
      return deco ? deco.icon : '';
    },

    // 计算点击位置（参考第二关）
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
      if (!this.selectedDecoration) {
        uni.showToast({ title: '请先选择装饰物', icon: 'none' });
        return;
      }

      this.computeClickPercent(e, (percent) => {
        const threshold = 5;
        // 先看附近有没有小结，有就解小结
        const idx = this.smallKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (idx !== -1) {
          this.smallKnots.splice(idx, 1);
          return;
        }
        // 再看附近有没有大结，有就解大结
        const bigIdx = this.bigKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (bigIdx !== -1) {
          this.bigKnots.splice(bigIdx, 1);
          return;
        }
        // 否则新增一个小结
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
      });
    },

    // 单一题目：长按绳子打大结（10）或解结
    handleSingleRopeLongPress(e) {
      if (!this.selectedDecoration) {
        uni.showToast({ title: '请先选择装饰物', icon: 'none' });
        return;
      }

      this.computeClickPercent(e, (percent) => {
        const threshold = 5;
        // 先看附近有没有大结，有就解大结
        const bigIdx = this.bigKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (bigIdx !== -1) {
          this.bigKnots.splice(bigIdx, 1);
          return;
        }
        // 再看附近有没有小结，有就解小结
        const idx = this.smallKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (idx !== -1) {
          this.smallKnots.splice(idx, 1);
          return;
        }
        // 否则新增一个大结
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
      });
    },

    // 组合题目：点击某根绳子打小结（1）或解结
    handleRopeClick(e) {
      // 从事件中获取绳子索引
      const ropeIndex = e.currentTarget.dataset.index;
      if (ropeIndex === undefined) return;

      const rope = this.ropes[ropeIndex];
      if (!rope.decoration) {
        uni.showToast({ title: '绳子装饰物未设置', icon: 'none' });
        return;
      }

      this.computeClickPercent(e, (percent) => {
        const threshold = 5;
        // 先看附近有没有小结，有就解小结
        const idx = rope.smallKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (idx !== -1) {
          rope.smallKnots.splice(idx, 1);
          return;
        }
        // 再看附近有没有大结，有就解大结
        const bigIdx = rope.bigKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (bigIdx !== -1) {
          rope.bigKnots.splice(bigIdx, 1);
          return;
        }
        // 否则新增一个小结
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
      });
    },

    // 组合题目：长按某根绳子打大结（10）或解结
    handleRopeLongPress(e) {
      // 从事件中获取绳子索引
      const ropeIndex = e.currentTarget.dataset.index;
      if (ropeIndex === undefined) return;

      const rope = this.ropes[ropeIndex];
      if (!rope.decoration) {
        uni.showToast({ title: '绳子装饰物未设置', icon: 'none' });
        return;
      }

      this.computeClickPercent(e, (percent) => {
        const threshold = 5;
        // 先看附近有没有大结，有就解大结
        const bigIdx = rope.bigKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (bigIdx !== -1) {
          rope.bigKnots.splice(bigIdx, 1);
          return;
        }
        // 再看附近有没有小结，有就解小结
        const idx = rope.smallKnots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
        if (idx !== -1) {
          rope.smallKnots.splice(idx, 1);
          return;
        }
        // 否则新增一个大结
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
          // 答错了：显示错误弹窗
          this.showErrorModal = true;
        }
      });
    },

    // 重新尝试（清空绳结，重新开始）
    retryQuestion() {
      this.showErrorModal = false;
      // 清空绳结
      if (this.isComboQuestion) {
        this.ropes.forEach(rope => {
          rope.smallKnots = [];
          rope.bigKnots = [];
        });
      } else {
        this.smallKnots = [];
        this.bigKnots = [];
      }
      // 重置计时器
      this.resetTimer();
    },

    // 换一题
    skipQuestion() {
      this.showErrorModal = false;
      // 先关闭游戏弹窗，再加载新题目
      this.showGamePopup = false;
      // 清空当前题目数据
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
      // 调用新的接口获取下一题
      this.fetchNextQuestion();
    },

    // 从后端获取下一题（用于换一题功能）
    fetchNextQuestion() {
      const gamerId = Number(this.gamerId);
      const levelId = Number(this.levelId);
      const trackId = Number(this.trackId);

      if (!gamerId || Number.isNaN(levelId) || levelId <= 0 || Number.isNaN(trackId) || trackId <= 0) {
        const local = {
          question_id: 0,
          question_type: 'rope_decoration',
          title: '今日族长笑着对你说：今天打到了1头鹿，用绳结+鹿皮标记记录下来吧',
          correct_answer: { decoration: 'LuPi', count: 1 },
          decorations: [{ type: 'LuPi', name: '鹿皮', icon: '🦌' }],
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

          // 解析题目内容
          let content = {};
          let answer = {};
          let elderSpeech = '';
          let questionTitle = '';
          try {
            content = typeof q.question_content === 'string' ? JSON.parse(q.question_content) : q.question_content;
            answer = typeof q.correct_answer === 'string' ? JSON.parse(q.correct_answer) : q.correct_answer;

            // 拼接：question_title + options[0]
            if (content.options && content.options[0]) {
              elderSpeech = (q.question_title || '') + "," + content.options[0];
              // 保存 options[0] 用于弹窗标题
              questionTitle = content.options[0];
            }
          } catch (e) {
            console.error('解析题目JSON失败', e);
          }

          // 如果拼接后为空，使用默认文案
          if (!elderSpeech || elderSpeech.trim() === '') {
            elderSpeech = '请完成题目';
          }

          // 如果 questionTitle 为空，使用默认标题
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
          
          // 将当前题目ID添加到已尝试列表
          if (!this.triedQuestionIds.includes(q.question_id)) {
            this.triedQuestionIds.push(q.question_id);
          }

          this.currentDecorations = this.question.decorations;

          // 初始化绳子
          if (this.question.question_type === 'rope_decoration_combo') {
            // 组合题目：根据答案初始化多根绳子，每根绳子预设对应的装饰物类型
            this.ropes = Array.isArray(answer.items)
              ? answer.items.map(item => ({
                  decoration: item.decoration,
                  smallKnots: [],
                  bigKnots: [],
                }))
              : [];
            // 默认选中第一个装饰物
            if (this.currentDecorations.length > 0) {
              this.selectedDecoration = this.currentDecorations[0].type;
            }
          } else {
            // 单一题目：默认选中唯一的装饰物
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

    // 计算星级
    calculateStars(isCorrect, usedTime) {
      if (!isCorrect) return 0;
      if (usedTime <= 60) return 3;
      if (usedTime <= 120) return 2;
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

    // 前往下一关
    goToNextLevel() {
      this.showAllCompleteModal = false;
      uni.navigateBack({ delta: 1 });
    },

    // 切到下一题
    startNextQuestion() {
      this.smallKnots = [];
      this.bigKnots = [];
      this.ropes = [];
      this.selectedDecoration = null;
      this.currentDecorations = [];
      // 清空已尝试题目列表
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
  background: linear-gradient(135deg, #fff59d 0%, #ffeb3b 50%, #ffc107 100%);
  border-radius: 24rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
  font-size: 24rpx;
  color: #333;
  max-width: 420rpx;
  position: relative;
  z-index: 3;
}

.child-speech-text {
  line-height: 1.6;
  font-weight: 500;
}

.child-tip {
  position: absolute;
  top: -60rpx;
  right: 0;
  background: rgba(255, 152, 0, 0.95);
  color: #fff;
  padding: 12rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  white-space: nowrap;
  animation: bounce 1s infinite;
  box-shadow: 0 4rpx 12rpx rgba(255, 152, 0, 0.4);
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10rpx);
  }
}

/* 弹窗样式 */
.popup-mask {
  position: fixed;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 998;
}

.game-popup {
  position: fixed;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 999;
  width: 640rpx;
  border-radius: 24rpx;
  padding: 46rpx 44rpx;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10rpx);
  -webkit-backdrop-filter: blur(10rpx);
  border: 1rpx solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
}

/* 绳结区域 */
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
  font-weight: 700;
  color: #2c2c2c;
  margin-bottom: 20rpx;
  padding: 0 120rpx;
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

.rope-section {
  margin-bottom: 22rpx;
  padding: 18rpx 0;
  background: transparent;
  border-radius: 0;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.35);
}

.rope-section:last-child {
  border-bottom: 0;
}

.rope-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15rpx;
}

.rope-label {
  font-size: 26rpx;
  font-weight: 600;
  color: #2c2c2c;
  text-shadow: 0 1rpx 3rpx rgba(255, 255, 255, 0.8);
}

.rope-count {
  font-size: 24rpx;
  color: #2c2c2c;
  font-weight: 600;
  text-shadow: 0 1rpx 3rpx rgba(255, 255, 255, 0.8);
}

.rope-wrapper {
  position: relative;
  width: 100%;
  height: 120rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.icon-display-area {
  position: absolute;
  top: 30rpx;
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

.icon-display-area--left {
  left: 30rpx;
}

.icon-display-area--right {
  right: 30rpx;
}

.question-icon-unicode {
  font-size: 50rpx;
}

.rope-image {
  width: 100%;
  height: auto;
}

.rope-knot {
  position: absolute;
  top: 50%;
  left: 0;
  width: 15%;
  min-width: 80rpx;
  max-width: 110rpx;
  height: 100rpx;
  transform: translate(-50%, calc(-50% - 18rpx));
  transform-origin: center center;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  pointer-events: none;
}

.knot-animating {
  animation: knotAppear 0.3s ease;
}

@keyframes knotAppear {
  0% {
    transform: translate(-50%, calc(-50% - 18rpx)) scale(0);
    opacity: 0;
  }
  50% {
    transform: translate(-50%, calc(-50% - 18rpx)) scale(1.2);
  }
  100% {
    transform: translate(-50%, calc(-50% - 18rpx)) scale(1);
    opacity: 1;
  }
}

.knot-image {
  width: 100%;
  height: 100%;
  transform: rotate(90deg);
  object-fit: contain;
}

.knot-decoration {
  position: absolute;
  bottom: -8rpx;
  font-size: 32rpx;
  pointer-events: none;
  filter: drop-shadow(0 2rpx 4rpx rgba(0, 0, 0, 0.2));
}

/* 大结样式（10） */
.big-knot {
  width: 18%;
  min-width: 90rpx;
  max-width: 120rpx;
  height: 110rpx;
}

.big-knot-image {
  transform: rotate(90deg) scale(1.2);
}

.rope-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-top: 10rpx;
  padding: 0 40rpx;
  font-size: 26rpx;
  color: #2c2c2c;
  font-weight: 600;
  text-shadow: 0 1rpx 3rpx rgba(255, 255, 255, 0.8);
  -webkit-font-smoothing: antialiased;
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

.time-text {
  color: #ff9800;
  font-weight: 600;
}

/* 底部操作栏 */
.bottom-bar {
  display: flex;
  justify-content: center;
  gap: 20rpx;
  margin-top: 30rpx;
}

.btn-submit {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  background: linear-gradient(135deg, #ff8c42 0%, #ff6b35 50%, #ff5722 100%);
  color: #fff;
  border: none;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 600;
  box-shadow: 0 6rpx 20rpx rgba(255, 107, 53, 0.4);
}

.btn-disabled {
  background: #ccc;
  box-shadow: none;
}

/* 成功弹窗 */
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
  justify-content: center;
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

/* 答错弹窗样式 */
.error-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 300;
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
  background: linear-gradient(135deg, #2196f3 0%, #42a5f5 100%);
  color: #fff;
  box-shadow: 0 4rpx 12rpx rgba(33, 150, 243, 0.3);
}
</style>

