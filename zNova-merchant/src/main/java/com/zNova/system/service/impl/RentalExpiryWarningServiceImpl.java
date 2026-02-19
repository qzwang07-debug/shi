package com.zNova.system.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zNova.system.domain.ShopOrder;
import com.zNova.system.domain.ShopOrderItem;
import com.zNova.system.mapper.ShopOrderMapper;
import com.zNova.system.service.IRentalExpiryWarningService;

/**
 * 租期到期提醒Service实现
 * 
 * @author zNova
 * @date 2025-01-22
 */
@Service
public class RentalExpiryWarningServiceImpl implements IRentalExpiryWarningService {
    
    private static final Logger log = LoggerFactory.getLogger(RentalExpiryWarningServiceImpl.class);
    
    @Autowired
    private ShopOrderMapper shopOrderMapper;
    
    /**
     * 标记即将到期的订单
     * 此方法主要用于定时任务记录日志，实际提醒逻辑由前端登录时查询实现
     */
    @Override
    public int markExpiringSoonOrders(int daysBeforeExpiry) {
        // 查询所有即将到期的租赁订单明细
        List<ShopOrderItem> expiringItems = shopOrderMapper.selectExpiringSoonItems(daysBeforeExpiry);
        
        if (expiringItems == null || expiringItems.isEmpty()) {
            log.info("未发现即将到期的租赁订单");
            return 0;
        }
        
        log.info("发现 {} 条即将到期（{}天内）的租赁明细", expiringItems.size(), daysBeforeExpiry);
        
        // 这里可以发送消息通知、邮件等，当前仅记录日志
        for (ShopOrderItem item : expiringItems) {
            log.info("订单明细 {} - 商品: {}, 到期日期: {}", 
                    item.getId(), item.getProductName(), item.getRentEndTime());
        }
        
        return expiringItems.size();
    }
    
    /**
     * 获取用户即将到期的订单列表
     */
    @Override
    public List<Map<String, Object>> getExpiringSoonOrders(Long userId, int daysBeforeExpiry) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 查询该用户即将到期的租赁订单
        List<ShopOrderItem> expiringItems = shopOrderMapper.selectExpiringSoonItemsByUserId(userId, daysBeforeExpiry);
        
        if (expiringItems == null || expiringItems.isEmpty()) {
            return result;
        }
        
        LocalDate today = LocalDate.now();
        
        for (ShopOrderItem item : expiringItems) {
            Map<String, Object> info = new HashMap<>();
            info.put("orderId", item.getOrderId());
            info.put("productName", item.getProductName());
            info.put("rentEndTime", item.getRentEndTime());
            
            // 计算剩余天数
            LocalDate endDate = item.getRentEndTime().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, endDate);
            info.put("daysRemaining", daysRemaining);
            
            result.add(info);
        }
        
        return result;
    }
}
