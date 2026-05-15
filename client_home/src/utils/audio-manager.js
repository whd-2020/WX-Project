const AudioManager = {
  _bgmAudioContext: null,
  _isPlaying: false,
  _bgmSrc: '/static/audio/bgm.mp3',
  _tipAudioContext: null,

  _initBGM() {
    if (this._bgmAudioContext) {
      try {
        if (typeof this._bgmAudioContext.destroy === 'function') {
          this._bgmAudioContext.destroy();
        }
      } catch (e) {}
      this._bgmAudioContext = null;
    }
    this._bgmAudioContext = uni.createInnerAudioContext();
    this._bgmAudioContext.src = this._bgmSrc;
    this._bgmAudioContext.loop = true;
    this._bgmAudioContext.onPlay(() => {
      this._isPlaying = true;
    });
    this._bgmAudioContext.onPause(() => {
      this._isPlaying = false;
    });
    this._bgmAudioContext.onStop(() => {
      this._isPlaying = false;
    });
    this._bgmAudioContext.onEnded(() => {
      this._isPlaying = false;
    });
    this._bgmAudioContext.onError((res) => {
      console.warn('背景音乐加载失败:', res && res.errMsg ? res.errMsg : res);
      this._isPlaying = false;
    });
  },

  playBGM() {
    const disable = !!uni.getStorageSync('disable_bgm');
    if (disable) return;
    if (!this._bgmAudioContext) {
      this._initBGM();
    }
    if (!this._isPlaying) {
      try {
        this._bgmAudioContext.play();
      } catch (e) {
        console.warn('播放背景音乐失败:', e);
        this._isPlaying = false;
        this._bgmAudioContext = null;
      }
    }
  },

  pauseBGM() {
    if (this._bgmAudioContext && this._isPlaying) {
      try {
        this._bgmAudioContext.pause();
      } catch (e) {
        console.warn('暂停背景音乐失败:', e);
      }
    }
  },

  stopBGM() {
    if (this._bgmAudioContext) {
      try {
        this._bgmAudioContext.stop();
      } catch (e) {
        console.warn('停止背景音乐失败:', e);
      }
      this._isPlaying = false;
    }
  },

  refreshBGMState() {
    const disable = !!uni.getStorageSync('disable_bgm');
    if (disable) {
      this.pauseBGM();
    } else {
      this.playBGM();
    }
  },

  playTipSound(src) {
    const disable = !!uni.getStorageSync('disable_tip_sound');
    if (disable) return;
    
    if (this._tipAudioContext) {
      try {
        this._tipAudioContext.destroy();
      } catch (e) {}
    }
    
    this._tipAudioContext = uni.createInnerAudioContext();
    this._tipAudioContext.src = src;
    this._tipAudioContext.onEnded(() => {
      try {
        this._tipAudioContext.destroy();
        this._tipAudioContext = null;
      } catch (e) {}
    });
    this._tipAudioContext.onError((res) => {
      console.warn('提示音加载失败:', res);
      try {
        this._tipAudioContext.destroy();
        this._tipAudioContext = null;
      } catch (e) {}
    });
    this._tipAudioContext.play();
  },

  destroy() {
    if (this._bgmAudioContext) {
      this._bgmAudioContext.destroy();
      this._bgmAudioContext = null;
      this._isPlaying = false;
    }
    if (this._tipAudioContext) {
      this._tipAudioContext.destroy();
      this._tipAudioContext = null;
    }
  }
};

export default AudioManager;
