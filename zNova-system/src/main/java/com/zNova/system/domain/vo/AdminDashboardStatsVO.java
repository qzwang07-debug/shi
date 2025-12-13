package com.zNova.system.domain.vo;

/**
 * 管理员后台首页统计数据 VO
 */
public class AdminDashboardStatsVO {

    /**
     * C端用户总数
     */
    private Long totalConsumerCount;

    /**
     * 商家总数
     */
    private Long totalMerchantCount;

    /**
     * 平台累计交易额
     */
    private Double totalTransactionAmount;

    /**
     * 平台累计订单数
     */
    private Long totalOrderCount;

    public AdminDashboardStatsVO() {
        this.totalConsumerCount = 0L;
        this.totalMerchantCount = 0L;
        this.totalTransactionAmount = 0.0;
        this.totalOrderCount = 0L;
    }

    public Long getTotalConsumerCount() {
        return totalConsumerCount;
    }

    public void setTotalConsumerCount(Long totalConsumerCount) {
        this.totalConsumerCount = totalConsumerCount;
    }

    public Long getTotalMerchantCount() {
        return totalMerchantCount;
    }

    public void setTotalMerchantCount(Long totalMerchantCount) {
        this.totalMerchantCount = totalMerchantCount;
    }

    public Double getTotalTransactionAmount() {
        return totalTransactionAmount;
    }

    public void setTotalTransactionAmount(Double totalTransactionAmount) {
        this.totalTransactionAmount = totalTransactionAmount;
    }

    public Long getTotalOrderCount() {
        return totalOrderCount;
    }

    public void setTotalOrderCount(Long totalOrderCount) {
        this.totalOrderCount = totalOrderCount;
    }
}