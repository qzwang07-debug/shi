package com.zNova.system.controller;

import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.system.domain.vo.DashboardStatsVO;
import com.zNova.system.domain.vo.DashboardTrendVO;
import com.zNova.system.service.IMerchantDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家后台首页统计控制器
 */
@RestController
@RequestMapping("/merchant/dashboard")
public class MerchantDashboardController extends BaseController {

    @Autowired
    private IMerchantDashboardService merchantDashboardService;

    /**
     * 获取首页统计数据
     * URL: GET /merchant/dashboard/stats
     */
    // 假设商家后台有特定的权限标识，或者只要登录即可访问
    // 这里使用 common 权限或者自定义权限 @PreAuthorize("@ss.hasPermi('merchant:dashboard:list')")
    @GetMapping("/stats")
    public AjaxResult getDashboardStats() {
        DashboardStatsVO stats = merchantDashboardService.getDashboardStats();
        return success(stats);
    }

    /**
     * 获取图表趋势数据
     * URL: GET /merchant/dashboard/trend
     */
    @GetMapping("/trend")
    public AjaxResult getDashboardTrend() {
        DashboardTrendVO trend = merchantDashboardService.getDashboardTrend();
        return success(trend);
    }
}