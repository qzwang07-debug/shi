package com.zNova.system.service;

import java.util.List;
import com.zNova.system.domain.BizProduct;

/**
 * 商品推荐服务接口
 * 基于协同过滤算法的商品推荐
 * 
 * @author zNova
 * @date 2025-01-21
 */
public interface IRecommendService 
{
    /**
     * 为用户推荐商品（基于协同过滤）
     * 
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐的商品列表
     */
    List<BizProduct> recommendForUser(Long userId, int limit);

    /**
     * 获取相似商品推荐（基于物品的协同过滤）
     * 
     * @param productId 商品ID
     * @param limit 推荐数量
     * @return 相似商品列表
     */
    List<BizProduct> getSimilarProducts(Long productId, int limit);

    /**
     * 获取热门商品推荐（冷启动/无登录用户）
     * 
     * @param limit 推荐数量
     * @return 热门商品列表
     */
    List<BizProduct> getHotProducts(int limit);

    /**
     * 记录用户商品交互（用于协同过滤数据收集）
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @param interactionType 交互类型(1=下单 2=浏览)
     * @param orderId 关联订单ID（可选）
     */
    void recordInteraction(Long userId, Long productId, String interactionType, Long orderId);
}
