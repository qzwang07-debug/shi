import request from '@/utils/request'

/**
 * 获取个性化推荐商品
 * @param {number} limit 推荐数量
 */
export function getPersonalRecommend(limit = 10) {
  return request({
    url: '/front/recommend/personal',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取相似商品推荐
 * @param {number} productId 商品ID
 * @param {number} limit 推荐数量
 */
export function getSimilarProducts(productId, limit = 6) {
  return request({
    url: `/front/recommend/similar/${productId}`,
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取热门商品推荐
 * @param {number} limit 推荐数量
 */
export function getHotProducts(limit = 10) {
  return request({
    url: '/front/recommend/hot',
    method: 'get',
    params: { limit }
  })
}

/**
 * 记录商品浏览行为
 * @param {number} productId 商品ID
 */
export function recordProductView(productId) {
  return request({
    url: `/front/recommend/view/${productId}`,
    method: 'post'
  })
}
