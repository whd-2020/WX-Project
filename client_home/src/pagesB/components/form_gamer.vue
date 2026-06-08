<template>
  <view class="diy_register">
      <!-- 注册页：玩家网名是前端必填字段，这里不再受权限配置隐藏，避免校验报“玩家网名不能为空”但看不到输入框 -->
      <view class="diy_register_field col-md-6">
      <view class="diy_title">
        <text> 玩家网名 </text>
      </view>
                  <!-- 文本 -->
        <view class="diy_field diy_text">
          <input type="text" id="form_player_screen_name" v-model="form['player_screen_name']" @blur="handleBlur($event.target.value,'player_screen_name')"  placeholder="请输入玩家网名" />
        </view>
              </view>
    </view>
</template>

<script>
    import { uploadFilePathApi, uploadFileApi } from '@/api/common.js';
export default {
  model: {
    prop: 'form',
    event: 'change',
  },
  props: {
    form: {
      type: Object,
      default: () => {
        return {
          url_upload: '~/api/gamer/upload?',
        };
      },
    },
  },
  data() {
    return {
        };
  },
  methods: {
        /**
     * 获取当前表单数据
     */
    getFormData() {
      return this.form;
    },
        /**
     * 触发所有输入框的blur事件，用于提交前收集数据
     */
    triggerBlur() {
      // 获取所有输入框
      const inputs = this.$el.querySelectorAll('input');
      inputs.forEach(input => {
        // 手动触发blur事件
        const event = new Event('blur', { bubbles: true });
        input.dispatchEvent(event);
        // 同时调用handleBlur方法
        this.handleBlur(input.value, input.id.replace('form_', ''));
      });
    },
        /**
     * 上传图片
     * @param {Object} param文件参数
     */
    change_img(key_name) {
      var _self = this;
      _self.upload_img_flag = false;
      // 选择图像方法
      uni.chooseImage({
        count: 1,
        sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
        sourceType: ['album'], //从相册选择
        success: (res) => {
          console.log('开始上传图片');
          console.log(tempFilePaths);
          const tempFilePaths = res.tempFilePaths;
          const filePath = tempFilePaths[0];
          uploadFilePathApi(
                  undefined,
                  filePath,
                  undefined,
                  {
                    i_want_to_customize: 'test',
                  },
                  (task) => {
                    task.onProgressUpdate((res) => {
                      this.percent = res.progress;
                      console.log('上传进度' + res.progress);
                      console.log('已经上传的数据长度' + res.totalBytesSent);
                      console.log('预期需要上传的数据总长度' + res.totalBytesExpectedToSend);
                    });
                  }
          ).then((res) => {
            const filename = res.result.url;
            _self.$delete(this.form, key_name);
            _self.$set(this.form, key_name, filename);
            _self.handleBlur(filename, key_name);
          });
        },
        error: function (e) {
          console.log(e);
        },
      });
    },
    handleBlur(value, type) {
      this.$emit('change', { value, type });
    },
  },
  mounted() {
          },
};
</script>
