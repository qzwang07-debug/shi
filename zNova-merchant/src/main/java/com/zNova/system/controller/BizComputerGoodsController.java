package com.zNova.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.zNova.system.domain.vo.PerformanceScoreVO;
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
import com.zNova.system.domain.BizComputerGoods;
import com.zNova.system.service.IBizComputerGoodsService;
import com.zNova.common.utils.poi.ExcelUtil;
import com.zNova.common.core.page.TableDataInfo;

/**
 * 电脑商品Controller
 *
 * @author wyz
 * @date 2025-11-09
 */
@RestController
@RequestMapping("/system/computer")
public class BizComputerGoodsController extends BaseController
{
    @Autowired
    private IBizComputerGoodsService bizComputerGoodsService;

    /**
     * 查询电脑商品列表（带分页）
     */
    @PreAuthorize("@ss.hasPermi('system:computer:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizComputerGoods bizComputerGoods)
    {
        // 调用BaseController中的分页方法
        startPage();
        List<BizComputerGoods> list = bizComputerGoodsService.selectBizComputerGoodsList(bizComputerGoods);
        return getDataTable(list);
    }

    /**
     * 导出电脑商品列表
     */
    @PreAuthorize("@ss.hasPermi('system:computer:export')")
    @Log(title = "电脑商品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizComputerGoods bizComputerGoods)
    {
        List<BizComputerGoods> list = bizComputerGoodsService.selectBizComputerGoodsList(bizComputerGoods);
        ExcelUtil<BizComputerGoods> util = new ExcelUtil<BizComputerGoods>(BizComputerGoods.class);
        util.exportExcel(response, list, "电脑商品数据");
    }

    /**
     * 获取电脑商品详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:computer:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizComputerGoodsService.selectBizComputerGoodsById(id));
    }

    /**
     * 新增电脑商品
     */
    @PreAuthorize("@ss.hasPermi('system:computer:add')")
    @Log(title = "电脑商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizComputerGoods bizComputerGoods)
    {
        return toAjax(bizComputerGoodsService.insertBizComputerGoods(bizComputerGoods));
    }

    /**
     * 修改电脑商品
     */
    @PreAuthorize("@ss.hasPermi('system:computer:edit')")
    @Log(title = "电脑商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizComputerGoods bizComputerGoods)
    {
        return toAjax(bizComputerGoodsService.updateBizComputerGoods(bizComputerGoods));
    }

    /**
     * 删除电脑商品
     */
    @PreAuthorize("@ss.hasPermi('system:computer:remove')")
    @Log(title = "电脑商品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizComputerGoodsService.deleteBizComputerGoodsByIds(ids));
    }

    /**
     * 获取商品性能评分
     */
    @GetMapping("/performance/{id}")
    public AjaxResult getPerformanceScore(@PathVariable Long id) {
        BizComputerGoods goods = bizComputerGoodsService.getPerformanceById(id);
        PerformanceScoreVO scoreVO = bizComputerGoodsService.calculateScore(goods);
        return AjaxResult.success(scoreVO);
}
}
