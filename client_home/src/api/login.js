import http from '@/utils/http2';

/**
 * 登录
 */
export const loginApi = (data) => {
  return http.request({
    url: `/user/login`,
    method: 'POST',
	header: {
	    'content-type': 'application/json'
	},
    custom: {
      isDeduplication: false,
      isLoading: true,
    },
    data,
  });
};


/**
 * 获取用户信息
 */
export const getUserInfoApi = () => {
  return http.request({
    url: `/user/state`,
    method: 'GET',
    custom: {
      isLoading: false,
    },
  });
};

/**
 * 获取用户权限
 */
export const getUserAuthApi = (userGroup = '游客') => {
  const params = {
    user_group: userGroup,
  };
  return http.request({
    url: `/auth/get_list`,
    method: 'GET',
    custom: {
      isLoading: false,
    },
    params,
  });
};
/**
 * 新增微信登录
 */
export const wechatLoginApi = (data) => {
  return http.request({
    url: `/user/wechat/login`,
    method: 'POST',
    header: {
      'content-type': 'application/json'
    },
    custom: {
      isDeduplication: false,
      isLoading: true,
    },
    data,
  });
};

/**
 * 更新用户个人信息
 */
export const updateProfileApi = (data) => {
  return http.request({
    url: `/user/update_profile`,
    method: 'POST',
    custom: {
      isLoading: true,
    },
    data,
  });
};