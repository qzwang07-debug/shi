package com.zNova.system.controller;

import java.math.BigDecimal;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import com.zNova.common.exception.ServiceException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.zNova.common.annotation.Log;
import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.common.enums.BusinessType;
import com.zNova.system.domain.ShopOrder;
import com.zNova.system.service.IShopOrderService;
import com.zNova.common.utils.poi.ExcelUtil;
import com.zNova.common.core.page.TableDataInfo;
import com.zNova.common.utils.SecurityUtils;

/**
 * 订单主Controller
 * * @author zNova
 * @date 2025-11-24
 */
@RestController
@RequestMapping("/merchant/order")
public class ShopOrderController extends BaseController
{
    @Autowired
    private IShopOrderService shopOrderService;

    /**
     * 查询订单主列表
     * 注意：如遇到 Access Denied，请确保数据库中角色已分配 system:order:list 权限
     * 这里暂时注释权限注解，依赖代码中的 deptId 进行数据隔离
     */
    // @PreAuthorize("@ss.hasPermi('system:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(ShopOrder shopOrder)
    {
        startPage();
        // 权限逻辑：如果不是超级管理员，强制只查自己部门的订单
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            shopOrder.setDeptId(SecurityUtils.getDeptId());
        }
        List<ShopOrder> list = shopOrderService.selectShopOrderList(shopOrder);
        return getDataTable(list);
    }

    /**
     * 导出订单主列表
     */
    @PreAuthorize("@ss.hasPermi('system:order:export')")
    @Log(title = "订单主", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ShopOrder shopOrder)
    {
        // 权限逻辑同上
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            shopOrder.setDeptId(SecurityUtils.getDeptId());
        }
        List<ShopOrder> list = shopOrderService.selectShopOrderList(shopOrder);
        ExcelUtil<ShopOrder> util = new ExcelUtil<ShopOrder>(ShopOrder.class);
        util.exportExcel(response, list, "订单主数据");
    }

    /**
     * 获取订单主详细信息
     */
    // @PreAuthorize("@ss.hasPermi('system:order:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        ShopOrder order = shopOrderService.selectShopOrderByOrderId(orderId);
        // 详情也需要校验权限，防止直接通过ID访问别人的订单
        if (order != null) {
            checkOrderPermission(order);
        }
        return AjaxResult.success(order);
    }

    /**
     * 商家发货
     */
    // @PreAuthorize("@ss.hasPermi('system:order:edit')")
    @Log(title = "订单发货", businessType = BusinessType.UPDATE)
    @PutMapping("/ship/{orderId}")
    public AjaxResult ship(@PathVariable Long orderId)
    {
        ShopOrder order = shopOrderService.selectShopOrderByOrderId(orderId);
        if (order == null) {
            return error("订单不存在");
        }

        // 1. 校验权限 (管理员放行，商户校验deptId)
        checkOrderPermission(order);

        // 2. 校验状态
        if (!"1".equals(order.getStatus())) {
            return error("订单状态不正确，无法发货");
        }

        order.setStatus("2"); // 2=已发货/租赁中
        shopOrderService.updateShopOrder(order);
        return success();
    }

    /**
     * 商家确认收到归还
     * @param orderId 订单ID
     * @param body 请求体，可包含 damageDeduct（损坏扣款金额）
     */
    // @PreAuthorize("@ss.hasPermi('system:order:edit')")
    @Log(title = "确认归还", businessType = BusinessType.UPDATE)
    @PutMapping("/confirmReturn/{orderId}")
    public AjaxResult confirmReturn(@PathVariable Long orderId, @RequestBody(required = false) JSONObject body)
    {
        try {
            // 获取损坏扣款金额（可选参数）
            BigDecimal damageDeduct = null;
            if (body != null && body.containsKey("damageDeduct")) {
                damageDeduct = body.getBigDecimal("damageDeduct");
            }
            
            // 调用服务层方法，实现库存恢复、押金解冻和状态更新
            shopOrderService.confirmReturn(orderId, damageDeduct);
            return success();
        } catch (ServiceException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 内部方法：校验当前登录用户是否有权操作该订单
     */
    private void checkOrderPermission(ShopOrder order) {
        Long userId = SecurityUtils.getUserId();
        // 如果不是管理员
        if (!SecurityUtils.isAdmin(userId)) {
            Long currentDeptId = SecurityUtils.getDeptId();
            // 如果订单没有归属部门，或者归属部门与当前用户不一致，则视为无权限
            if (order.getDeptId() == null || !order.getDeptId().equals(currentDeptId)) {
                throw new RuntimeException("无权操作非本店订单");
            }
        }
    }
    @PostMapping("/cancel")
    public AjaxResult cancel(@RequestBody ShopOrder order) {
        shopOrderService.userCancelOrder(order.getOrderId());
        return success();
    }

    @PostMapping("/applyRefund")
    public AjaxResult applyRefund(@RequestBody ShopOrder order) {
        shopOrderService.userApplyRefund(order.getOrderId());
        return success();
    }

    @PostMapping("/auditRefund")
    public AjaxResult auditRefund(@RequestBody JSONObject body) {
        Long orderId = body.getLong("orderId");
        Boolean pass = body.getBoolean("pass");
        shopOrderService.adminAuditRefund(orderId, pass);
        return success();
    }

    /**
     * 获取订单详情（包含用户信息）
     */
    @GetMapping("/detail/{orderId}")
    public AjaxResult getOrderDetail(@PathVariable("orderId") Long orderId) {
        ShopOrder order = shopOrderService.selectShopOrderByOrderId(orderId);
        if (order == null) {
            return error("订单不存在");
        }

        // 校验权限
        checkOrderPermission(order);

        // 获取用户信息
        com.zNova.common.core.domain.entity.AppUser user = shopOrderService.selectAppUserByUserId(order.getUserId());

        JSONObject result = new JSONObject();
        result.put("order", order);
        result.put("user", user);

        return AjaxResult.success(result);
    }

    /**
     * 修改收货地址
     */
    @Log(title = "修改收货地址", businessType = BusinessType.UPDATE)
    @PutMapping("/updateAddress")
    public AjaxResult updateAddress(@RequestBody ShopOrder shopOrder) {
        ShopOrder order = shopOrderService.selectShopOrderByOrderId(shopOrder.getOrderId());
        if (order == null) {
            return error("订单不存在");
        }

        // 校验权限
        checkOrderPermission(order);

        // 更新收货地址信息
        order.setReceiverName(shopOrder.getReceiverName());
        order.setReceiverPhone(shopOrder.getReceiverPhone());
        order.setReceiverAddress(shopOrder.getReceiverAddress());

        shopOrderService.updateShopOrder(order);
        return success();
    }
}