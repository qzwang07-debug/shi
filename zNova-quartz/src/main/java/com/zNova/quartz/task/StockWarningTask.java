package com.zNova.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zNova.system.service.IStockWarningService;

/**
 * 库存预警检查定时任务
 * 
 * 配置方式：在sys_job表中新增记录
 * job_name: 库存预警检查
 * job_group: DEFAULT
 * invoke_target: stockWarningTask.checkStock()
 * cron_expression: 0 0 9 * * ?  (每天早上9点执行)
 * misfire_policy: 2
 * concurrent: 1
 * status: 0
 * 
 * @author zNova
 * @date 2025-01-21
 */
@Component("stockWarningTask")
public class StockWarningTask {
    
    private static final Logger log = LoggerFactory.getLogger(StockWarningTask.class);
    
    @Autowired
    private IStockWarningService stockWarningService;
    
    /**
     * 检查库存预警
     * 每日执行一次，检查所有商品库存并生成预警记录
     * 
     * 业务规则：
     * 1. 当商品库存 <= min_stock（预警阈值）时触发预警
     * 2. 生成预警记录，供商家后台查看
     * 3. 可通过日志查看预警详情
     */
    public void checkStock() {
        log.info("========== 库存预警检查任务开始 ==========");
        try {
            int warningCount = stockWarningService.checkAndWarnLowStock();
            log.info("========== 库存预警检查任务结束，新增 {} 条预警记录 ==========", warningCount);
        } catch (Exception e) {
            log.error("库存预警检查任务执行失败: {}", e.getMessage(), e);
            throw e;
        }
    }
}
