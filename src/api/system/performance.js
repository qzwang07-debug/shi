import request from '@/utils/request';

/**
 * 获取商品性能评分
 * @param {Number} productId 商品ID
 * @returns 性能评分数据
 */
export function getProductPerformanceScore(productId) {
  return request({
    url: `/system/product/performance/${productId}`,
    method: 'get'
  });
}

