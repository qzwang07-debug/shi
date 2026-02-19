package com.zNova.system.service;

import java.util.List;
import com.zNova.system.domain.BizProduct;

/**
 * 库存预警服务接口
 * 
 * @author zNova
 * @date 2025-01-21
 */
public interface IStockWarningService 
{
    /**
     * 检查库存预警并生成预警记录
     * 
     * @return 预警商品数量
     */
    int checkAndWarnLowStock();

    /**
     * 查询低库存商品列表
     * 
     * @return 低库存商品列表
     */
    List<BizProduct> selectLowStockProducts();
    
    /**
     * 查询低库存商品列表（按部门过滤）
     * 
     * @param deptId 部门ID，null则查询全部
     * @return 低库存商品列表
     */
    List<BizProduct> selectLowStockProducts(Long deptId);

    /**
     * 获取预警记录列表
     * 
     * @param deptId 部门ID（商家ID），null则查询全部
     * @return 预警记录列表
     */
    List<StockWarningRecord> selectWarningRecords(Long deptId);

    /**
     * 标记预警已处理
     * 
     * @param warningId 预警记录ID
     * @return 结果
     */
    int markWarningHandled(Long warningId);

    /**
     * 库存预警记录内部类
     */
    public static class StockWarningRecord {
        private Long id;
        private Long productId;
        private String productName;
        private Long currentStock;
        private Long minStock;
        private Long deptId;
        private String deptName;
        private String status; // 0=未处理 1=已处理
        private java.util.Date createTime;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Long getCurrentStock() { return currentStock; }
        public void setCurrentStock(Long currentStock) { this.currentStock = currentStock; }
        public Long getMinStock() { return minStock; }
        public void setMinStock(Long minStock) { this.minStock = minStock; }
        public Long getDeptId() { return deptId; }
        public void setDeptId(Long deptId) { this.deptId = deptId; }
        public String getDeptName() { return deptName; }
        public void setDeptName(String deptName) { this.deptName = deptName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public java.util.Date getCreateTime() { return createTime; }
        public void setCreateTime(java.util.Date createTime) { this.createTime = createTime; }
    }
}
