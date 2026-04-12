<template>
  <view class="page_demo" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
    <!-- 返回箭头 -->
    <view class="back-arrow" @click="goBack"></view>
    <tn-nav-bar>Demo</tn-nav-bar>
  </view>
</template>

<script>
  import mixin from '@/libs/mixins/page.js';

  export default {
    mixins: [mixin],
    data() {
      return {};
    },
    methods: {},
  };
</script>

<style></style>
