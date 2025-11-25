package com.zNova.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zNova.common.annotation.Log;
import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.common.enums.BusinessType;
import com.zNova.system.domain.ShopOrder;
import com.zNova.system.service.IShopOrderService;
import com.zNova.common.utils.poi.ExcelUtil;
import com.zNova.common.core.page.TableDataInfo;

/**
 * 订单主Controller
 * 
 * @author wyz
 * @date 2025-11-24
 */
@RestController
@RequestMapping("/system/ShopOrder")
public class ShopOrderController extends BaseController
{
    @Autowired
    private IShopOrderService shopOrderService;

    /**
     * 查询订单主列表
     */
    @PreAuthorize("@ss.hasPermi('system:ShopOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(ShopOrder shopOrder)
    {
        startPage();
        List<ShopOrder> list = shopOrderService.selectShopOrderList(shopOrder);
        return getDataTable(list);
    }

    /**
     * 导出订单主列表
     */
    @PreAuthorize("@ss.hasPermi('system:ShopOrder:export')")
    @Log(title = "订单主", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ShopOrder shopOrder)
    {
        List<ShopOrder> list = shopOrderService.selectShopOrderList(shopOrder);
        ExcelUtil<ShopOrder> util = new ExcelUtil<ShopOrder>(ShopOrder.class);
        util.exportExcel(response, list, "订单主数据");
    }

    /**
     * 获取订单主详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:ShopOrder:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        return success(shopOrderService.selectShopOrderByOrderId(orderId));
    }

    /**
     * 新增订单主
     */
    @PreAuthorize("@ss.hasPermi('system:ShopOrder:add')")
    @Log(title = "订单主", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ShopOrder shopOrder)
    {
        return toAjax(shopOrderService.insertShopOrder(shopOrder));
    }

    /**
     * 修改订单主
     */
    @PreAuthorize("@ss.hasPermi('system:ShopOrder:edit')")
    @Log(title = "订单主", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ShopOrder shopOrder)
    {
        return toAjax(shopOrderService.updateShopOrder(shopOrder));
    }

    /**
     * 删除订单主
     */
    @PreAuthorize("@ss.hasPermi('system:ShopOrder:remove')")
    @Log(title = "订单主", businessType = BusinessType.DELETE)
	@DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(shopOrderService.deleteShopOrderByOrderIds(orderIds));
    }
}
