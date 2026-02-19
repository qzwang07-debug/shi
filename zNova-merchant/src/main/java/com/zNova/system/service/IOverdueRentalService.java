package com.zNova.system.service;

/**
 * 逾期租赁处理Service接口
 * 
 * @author zNova
 * @date 2025-01-21
 */
public interface IOverdueRentalService {
    
    /**
     * 检查并处理所有逾期租赁订单
     * 逻辑：
     * 1. 查询所有逾期的租赁订单明细（租赁结束时间 < 当前日期）
     * 2. 对每个逾期明细计算需要扣除的押金（2x日租 * 数量 * 逾期天数）
     * 3. 扣减订单押金余额
     * 4. 首次逾期时扣除信用分-100
     * 5. 更新已扣逾期天数（用于幂等处理）
     * 
     * @return 处理的逾期订单数量
     */
    int checkAndDeductOverdueRentals();
}
