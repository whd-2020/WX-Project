<template>
  <view class="page_webview" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <view class="back-arrow" @click="goBack"></view>
    <!-- #ifndef MP -->
    <tn-nav-bar>{{ pageTitle }}</tn-nav-bar>
    <view class="webview-container">
      <web-view id="inneriframe" :src="webUrl"> </web-view>
    </view>
    <!-- #endif -->

    <!-- #ifdef MP -->
    <web-view id="inneriframe" :src="webUrl"></web-view>
    <!-- #endif -->
  </view>
</template>

<script>
import mixin from '@/libs/mixins/page.js';
export default {
  mixins: [mixin],
  data() {
    return {
      webUrl: '',
      pageTitle: '网页'
    };
  },
  onLoad(options) {
    if (options.url) {
      this.webUrl = decodeURIComponent(options.url);
    }
    if (options.title) {
      this.pageTitle = decodeURIComponent(options.title);
      uni.setNavigationBarTitle({
        title: this.pageTitle
      });
    }
  },
};
</script>

<style lang="scss" scoped>
@import 'styles/pages/index.scss';
</style>
