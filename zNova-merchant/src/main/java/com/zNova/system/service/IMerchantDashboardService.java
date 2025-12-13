package com.zNova.system.service;

import com.zNova.system.domain.vo.DashboardStatsVO;
import com.zNova.system.domain.vo.DashboardTrendVO;

/**
 * 商家面板统计 Service 接口
 */
public interface IMerchantDashboardService {

    /**
     * 获取聚合统计数据
     *
     * @return DashboardStatsVO
     */
    DashboardStatsVO getDashboardStats();

    /**
     * 获取图表趋势数据
     */
    DashboardTrendVO getDashboardTrend();
}