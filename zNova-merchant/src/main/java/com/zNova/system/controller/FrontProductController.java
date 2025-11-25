package com.zNova.system.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.common.core.page.TableDataInfo;
import com.zNova.system.domain.BizProduct;
import com.zNova.system.service.IBizProductService;

/**
 * 前台商品Controller - 不应用权限控制
 *
 * @author wyz
 * @date 2025-11-20
 */
@RestController
@RequestMapping("/front/product")
public class FrontProductController extends BaseController
{
    @Autowired
    private IBizProductService bizProductService;

    /**
     * 前台查询商品列表 - 不应用数据权限过滤
     */
    @GetMapping("/list")
    public TableDataInfo list(BizProduct bizProduct)
    {
        startPage();
        // 前台查询时，不应用数据权限过滤，可以查看所有商品
        List<BizProduct> list = bizProductService.selectFrontProductList(bizProduct);
        return getDataTable(list);
    }

    /**
     * 前台获取商品详细信息 - 不应用权限控制
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizProductService.selectBizProductById(id));
    }
}