Component({
  name: 'CardList',
  properties: {
    title: {
      type: String,
      default: '',
    },
    url: {
      type: String,
      default: '',
    },
    toTab: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    clickMore() {
      if (this.properties.toTab) {
        wx.switchTab({
          url: this.properties.url,
        });
      } else {
        wx.navigateTo({
          url: this.properties.url,
        });
      }
    },
  },
});