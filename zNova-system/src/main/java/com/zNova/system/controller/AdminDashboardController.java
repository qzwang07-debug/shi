package com.zNova.system.controller;

import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.system.domain.vo.AdminDashboardStatsVO;
import com.zNova.system.domain.vo.VisitTrendVO;
import com.zNova.system.service.IAdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员后台首页统计控制器
 */
@RestController
@RequestMapping("/system/dashboard")
public class AdminDashboardController extends BaseController {

    @Autowired
    private IAdminDashboardService adminDashboardService;

    /**
     * 获取管理员后台首页统计数据
     * URL: GET /system/dashboard/admin-stats
     */
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/admin-stats")
    public AjaxResult getAdminStats() {
        AdminDashboardStatsVO stats = adminDashboardService.getAdminStats();
        return success(stats);
    }

    /**
     * 获取近30日系统访问趋势数据
     * URL: GET /system/dashboard/visit-trend
     */
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/visit-trend")
    public AjaxResult getRecent30DaysVisitTrend() {
        VisitTrendVO trend = adminDashboardService.getRecent30DaysVisitTrend();
        return success(trend);
    }
}