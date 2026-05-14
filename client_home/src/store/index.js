import Vue from 'vue';
import Vuex from 'vuex';
import auth from './modules/auth';
import app from './modules/app';
import tabbar from './modules/tabbar';
import getters from './getters';
Vue.use(Vuex);

const store = new Vuex.Store({
  getters,
  modules: {
    app,
    auth,
    tabbar,
  },
});

export default store;
