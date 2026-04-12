<template>
  <view class="rope-level-page" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <!-- 返回箭头 -->
    <view class="back-arrow" @click="goBack"></view>
    <tn-nav-bar>结绳计数 · 第三关</tn-nav-bar>

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
      <!-- 绳结互动区域 -->
      <view class="rope-area">
        <view class="rope-title">{{ question && question.questionTitle ? question.questionTitle : '点击绳子打结' }}</view>
        <view class="rope-wrapper" @click="handleRopeClick">
          <image class="rope-image" src="/static/img/rope/ShengZi.png" mode="widthFix" />
          <view
            v-for="(knot, index) in knots"
            :key="knot.id"
            class="rope-knot"
            :class="{ 'knot-animating': knot.animating }"
            :style="{ left: knot.x + '%' }"
          >
            <image class="knot-image" src="/static/img/rope/ShengJie.png" mode="aspectFit" />
            <text class="knot-decoration">{{ selectedDecorationIcon }}</text>
          </view>
        </view>
        <view class="rope-info">
          <text>当前绳结数量：{{ knots.length }}</text>
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
      trackId: 1, // 结绳计数赛道
      levelId: 3, // 第三关
      question: null,
      fullText: '今日族长正在思考要出什么题目给你……',
      displayText: '',
      typingTimer: null,
      knots: [],
      startTime: 0,
      elapsedSeconds: 0,
      elapsedTimer: null,
      // 成功弹窗相关
      showSuccessModal: false,
      successMessage: '',
      successStarCount: 0,
      successTime: 0,
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
      // 装饰物相关
      currentDecorations: [],
      selectedDecoration: null,
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
    // 获取目标数字文本
    targetNumberText() {
      if (this.question && this.question.targetNumber) {
        return this.question.targetNumber;
      }
      return '';
    },
    // 判断是否可以提交（是否达到目标结数）
    canSubmit() {
      if (!this.question || !this.question.targetNumber) {
        return false;
      }
      const target = Number(this.question.targetNumber);
      const current = this.knots.length;
      return current === target;
    },
    // 选中装饰物的图标
    selectedDecorationIcon() {
      if (!this.selectedDecoration) return '';
      const deco = this.currentDecorations.find(d => d.type === this.selectedDecoration);
      return deco ? deco.icon : '';
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

    // 从后端获取本关的一道随机题（优先本地随机）
    fetchQuestion() {
      const gamerId = Number(this.gamerId);
      const levelId = Number(this.levelId);
      const trackId = Number(this.trackId);

      // 前端兜底：如果参数不合法，就不用访问后端，直接给默认题
      if (!gamerId || Number.isNaN(levelId) || levelId <= 0 || Number.isNaN(trackId) || trackId <= 0) {
        // 如果没拿到 gamerId，也先给一条本地题，保证可以玩
        const local = {
          question_id: 0,
          targetNumber: 1,
          title: '今日族长笑着对你说：用绳结表示数字 1，你会怎么打结呢？',
          decorations: [{ type: 'BeiKe', name: '贝壳', icon: '🐚' }],
        };
        this.question = local;
        this.currentDecorations = local.decorations || [];
        this.selectedDecoration = this.currentDecorations.length > 0 ? this.currentDecorations[0].type : null;
        this.playTyping(local.title);
        return;
      }
      const params = {
        gamerId,
        levelId,
        trackId,
      };
      this.$get('/question_bank/get_level_questions', params, (json) => {
        // 打印后端返回的参数
        console.log('=== 后端返回参数 ===');
        console.log('完整返回:', JSON.stringify(json, null, 2));
        console.log('json.result:', json.result);
        if (json.result) {
          console.log('questions:', json.result.questions);
          // 兼容两种命名方式：驼峰和下划线
          console.log('allThreeStars (驼峰):', json.result.allThreeStars);
          console.log('all_three_stars (下划线):', json.result.all_three_stars);
          console.log('isCompleted (驼峰):', json.result.isCompleted);
          console.log('is_completed (下划线):', json.result.is_completed);
          console.log('questionCount (驼峰):', json.result.questionCount);
          console.log('question_count (下划线):', json.result.question_count);
          console.log('message:', json.result.message);
        }
        
        // 如果后端返回错误，直接提示出来，方便排查，而不是静默用本地题
        if (json && json.error) {
          uni.showToast({
            title: json.error.message || '获取题目失败',
            icon: 'none',
          });
          return;
        }

        // 检查是否所有题目都满3星（优先检查，避免显示默认题目）
        // 兼容两种命名方式：驼峰和下划线
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
          // 显示所有题目满3星弹窗
          this.showAllCompleteModal = true;
          console.log('弹窗状态 showAllCompleteModal:', this.showAllCompleteModal);
          
          // 3秒后自动跳转到关卡选择页面
          setTimeout(() => {
            console.log('3秒后自动跳转到关卡选择页面');
            this.showAllCompleteModal = false;
            uni.redirectTo({
              url: '/pages/track/rope',
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

        // 如果题目列表为空，但不是所有题目都满3星，显示默认题目并提示
        if (!json.result || !json.result.questions || json.result.questions.length === 0) {
          const local = {
            question_id: 0,
            targetNumber: 1,
            title: '今日族长笑着对你说：用绳结表示数字 1，你会怎么打结呢？',
            decorations: [{ type: 'BeiKe', name: '贝壳', icon: '🐚' }],
          };
          this.question = local;
          this.currentDecorations = local.decorations || [];
          this.selectedDecoration = this.currentDecorations.length > 0 ? this.currentDecorations[0].type : null;
          this.showDefaultQuestionTip = true; // 显示默认题目提示
          
          // 清除之前的定时器（如果存在）
          if (this.defaultQuestionTipTimer) {
            clearTimeout(this.defaultQuestionTipTimer);
          }
          
          // 2秒后隐藏提示
          this.defaultQuestionTipTimer = setTimeout(() => {
            this.showDefaultQuestionTip = false;
            this.defaultQuestionTipTimer = null;
          }, 2000);
          
          this.playTyping(local.title);
          return;
        }
        
        // 有正常题目时，隐藏默认题目提示
        this.showDefaultQuestionTip = false;
        // 清除定时器（如果存在）
        if (this.defaultQuestionTipTimer) {
          clearTimeout(this.defaultQuestionTipTimer);
          this.defaultQuestionTipTimer = null;
        }
        const list = json.result.questions;
        const q = list[Math.floor(Math.random() * list.length)];
        let target = 1;
        let elderSpeech = '';
        let questionTitle = '';
        let decorations = [];

        try {
          // 解析 question_content，取出 options[0] 与 question_title 拼接
          if (q.question_content) {
            const content = JSON.parse(q.question_content);
            if (content.options && Array.isArray(content.options) && content.options.length > 0) {
              // 拼接：question_title + options[0]
              elderSpeech = (q.question_title || '') + "," + content.options[0];
              // 保存 options[0] 用于弹窗标题
              questionTitle = content.options[0];
            }
            // 如果 question_content 里有 targetNumber，优先用它
            if (content.targetNumber) {
              target = Number(content.targetNumber);
            }
            // 获取装饰物列表
            if (content.decorations && Array.isArray(content.decorations)) {
              decorations = content.decorations;
            }
          }

          // 从 correct_answer 解析目标数字（如果 question_content 里没有 targetNumber）
          if (target === 1 && q.correct_answer) {
            try {
              const answer = typeof q.correct_answer === 'string' ? JSON.parse(q.correct_answer) : q.correct_answer;
              if (answer.answer) {
                const answerNum = Number(answer.answer);
                if (!Number.isNaN(answerNum) && answerNum > 0) {
                  target = answerNum;
                }
              }
              // 如果 correct_answer 中有 count，使用它作为目标数字
              if (answer.count) {
                const countNum = Number(answer.count);
                if (!Number.isNaN(countNum) && countNum > 0) {
                  target = countNum;
                }
              }
            } catch (e) {
              // 忽略解析错误
            }
          }
        } catch (e) {
          // 忽略解析错误，按默认处理
        }

        // 如果拼接后还是空的，用默认文案
        if (!elderSpeech || elderSpeech.trim() === '') {
          elderSpeech = `今日族长笑着对你说：用绳结表示数字 ${target}，你会怎么打结呢？`;
        }

        // 如果 questionTitle 为空，使用默认标题
        if (!questionTitle || questionTitle.trim() === '') {
          questionTitle = `点击绳子打结，表示数字${target}`;
        }

        this.question = {
          question_id: q.question_id,
          targetNumber: target,
          title: elderSpeech,
          questionTitle: questionTitle,
          decorations: decorations,
        };
        this.currentDecorations = decorations;
        this.selectedDecoration = decorations.length > 0 ? decorations[0].type : null;
        this.playTyping(this.question.title);
      });
    },

    // 点击绳子：如果附近已有绳结则"解结"，否则新增一个绳结
    handleRopeClick(e) {
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
          // 限制在 5% ~ 95% 之间，避免太靠边
          percent = Math.max(5, Math.min(95, percent));

          // 查找距离点击位置很近的结（±5%），有的话移除这个结
          const threshold = 5;
          const idx = this.knots.findIndex((k) => Math.abs(k.x - percent) <= threshold);
          if (idx !== -1) {
            // 解结
            this.knots.splice(idx, 1);
          } else if (this.knots.length < 9) {
            // 打结（最多 9 个绳结）：添加绳结并触发出现动画
            const newKnot = {
              id: Date.now() + '_' + this.knots.length,
              x: percent,
              animating: true,
            };
            this.knots.push(newKnot);
            // 动画结束后移除 animating 标记
            setTimeout(() => {
              const idx = this.knots.findIndex((k) => k.id === newKnot.id);
              if (idx !== -1) {
                this.$set(this.knots[idx], 'animating', false);
              }
            }, 400);
          }
        })
        .exec();
    },

    // 提交答案：前端先根据绳结数量判断，再调用后端记录
    submitAnswer() {
      if (!this.question) return;
      // 如果未达到目标结数，不允许提交
      if (!this.canSubmit) return;
      const count = this.knots.length;
      const target = Number(this.question.targetNumber || 0);

      const isCorrectFront = count === target;

      const usedTime = (Date.now() - this.startTime) / 1000.0;
      const stars = this.calculateStars(isCorrectFront, usedTime);

      if (!this.gamerId || !this.question.question_id) {
        // 没有 gamerId 或题目ID，使用前端判断
        if (isCorrectFront) {
          // 答对了：显示弹窗
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
          
          // 关闭游戏弹窗
          this.showGamePopup = false;
          this.successMessage = encouragement;
          this.successStarCount = stars;
          this.successTime = Math.round(actualTime);
          this.showSuccessModal = true;
          this.$forceUpdate();
        } else {
          // 答错了：显示提示
          uni.showToast({
            title: '再试试，多打几个结～',
            icon: 'none',
          });
        }
        return;
      }

      // 调用后端提交答案（使用JSON格式：{"answer":"7"}）
      const body = {
        gamerId: this.gamerId,
        questionId: this.question.question_id,
        levelId: this.levelId,
        trackId: this.trackId,
        userAnswer: JSON.stringify({ answer: String(count) }),
        answerTime: usedTime,
      };

      this.$post('/question_bank/submit_answer', body, (res) => {
        // 调试：打印完整返回结果
        console.log('=== 提交答案返回结果 ===');
        console.log('完整 res:', JSON.stringify(res, null, 2));
        console.log('res.result:', res.result);
        console.log('res.result?.isCorrect:', res.result?.isCorrect);
        console.log('res.result?.is_correct:', res.result?.is_correct);
        console.log('res.result?.isCorrect 类型:', typeof res.result?.isCorrect);

        // 信任后端的判断结果（使用更宽松的判断，兼容多种格式）
        let isCorrect = false;
        if (res && res.result) {
          const result = res.result;
          // 优先检查下划线格式（is_correct），再检查驼峰格式（isCorrect）
          const isCorrectValue = result.is_correct !== undefined ? result.is_correct : result.isCorrect;

          // 判断是否为 true（兼容布尔值、数字、字符串）
          if (isCorrectValue === true || isCorrectValue === 1 || isCorrectValue === '1' || isCorrectValue === 'true') {
            isCorrect = true;
          } else if (isCorrectValue === false || isCorrectValue === 0 || isCorrectValue === '0' || isCorrectValue === 'false') {
            isCorrect = false;
          } else if (isCorrectValue != null) {
            // 其他情况，转换为布尔值
            isCorrect = Boolean(isCorrectValue);
          }
        }
        
        console.log('最终判断 isCorrect:', isCorrect);
        console.log('提交时的 usedTime:', usedTime);
        console.log('当前 elapsedSeconds:', this.elapsedSeconds);
        
        if (isCorrect) {
          // 答对了：重新计算用时（使用实际经过的秒数，更准确）
          // 注意：elapsedSeconds 是整数秒，usedTime 是精确的秒数（带小数）
          const actualTime = usedTime; // 使用提交时的精确时间
          console.log('实际用时 actualTime:', actualTime, '秒');
          console.log('elapsedSeconds:', this.elapsedSeconds);
          
          // 根据用时计算星级并显示鼓励语
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
          
          // 关闭游戏弹窗
          this.showGamePopup = false;
          // 显示自定义成功弹窗
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
          
          // 强制更新视图
          this.$forceUpdate();
        } else {
          // 答错了：显示提示
          uni.showToast({
            title: '有点小问题，再想一想～',
            icon: 'none',
          });
        }
      });
    },

    // 根据是否答对 + 用时计算星级
    // 规则：≤60秒=3颗星，≤120秒=2颗星，>120秒=1颗星
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
      return 1; // 超过2分钟
    },

    // 格式化时间显示（超过60秒显示为分钟+秒）
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
      // 关闭后自动刷新题目
      this.startNextQuestion();
    },

    // 关闭所有题目满3星弹窗
    closeAllCompleteModal() {
      this.showAllCompleteModal = false;
    },

    // 前往下一关
    goToNextLevel() {
      this.showAllCompleteModal = false;
      // 返回关卡列表页
      uni.navigateBack({
        delta: 1
      });
    },

    // 切到下一题：清空绳结、重置计时器并重新拉题
    startNextQuestion() {
      this.knots = [];
      this.resetTimer();
      this.showFullSpeech = false; // 重置展开状态
      this.showGamePopup = false; // 关闭游戏弹窗
      // 重置小孩说话和提示
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
      // 关闭小孩提示
      this.showChildTip = false;
      if (this.childTipTimer) {
        clearTimeout(this.childTipTimer);
        this.childTipTimer = null;
      }
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

.rope-wrapper {
  position: relative;
  width: 100%;
  height: 120rpx;
  margin: 10rpx 0;
  cursor: pointer;
  /* 与按钮同宽：game-popup 内容区 520rpx */
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

.knot-decoration {
  position: absolute;
  bottom: -8rpx;
  font-size: 32rpx;
  pointer-events: none;
  filter: drop-shadow(0 2rpx 4rpx rgba(0, 0, 0, 0.2));
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
  align-items: center;
  gap: 20rpx; /* 两个文本之间的间距 */
}

.rope-info .time-text {
  margin-left: auto; /* 确保用时文本靠右 */
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
  background: rgba(0, 0, 0, 0.4);
  z-index: 998;
}

/* 游戏弹窗 */
.game-popup {
  position: fixed;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 999; /* 比绳子层级高 */
  
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
</style>

