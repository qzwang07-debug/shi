package com.zNova.system.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.common.utils.SecurityUtils;
import com.zNova.system.domain.BizProduct;
import com.zNova.system.service.IRecommendService;

/**
 * 商品推荐Controller
 * 基于协同过滤算法的商品推荐接口
 * 
 * @author zNova
 * @date 2025-01-21
 */
@RestController
@RequestMapping("/front/recommend")
public class FrontRecommendController extends BaseController {
    
    @Autowired
    private IRecommendService recommendService;
    
    /**
     * 获取个性化推荐商品
     * 基于用户历史行为的协同过滤推荐
     * 
     * @param limit 推荐数量（默认10）
     * @return 推荐商品列表
     */
    @GetMapping("/personal")
    public AjaxResult getPersonalRecommend(@RequestParam(defaultValue = "10") Integer limit) {
        Long userId = null;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            // 未登录用户，返回热门商品
        }
        
        List<BizProduct> products;
        if (userId != null) {
            products = recommendService.recommendForUser(userId, limit);
        } else {
            products = recommendService.getHotProducts(limit);
        }
        
        return success(products);
    }
    
    /**
     * 获取相似商品推荐
     * 基于物品的协同过滤，找出与指定商品相似的商品
     * 
     * @param productId 商品ID
     * @param limit 推荐数量（默认6）
     * @return 相似商品列表
     */
    @GetMapping("/similar/{productId}")
    public AjaxResult getSimilarProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "6") Integer limit) {
        List<BizProduct> products = recommendService.getSimilarProducts(productId, limit);
        return success(products);
    }
    
    /**
     * 获取热门商品推荐
     * 适用于冷启动场景（新用户/未登录用户）
     * 
     * @param limit 推荐数量（默认10）
     * @return 热门商品列表
     */
    @GetMapping("/hot")
    public AjaxResult getHotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        List<BizProduct> products = recommendService.getHotProducts(limit);
        return success(products);
    }
    
    /**
     * 记录商品浏览行为
     * 用于收集协同过滤数据
     * 
     * @param productId 商品ID
     * @return 操作结果
     */
    @PostMapping("/view/{productId}")
    public AjaxResult recordView(@PathVariable Long productId) {
        Long userId = null;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            // 未登录用户不记录
            return success();
        }
        
        if (userId != null) {
            recommendService.recordInteraction(userId, productId, "2", null);
        }
        return success();
    }
}
