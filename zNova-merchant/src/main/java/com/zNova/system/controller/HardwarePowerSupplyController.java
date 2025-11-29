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
import com.zNova.system.domain.HardwarePowerSupply;
import com.zNova.system.service.IHardwarePowerSupplyService;
import com.zNova.common.utils.poi.ExcelUtil;
import com.zNova.common.core.page.TableDataInfo;

/**
 * 电源规格Controller
 * 
 * @author ruoyi
 * @date 2025-11-29
 */
@RestController
@RequestMapping("/system/supply")
public class HardwarePowerSupplyController extends BaseController
{
    @Autowired
    private IHardwarePowerSupplyService hardwarePowerSupplyService;

    /**
     * 查询电源规格列表
     */
    @PreAuthorize("@ss.hasPermi('system:supply:list')")
    @GetMapping("/list")
    public TableDataInfo list(HardwarePowerSupply hardwarePowerSupply)
    {
        startPage();
        List<HardwarePowerSupply> list = hardwarePowerSupplyService.selectHardwarePowerSupplyList(hardwarePowerSupply);
        return getDataTable(list);
    }

    /**
     * 导出电源规格列表
     */
    @PreAuthorize("@ss.hasPermi('system:supply:export')")
    @Log(title = "电源规格", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HardwarePowerSupply hardwarePowerSupply)
    {
        List<HardwarePowerSupply> list = hardwarePowerSupplyService.selectHardwarePowerSupplyList(hardwarePowerSupply);
        ExcelUtil<HardwarePowerSupply> util = new ExcelUtil<HardwarePowerSupply>(HardwarePowerSupply.class);
        util.exportExcel(response, list, "电源规格数据");
    }

    /**
     * 获取电源规格详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:supply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(hardwarePowerSupplyService.selectHardwarePowerSupplyById(id));
    }

    /**
     * 新增电源规格
     */
    @PreAuthorize("@ss.hasPermi('system:supply:add')")
    @Log(title = "电源规格", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HardwarePowerSupply hardwarePowerSupply)
    {
        return toAjax(hardwarePowerSupplyService.insertHardwarePowerSupply(hardwarePowerSupply));
    }

    /**
     * 修改电源规格
     */
    @PreAuthorize("@ss.hasPermi('system:supply:edit')")
    @Log(title = "电源规格", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HardwarePowerSupply hardwarePowerSupply)
    {
        return toAjax(hardwarePowerSupplyService.updateHardwarePowerSupply(hardwarePowerSupply));
    }

    /**
     * 删除电源规格
     */
    @PreAuthorize("@ss.hasPermi('system:supply:remove')")
    @Log(title = "电源规格", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(hardwarePowerSupplyService.deleteHardwarePowerSupplyByIds(ids));
    }
}
