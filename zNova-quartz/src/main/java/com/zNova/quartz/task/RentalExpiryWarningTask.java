package com.zNova.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zNova.system.service.IRentalExpiryWarningService;

/**
 * 租期到期提醒定时任务
 * 
 * 配置方式：在sys_job表中新增记录
 * job_name: 租期到期提醒
 * job_group: DEFAULT
 * invoke_target: rentalExpiryWarningTask.checkExpiringSoon()
 * cron_expression: 0 0 9 * * ?  (每天早上9点执行)
 * misfire_policy: 2
 * concurrent: 1
 * status: 0
 * 
 * @author zNova
 * @date 2025-01-22
 */
@Component("rentalExpiryWarningTask")
public class RentalExpiryWarningTask {
    
    private static final Logger log = LoggerFactory.getLogger(RentalExpiryWarningTask.class);
    
    @Autowired
    private IRentalExpiryWarningService rentalExpiryWarningService;
    
    /**
     * 检查即将到期的租赁订单（2天内到期）
     * 每日执行一次，标记即将到期的订单，供前端登录时查询显示
     */
    public void checkExpiringSoon() {
        log.info("========== 租期到期提醒检查任务开始 ==========");
        try {
            int count = rentalExpiryWarningService.markExpiringSoonOrders(2);
            log.info("========== 租期到期提醒检查任务结束，发现 {} 条即将到期订单 ==========", count);
        } catch (Exception e) {
            log.error("租期到期提醒检查任务执行失败: {}", e.getMessage(), e);
            throw e;
        }
    }
}
