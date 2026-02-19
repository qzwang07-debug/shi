package com.zNova.system.service;

import java.util.List;
import java.util.Map;

/**
 * 租期到期提醒Service接口
 * 
 * @author zNova
 * @date 2025-01-22
 */
public interface IRentalExpiryWarningService {
    
    /**
     * 标记即将到期的订单（用于定时任务）
     * 
     * @param daysBeforeExpiry 到期前天数（如2表示2天内到期）
     * @return 即将到期的订单数量
     */
    int markExpiringSoonOrders(int daysBeforeExpiry);
    
    /**
     * 获取用户即将到期的订单列表
     * 
     * @param userId 用户ID
     * @param daysBeforeExpiry 到期前天数
     * @return 即将到期的订单信息列表
     */
    List<Map<String, Object>> getExpiringSoonOrders(Long userId, int daysBeforeExpiry);
}
