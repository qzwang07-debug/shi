package com.zNova.system.service.impl;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zNova.common.utils.DateUtils;
import com.zNova.system.domain.BizProduct;
import com.zNova.system.mapper.BizProductMapper;
import com.zNova.system.service.IStockWarningService;

/**
 * 库存预警服务实现
 * 
 * 业务规则：
 * 1. 当商品库存 <= min_stock（预警阈值）时触发预警
 * 2. 生成预警记录，通知对应商家
 * 3. 支持批量检查和单品检查
 * 
 * @author zNova
 * @date 2025-01-21
 */
@Service
public class StockWarningServiceImpl implements IStockWarningService {
    
    private static final Logger log = LoggerFactory.getLogger(StockWarningServiceImpl.class);
    
    // 预警记录缓存（简化实现，生产环境应使用数据库表）
    private static final List<StockWarningRecord> warningRecords = Collections.synchronizedList(new ArrayList<>());
    private static long warningIdSequence = 0;
    
    @Autowired
    private BizProductMapper bizProductMapper;
    
    /**
     * 检查库存预警并生成预警记录
     */
    @Override
    public int checkAndWarnLowStock() {
        log.info("========== 开始执行库存预警检查任务 ==========");
        
        // 1. 查询所有低库存商品
        List<BizProduct> lowStockProducts = selectLowStockProducts();
        
        if (lowStockProducts == null || lowStockProducts.isEmpty()) {
            log.info("未发现低库存商品");
            return 0;
        }
        
        log.info("发现 {} 个低库存商品", lowStockProducts.size());
        
        int newWarningCount = 0;
        Date now = DateUtils.getNowDate();
        
        for (BizProduct product : lowStockProducts) {
            // 检查是否已有未处理的预警记录
            boolean hasUnhandledWarning = warningRecords.stream()
                    .anyMatch(r -> r.getProductId().equals(product.getId()) && "0".equals(r.getStatus()));
            
            if (!hasUnhandledWarning) {
                // 创建新的预警记录
                StockWarningRecord record = new StockWarningRecord();
                record.setId(++warningIdSequence);
                record.setProductId(product.getId());
                record.setProductName(product.getProductName());
                record.setCurrentStock(product.getStockQuantity());
                record.setMinStock(product.getMinStock());
                record.setDeptId(product.getDeptId());
                record.setDeptName(product.getDeptName());
                record.setStatus("0"); // 未处理
                record.setCreateTime(now);
                
                warningRecords.add(record);
                newWarningCount++;
                
                log.warn("【库存预警】商品: {} (ID: {}), 当前库存: {}, 预警阈值: {}, 商家: {}",
                        product.getProductName(), product.getId(), 
                        product.getStockQuantity(), product.getMinStock(),
                        product.getDeptName());
            }
        }
        
        log.info("========== 库存预警检查完成，新增 {} 条预警记录 ==========", newWarningCount);
        return newWarningCount;
    }
    
    /**
     * 查询低库存商品列表
     */
    @Override
    public List<BizProduct> selectLowStockProducts() {
        // 使用Mapper查询库存低于预警阈值的商品
        return bizProductMapper.selectLowStockProducts(null);
    }
    
    /**
     * 查询低库存商品列表（按部门过滤）
     */
    @Override
    public List<BizProduct> selectLowStockProducts(Long deptId) {
        // 使用Mapper查询库存低于预警阈值的商品
        return bizProductMapper.selectLowStockProducts(deptId);
    }
    
    /**
     * 获取预警记录列表
     */
    @Override
    public List<StockWarningRecord> selectWarningRecords(Long deptId) {
        if (deptId == null) {
            return new ArrayList<>(warningRecords);
        }
        
        List<StockWarningRecord> result = new ArrayList<>();
        for (StockWarningRecord record : warningRecords) {
            if (deptId.equals(record.getDeptId())) {
                result.add(record);
            }
        }
        return result;
    }
    
    /**
     * 标记预警已处理
     */
    @Override
    public int markWarningHandled(Long warningId) {
        for (StockWarningRecord record : warningRecords) {
            if (record.getId().equals(warningId)) {
                record.setStatus("1"); // 已处理
                log.info("预警记录 {} 已标记为已处理", warningId);
                return 1;
            }
        }
        return 0;
    }
}
