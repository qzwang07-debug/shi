package com.zNova.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zNova.system.service.IOverdueRentalService;

/**
 * 逾期租赁检查定时任务
 * 
 * 配置方式：在sys_job表中新增记录
 * job_name: 逾期租赁检查
 * job_group: DEFAULT
 * invoke_target: overdueRentalTask.checkAndDeduct()
 * cron_expression: 0 0 1 * * ?  (每天凌晨1点执行)
 * misfire_policy: 2
 * concurrent: 1
 * status: 0
 * 
 * @author zNova
 * @date 2025-01-21
 */
@Component("overdueRentalTask")
public class OverdueRentalTask {
    
    private static final Logger log = LoggerFactory.getLogger(OverdueRentalTask.class);
    
    @Autowired
    private IOverdueRentalService overdueRentalService;
    
    /**
     * 检查并处理逾期租赁订单
     * 每日执行一次，检查所有逾期的租赁订单并扣除押金
     * 
     * 业务规则：
     * 1. 逾期扣款 = 日租金 × 2 × 数量 × 逾期天数
     * 2. 首次逾期时扣除信用分100分
     * 3. 押金扣完后不再扣除（押金余额最低为0）
     */
    public void checkAndDeduct() {
        log.info("========== 逾期租赁检查任务开始 ==========");
        try {
            int processedCount = overdueRentalService.checkAndDeductOverdueRentals();
            log.info("========== 逾期租赁检查任务结束，共处理 {} 条逾期明细 ==========", processedCount);
        } catch (Exception e) {
            log.error("逾期租赁检查任务执行失败: {}", e.getMessage(), e);
            throw e; // 抛出异常让Quartz知道任务失败
        }
    }
}
