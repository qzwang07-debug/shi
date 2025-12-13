package com.zNova.system.service;

import com.zNova.system.domain.vo.AdminDashboardStatsVO;
import com.zNova.system.domain.vo.VisitTrendVO;

/**
 * 管理员面板统计 Service 接口
 */
public interface IAdminDashboardService {

    /**
     * 获取管理员后台首页统计数据
     *
     * @return AdminDashboardStatsVO
     */
    AdminDashboardStatsVO getAdminStats();

    /**
     * 获取近30日系统访问趋势数据
     *
     * @return VisitTrendVO 包含日期列表和对应的访问人数列表
     */
    VisitTrendVO getRecent30DaysVisitTrend();
}