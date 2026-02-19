package com.zNova.system.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zNova.common.utils.DateUtils;
import com.zNova.system.domain.BizProduct;
import com.zNova.system.domain.UserProductInteraction;
import com.zNova.system.mapper.BizProductMapper;
import com.zNova.system.mapper.UserProductInteractionMapper;
import com.zNova.system.service.IRecommendService;

/**
 * 商品推荐服务实现
 * 基于物品的协同过滤算法（Item-Based Collaborative Filtering）
 * 
 * 算法思路：
 * 1. 计算商品之间的相似度（基于用户共同交互）
 * 2. 对于目标用户，找出他交互过的商品
 * 3. 推荐与这些商品相似的、用户未交互过的商品
 * 
 * @author zNova
 * @date 2025-01-21
 */
@Service
public class RecommendServiceImpl implements IRecommendService {
    
    private static final Logger log = LoggerFactory.getLogger(RecommendServiceImpl.class);
    
    // 交互类型常量
    private static final String INTERACTION_ORDER = "1";  // 下单/支付
    private static final String INTERACTION_VIEW = "2";   // 浏览
    
    // 交互权重
    private static final int WEIGHT_ORDER = 5;  // 下单权重高
    private static final int WEIGHT_VIEW = 1;   // 浏览权重低
    
    @Autowired
    private UserProductInteractionMapper interactionMapper;
    
    @Autowired
    private BizProductMapper productMapper;
    
    /**
     * 为用户推荐商品（基于协同过滤）
     * 
     * 算法步骤：
     * 1. 获取用户交互过的商品列表
     * 2. 如果用户无交互记录，返回热门商品（冷启动）
     * 3. 计算商品相似度矩阵
     * 4. 对用户交互过的每个商品，找出相似商品
     * 5. 按相似度得分排序，过滤掉用户已交互的商品
     * 6. 返回Top N推荐
     */
    @Override
    public List<BizProduct> recommendForUser(Long userId, int limit) {
        log.info("开始为用户 {} 生成推荐，限制数量: {}", userId, limit);
        
        // 1. 获取用户交互过的商品ID
        List<Long> userInteractedProductIds = interactionMapper.selectUserInteractedProductIds(userId);
        
        if (userInteractedProductIds == null || userInteractedProductIds.isEmpty()) {
            log.info("用户 {} 无交互记录，返回热门商品", userId);
            return getHotProducts(limit);
        }
        
        log.info("用户 {} 交互过 {} 个商品", userId, userInteractedProductIds.size());
        
        // 2. 获取商品共现矩阵和用户计数
        List<Map<String, Object>> coOccurrenceList = interactionMapper.selectProductCoOccurrence();
        List<Map<String, Object>> productUserCountList = interactionMapper.selectProductUserCount();
        
        if (coOccurrenceList == null || coOccurrenceList.isEmpty()) {
            log.info("共现数据不足，返回热门商品");
            return getHotProducts(limit);
        }
        
        // 3. 构建用户计数Map
        Map<Long, Integer> productUserCountMap = new HashMap<>();
        for (Map<String, Object> item : productUserCountList) {
            Long productId = ((Number) item.get("productId")).longValue();
            Integer userCount = ((Number) item.get("userCount")).intValue();
            productUserCountMap.put(productId, userCount);
        }
        
        // 4. 计算相似度并生成推荐分数
        // 使用余弦相似度: sim(A,B) = coCount / sqrt(userCount(A) * userCount(B))
        Map<Long, Double> recommendScores = new HashMap<>();
        Set<Long> userInteractedSet = new HashSet<>(userInteractedProductIds);
        
        for (Map<String, Object> coItem : coOccurrenceList) {
            Long productA = ((Number) coItem.get("productA")).longValue();
            Long productB = ((Number) coItem.get("productB")).longValue();
            Integer coCount = ((Number) coItem.get("coCount")).intValue();
            
            Integer countA = productUserCountMap.getOrDefault(productA, 1);
            Integer countB = productUserCountMap.getOrDefault(productB, 1);
            
            // 计算余弦相似度
            double similarity = coCount / Math.sqrt((double) countA * countB);
            
            // 如果用户交互过A，推荐B（如果B未被交互）
            if (userInteractedSet.contains(productA) && !userInteractedSet.contains(productB)) {
                recommendScores.merge(productB, similarity, Double::sum);
            }
            // 如果用户交互过B，推荐A（如果A未被交互）
            if (userInteractedSet.contains(productB) && !userInteractedSet.contains(productA)) {
                recommendScores.merge(productA, similarity, Double::sum);
            }
        }
        
        if (recommendScores.isEmpty()) {
            log.info("协同过滤无推荐结果，返回热门商品");
            return getHotProducts(limit);
        }
        
        // 5. 按分数排序，取Top N
        List<Long> recommendedProductIds = recommendScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        log.info("为用户 {} 生成 {} 个推荐商品", userId, recommendedProductIds.size());
        
        // 6. 查询商品详情
        return getProductsByIds(recommendedProductIds);
    }
    
    /**
     * 获取相似商品推荐（基于物品的协同过滤）
     */
    @Override
    public List<BizProduct> getSimilarProducts(Long productId, int limit) {
        log.info("获取商品 {} 的相似商品，限制数量: {}", productId, limit);
        
        // 1. 获取共现数据
        List<Map<String, Object>> coOccurrenceList = interactionMapper.selectProductCoOccurrence();
        List<Map<String, Object>> productUserCountList = interactionMapper.selectProductUserCount();
        
        if (coOccurrenceList == null || coOccurrenceList.isEmpty()) {
            log.info("共现数据不足，返回热门商品");
            return getHotProducts(limit);
        }
        
        // 2. 构建用户计数Map
        Map<Long, Integer> productUserCountMap = new HashMap<>();
        for (Map<String, Object> item : productUserCountList) {
            Long pid = ((Number) item.get("productId")).longValue();
            Integer userCount = ((Number) item.get("userCount")).intValue();
            productUserCountMap.put(pid, userCount);
        }
        
        // 3. 计算目标商品与其他商品的相似度
        Map<Long, Double> similarityScores = new HashMap<>();
        
        for (Map<String, Object> coItem : coOccurrenceList) {
            Long productA = ((Number) coItem.get("productA")).longValue();
            Long productB = ((Number) coItem.get("productB")).longValue();
            Integer coCount = ((Number) coItem.get("coCount")).intValue();
            
            Long targetProduct = null;
            Long otherProduct = null;
            
            if (productA.equals(productId)) {
                targetProduct = productA;
                otherProduct = productB;
            } else if (productB.equals(productId)) {
                targetProduct = productB;
                otherProduct = productA;
            }
            
            if (targetProduct != null) {
                Integer countTarget = productUserCountMap.getOrDefault(targetProduct, 1);
                Integer countOther = productUserCountMap.getOrDefault(otherProduct, 1);
                double similarity = coCount / Math.sqrt((double) countTarget * countOther);
                similarityScores.put(otherProduct, similarity);
            }
        }
        
        if (similarityScores.isEmpty()) {
            log.info("商品 {} 无相似商品，返回热门商品", productId);
            return getHotProducts(limit);
        }
        
        // 4. 按相似度排序
        List<Long> similarProductIds = similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        log.info("商品 {} 的相似商品数量: {}", productId, similarProductIds.size());
        
        return getProductsByIds(similarProductIds);
    }
    
    /**
     * 获取热门商品推荐（冷启动/无登录用户）
     */
    @Override
    public List<BizProduct> getHotProducts(int limit) {
        log.info("获取热门商品，限制数量: {}", limit);
        
        // 1. 从交互表获取热门商品ID
        List<Long> hotProductIds = interactionMapper.selectHotProductIds(limit);
        
        if (hotProductIds == null || hotProductIds.isEmpty()) {
            log.info("无交互数据，返回所有正常状态商品");
            BizProduct query = new BizProduct();
            query.setStatus("0"); // 正常状态
            return productMapper.selectBizProductList(query);
        }
        
        return getProductsByIds(hotProductIds);
    }
    
    /**
     * 记录用户商品交互
     */
    @Override
    public void recordInteraction(Long userId, Long productId, String interactionType, Long orderId) {
        if (userId == null || productId == null) {
            log.warn("记录交互失败：userId或productId为空");
            return;
        }
        
        UserProductInteraction interaction = new UserProductInteraction();
        interaction.setUserId(userId);
        interaction.setProductId(productId);
        interaction.setInteractionType(interactionType);
        interaction.setOrderId(orderId);
        interaction.setCreateTime(DateUtils.getNowDate());
        
        // 根据交互类型设置权重
        if (INTERACTION_ORDER.equals(interactionType)) {
            interaction.setWeight(WEIGHT_ORDER);
        } else {
            interaction.setWeight(WEIGHT_VIEW);
        }
        
        try {
            interactionMapper.insertUserProductInteraction(interaction);
            log.debug("记录用户交互成功: userId={}, productId={}, type={}", userId, productId, interactionType);
        } catch (Exception e) {
            log.error("记录用户交互失败: {}", e.getMessage());
        }
    }
    
    /**
     * 根据商品ID列表查询商品详情
     */
    private List<BizProduct> getProductsByIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<BizProduct> result = new ArrayList<>();
        for (Long productId : productIds) {
            BizProduct product = productMapper.selectBizProductById(productId);
            if (product != null && "0".equals(product.getStatus())) {
                result.add(product);
            }
        }
        return result;
    }
}
