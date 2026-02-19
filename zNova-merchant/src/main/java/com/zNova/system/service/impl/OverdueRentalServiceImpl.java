package com.zNova.system.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zNova.system.domain.ShopOrderItem;
import com.zNova.system.mapper.ShopOrderMapper;
import com.zNova.system.service.IAppUserService;
import com.zNova.system.service.IOverdueRentalService;

/**
 * 逾期租赁处理Service实现
 * 
 * @author zNova
 * @date 2025-01-21
 */
@Service
public class OverdueRentalServiceImpl implements IOverdueRentalService {
    
    private static final Logger log = LoggerFactory.getLogger(OverdueRentalServiceImpl.class);
    
    @Autowired
    private ShopOrderMapper shopOrderMapper;
    
    @Autowired
    private IAppUserService appUserService;
    
    /**
     * 检查并处理所有逾期租赁订单
     * 业务规则：
     * 1. 逾期扣款 = 日租金 * 2 * 数量 * 新增逾期天数
     * 2. 信用分-100仅在首次逾期时扣除（通过is_overdue字段判断）
     * 3. 使用overdue_deduct_days字段实现幂等，避免重复扣款
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int checkAndDeductOverdueRentals() {
        log.info("开始执行逾期租赁检查任务...");
        
        // 1. 查询所有逾期的租赁订单明细
        List<ShopOrderItem> overdueItems = shopOrderMapper.selectOverdueRentalItems();
        
        if (overdueItems == null || overdueItems.isEmpty()) {
            log.info("未发现逾期租赁订单");
            return 0;
        }
        
        log.info("发现 {} 条逾期租赁明细", overdueItems.size());
        
        // 用于跟踪已处理的订单（用于首次逾期信用分扣减）
        Map<Long, Boolean> processedOrders = new HashMap<>();
        int processedCount = 0;
        
        LocalDate today = LocalDate.now();
        
        for (ShopOrderItem item : overdueItems) {
            try {
                // 计算总逾期天数
                LocalDate rentEndDate = item.getRentEndTime().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                long totalOverdueDays = ChronoUnit.DAYS.between(rentEndDate, today);
                
                if (totalOverdueDays <= 0) {
                    continue; // 未逾期
                }
                
                // 获取已扣逾期天数（幂等处理）
                int alreadyDeductedDays = item.getOverdueDeductDays() != null ? item.getOverdueDeductDays() : 0;
                long newOverdueDays = totalOverdueDays - alreadyDeductedDays;
                
                if (newOverdueDays <= 0) {
                    log.debug("订单明细 {} 已处理到第 {} 天，无需重复扣款", item.getId(), alreadyDeductedDays);
                    continue; // 已处理过这些天数
                }
                
                // 计算本次需扣除金额 = 日租金 * 2 * 数量 * 新增逾期天数
                BigDecimal dailyPenalty = item.getPrice()
                        .multiply(new BigDecimal(2))
                        .multiply(new BigDecimal(item.getQuantity()));
                BigDecimal deductAmount = dailyPenalty.multiply(new BigDecimal(newOverdueDays));
                
                log.info("订单明细 {} - 商品: {}, 日租金: {}, 数量: {}, 新增逾期天数: {}, 扣除金额: {}",
                        item.getId(), item.getProductName(), item.getPrice(), 
                        item.getQuantity(), newOverdueDays, deductAmount);
                
                // 2. 扣减订单押金
                shopOrderMapper.deductDepositAmount(item.getOrderId(), deductAmount);
                
                // 3. 更新已扣逾期天数
                shopOrderMapper.updateOverdueDeductDays(item.getId(), (int) totalOverdueDays);
                
                // 4. 首次逾期时扣除信用分-100（每个订单只扣一次）
                Long orderId = item.getOrderId();
                String isOverdue = item.getOrderIsOverdue();
                
                if (!"1".equals(isOverdue) && !processedOrders.containsKey(orderId)) {
                    // 标记订单为已逾期
                    int updated = shopOrderMapper.markOrderOverdue(orderId);
                    if (updated > 0) {
                        // 扣除信用分
                        Long userId = item.getUserId();
                        appUserService.updateCreditScoreDelta(userId, -100);
                        log.info("订单 {} 首次逾期，用户 {} 信用分-100", orderId, userId);
                    }
                    processedOrders.put(orderId, true);
                }
                
                processedCount++;
                
            } catch (Exception e) {
                log.error("处理逾期订单明细 {} 失败: {}", item.getId(), e.getMessage(), e);
                // 继续处理下一条，不影响整体任务
            }
        }
        
        log.info("逾期租赁检查任务完成，共处理 {} 条逾期明细", processedCount);
        return processedCount;
    }
}
