package com.zNova.system.domain.vo;



/**
 * 商家后台首页统计数据 VO
 */

public class DashboardStatsVO {

    /**
     * 今日订单数
     */
    private Long todayOrderCount;

    /**
     * 待发货订单数 (status = '1')
     */
    private Long pendingShipmentCount;

    /**
     * 租赁中/待归还订单数 (status in ['2', '7'])
     */
    private Long rentingCount;

    /**
     * 商品总数
     */
    private Long totalProductCount;

    public DashboardStatsVO() {
        this.todayOrderCount = 0L;
        this.pendingShipmentCount = 0L;
        this.rentingCount = 0L;
        this.totalProductCount = 0L;
    }

    public Long getTodayOrderCount() {
        return todayOrderCount;
    }

    public void setTodayOrderCount(Long todayOrderCount) {
        this.todayOrderCount = todayOrderCount;
    }

    public Long getPendingShipmentCount() {
        return pendingShipmentCount;
    }

    public void setPendingShipmentCount(Long pendingShipmentCount) {
        this.pendingShipmentCount = pendingShipmentCount;
    }

    public Long getRentingCount() {
        return rentingCount;
    }

    public void setRentingCount(Long rentingCount) {
        this.rentingCount = rentingCount;
    }

    public Long getTotalProductCount() {
        return totalProductCount;
    }

    public void setTotalProductCount(Long totalProductCount) {
        this.totalProductCount = totalProductCount;
    }
}