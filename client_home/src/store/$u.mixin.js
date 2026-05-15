import { mapState } from 'vuex';
import store from '@/store';

export default {
  computed: {
    ...mapState('app', ['token', 'userInfo', 'userGroup', 'userAuth', 'supportChat', 'vuex_version', 'vuex_custom_nav_bar', 'vuex_status_bar_height', 'vuex_custom_bar_height', 'vuex_safe_area_bottom', 'pathList']),
  },
};
