<script>
import AudioManager from '@/utils/audio-manager.js';

export default {
  onLaunch: function () {
    console.log('App Launch');
    AudioManager.initClickSound();
  },
  onShow: function () {
    console.log('App Show');
    AudioManager.playBGM();
    AudioManager.initClickSound();
  },
  onHide: function () {
    console.log('App Hide');
    AudioManager.pauseBGM();
  },
};
</script>

<style lang="scss">
@import './styles/index.scss';
@import './styles/ui/index.scss';
@import './styles/ui/iconfont.css';
</style>
