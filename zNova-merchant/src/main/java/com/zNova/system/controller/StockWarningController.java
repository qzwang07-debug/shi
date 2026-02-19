package com.zNova.system.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.common.utils.SecurityUtils;
import com.zNova.system.domain.BizProduct;
import com.zNova.system.service.IStockWarningService;
import com.zNova.system.service.IStockWarningService.StockWarningRecord;

/**
 * 库存预警Controller
 * 供商家后台查看库存预警信息
 * 
 * @author zNova
 * @date 2025-01-21
 */
@RestController
@RequestMapping("/merchant/stock/warning")
public class StockWarningController extends BaseController {
    
    @Autowired
    private IStockWarningService stockWarningService;
    
    /**
     * 查询库存预警记录列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:stock:warning:list')")
    @GetMapping("/list")
    public AjaxResult list() {
        Long deptId = null;
        // 非管理员只能查看本部门的预警
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            deptId = SecurityUtils.getDeptId();
        }
        List<StockWarningRecord> records = stockWarningService.selectWarningRecords(deptId);
        return success(records);
    }
    
    /**
     * 查询低库存商品列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:stock:warning:list')")
    @GetMapping("/products")
    public AjaxResult lowStockProducts() {
        Long deptId = null;
        // 非管理员只能查看本部门的预警商品
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            deptId = SecurityUtils.getDeptId();
        }
        List<BizProduct> products = stockWarningService.selectLowStockProducts(deptId);
        return success(products);
    }
    
    /**
     * 标记预警已处理
     */
    @PreAuthorize("@ss.hasPermi('merchant:stock:warning:edit')")
    @PutMapping("/handle/{warningId}")
    public AjaxResult handleWarning(@PathVariable Long warningId) {
        int result = stockWarningService.markWarningHandled(warningId);
        return result > 0 ? success() : error("处理失败");
    }
    
    /**
     * 手动触发库存检查（管理员功能）
     */
    @PreAuthorize("@ss.hasRole('admin')")
    @PostMapping("/check")
    public AjaxResult manualCheck() {
        int count = stockWarningService.checkAndWarnLowStock();
        return success("检查完成，发现 " + count + " 条新预警");
    }
}
