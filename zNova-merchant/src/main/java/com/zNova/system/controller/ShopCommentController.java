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
import com.zNova.system.domain.ShopComment;
import com.zNova.system.service.IShopCommentService;
import com.zNova.common.utils.poi.ExcelUtil;
import com.zNova.common.core.page.TableDataInfo;

/**
 * 商品评价Controller
 * 
 * @author wyz
 * @date 2025-11-21
 */
@RestController
@RequestMapping("/system/comment")
public class ShopCommentController extends BaseController
{
    @Autowired
    private IShopCommentService shopCommentService;

    /**
     * 查询商品评价列表
     */
    @PreAuthorize("@ss.hasPermi('system:comment:list')")
    @GetMapping("/list")
    public TableDataInfo list(ShopComment shopComment)
    {
        startPage();
        List<ShopComment> list = shopCommentService.selectShopCommentList(shopComment);
        return getDataTable(list);
    }

    /**
     * 导出商品评价列表
     */
    @PreAuthorize("@ss.hasPermi('system:comment:export')")
    @Log(title = "商品评价", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ShopComment shopComment)
    {
        List<ShopComment> list = shopCommentService.selectShopCommentList(shopComment);
        ExcelUtil<ShopComment> util = new ExcelUtil<ShopComment>(ShopComment.class);
        util.exportExcel(response, list, "商品评价数据");
    }

    /**
     * 获取商品评价详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:comment:query')")
    @GetMapping(value = "/{commentId}")
    public AjaxResult getInfo(@PathVariable("commentId") Long commentId)
    {
        return success(shopCommentService.selectShopCommentByCommentId(commentId));
    }

    /**
     * 新增商品评价
     */
    @PreAuthorize("@ss.hasPermi('system:comment:add')")
    @Log(title = "商品评价", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ShopComment shopComment)
    {
        return toAjax(shopCommentService.insertShopComment(shopComment));
    }

    /**
     * 修改商品评价
     */
    @PreAuthorize("@ss.hasPermi('system:comment:edit')")
    @Log(title = "商品评价", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ShopComment shopComment)
    {
        return toAjax(shopCommentService.updateShopComment(shopComment));
    }

    /**
     * 删除商品评价
     */
    @PreAuthorize("@ss.hasPermi('system:comment:remove')")
    @Log(title = "商品评价", businessType = BusinessType.DELETE)
	@DeleteMapping("/{commentIds}")
    public AjaxResult remove(@PathVariable Long[] commentIds)
    {
        return toAjax(shopCommentService.deleteShopCommentByCommentIds(commentIds));
    }
}
