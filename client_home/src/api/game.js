import http from '@/utils/http2';

/**
 * 获取玩家在指定赛道的关卡进度
 * @param {Number} gamerId 玩家ID
 * @param {Number} trackId 赛道ID（1-结绳计数，2-筹算，3-珠算，4-数字认知
 */
export const getLevelProgressApi = (gamerId, trackId) => {
  const params = {
    gamerId,
    trackId,
  };
  return http.request({
    url: `/game_levels/get_progress`,
    method: 'GET',
    custom: {
      isDeduplication: false,
      isLoading: true,
    },
    params,
  });
};
