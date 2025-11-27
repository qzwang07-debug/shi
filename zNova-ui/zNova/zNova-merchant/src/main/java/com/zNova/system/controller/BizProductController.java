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
import com.zNova.system.domain.BizProduct;
import com.zNova.system.service.IBizProductService;
import com.zNova.common.utils.poi.ExcelUtil;
import com.zNova.common.core.page.TableDataInfo;

/**
 * 商品Controller
 * 
 * @author wyz
 * @date 2025-11-24
 */
@RestController
@RequestMapping("/system/product")
public class BizProductController extends BaseController
{
    @Autowired
    private IBizProductService bizProductService;

    /**
     * 查询商品列表
     */
    @PreAuthorize("@ss.hasPermi('system:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizProduct bizProduct)
    {
        startPage();
        List<BizProduct> list = bizProductService.selectBizProductList(bizProduct);
        return getDataTable(list);
    }

    /**
     * 导出商品列表
     */
    @PreAuthorize("@ss.hasPermi('system:product:export')")
    @Log(title = "商品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizProduct bizProduct)
    {
        List<BizProduct> list = bizProductService.selectBizProductList(bizProduct);
        ExcelUtil<BizProduct> util = new ExcelUtil<BizProduct>(BizProduct.class);
        util.exportExcel(response, list, "商品数据");
    }

    /**
     * 获取商品详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:product:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizProductService.selectBizProductById(id));
    }

    /**
     * 新增商品
     */
    @PreAuthorize("@ss.hasPermi('system:product:add')")
    @Log(title = "商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizProduct bizProduct)
    {
        return toAjax(bizProductService.insertBizProduct(bizProduct));
    }

    /**
     * 修改商品
     */
    @PreAuthorize("@ss.hasPermi('system:product:edit')")
    @Log(title = "商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizProduct bizProduct)
    {
        return toAjax(bizProductService.updateBizProduct(bizProduct));
    }

    /**
     * 删除商品
     */
    @PreAuthorize("@ss.hasPermi('system:product:remove')")
    @Log(title = "商品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizProductService.deleteBizProductByIds(ids));
    }
}
