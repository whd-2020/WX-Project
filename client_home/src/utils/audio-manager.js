const AudioManager = {
  _bgmAudioContext: null,
  _isPlaying: false,
  _bgmSrc: '/static/audio/bgm.mp3',
  _tipAudioContext: null,
  _clickAudioContext: null,
  _clickSrc: '/static/audio/click.mp3',
  _clickAudioReady: false,
  _clickAudioLoading: false,

  _storageKeys: {
    bgm: 'bgmEnabled',
    click: 'clickSoundEnabled'
  },

  isBgmEnabled() {
    try {
      const val = uni.getStorageSync(this._storageKeys.bgm);
      return val !== false;
    } catch (e) {
      console.warn('读取背景音乐设置失败:', e);
      return true;
    }
  },

  isClickSoundEnabled() {
    try {
      const val = uni.getStorageSync(this._storageKeys.click);
      return val !== false;
    } catch (e) {
      console.warn('读取点击音效设置失败:', e);
      return true;
    }
  },

  setBgmEnabled(enabled) {
    uni.setStorageSync(this._storageKeys.bgm, enabled);
    if (enabled) {
      this.playBGM();
    } else {
      this.stopBGM();
    }
  },

  setClickSoundEnabled(enabled) {
    uni.setStorageSync(this._storageKeys.click, enabled);
  },

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
    if (!this.isBgmEnabled()) return;
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
    if (this.isBgmEnabled()) {
      this.playBGM();
    } else {
      this.stopBGM();
    }
  },

  playTipSound(src) {
    if (!this.isClickSoundEnabled()) return;
    
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

  initClickSound() {
    if (this._clickAudioLoading || this._clickAudioReady) return;
    
    this._clickAudioLoading = true;
    
    if (this._clickAudioContext) {
      try {
        this._clickAudioContext.destroy();
      } catch (e) {}
    }
    
    this._clickAudioContext = uni.createInnerAudioContext();
    this._clickAudioContext.src = this._clickSrc;
    this._clickAudioContext.loop = false;
    
    this._clickAudioContext.onCanplay(() => {
      this._clickAudioReady = true;
      this._clickAudioLoading = false;
    });
    
    this._clickAudioContext.onEnded(() => {
      try {
        if (this._clickAudioContext) {
          this._clickAudioContext.seek(0);
        }
      } catch (e) {
        console.warn('重置点击音效失败:', e);
        this._clickAudioReady = false;
        this._clickAudioLoading = false;
      }
    });
    
    this._clickAudioContext.onError((res) => {
      console.warn('点击音效加载失败:', res && res.errMsg ? res.errMsg : res);
      this._clickAudioReady = false;
      this._clickAudioLoading = false;
      if (this._clickAudioContext) {
        try {
          this._clickAudioContext.destroy();
        } catch (e) {}
        this._clickAudioContext = null;
      }
    });
  },

  playClickSound() {
    if (!this.isClickSoundEnabled()) return;
    this._playClickSoundInternal();
  },

  playClickSoundForce() {
    this._playClickSoundInternal();
  },

  _playClickSoundInternal() {
    if (!this._clickAudioReady) {
      this.initClickSound();
      return;
    }
    
    if (!this._clickAudioContext) {
      this.initClickSound();
      return;
    }
    
    try {
      this._clickAudioContext.seek(0);
      this._clickAudioContext.play();
    } catch (e) {
      console.warn('播放点击音效失败:', e);
      this._clickAudioReady = false;
      this.initClickSound();
    }
  },

  destroy() {
    if (this._bgmAudioContext) {
      try {
        this._bgmAudioContext.destroy();
      } catch (e) {}
      this._bgmAudioContext = null;
      this._isPlaying = false;
    }
    if (this._tipAudioContext) {
      try {
        this._tipAudioContext.destroy();
      } catch (e) {}
      this._tipAudioContext = null;
    }
    if (this._clickAudioContext) {
      try {
        this._clickAudioContext.destroy();
      } catch (e) {}
      this._clickAudioContext = null;
    }
    this._clickAudioReady = false;
    this._clickAudioLoading = false;
  }
};

export default AudioManager;
