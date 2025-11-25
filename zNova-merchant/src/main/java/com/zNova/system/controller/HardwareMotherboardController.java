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
import com.zNova.system.domain.HardwareMotherboard;
import com.zNova.system.service.IHardwareMotherboardService;
import com.zNova.common.utils.poi.ExcelUtil;
import com.zNova.common.core.page.TableDataInfo;

/**
 * 主板规格Controller
 * 
 * @author ruoyi
 * @date 2025-11-13
 */
@RestController
@RequestMapping("/system/motherboard")
public class HardwareMotherboardController extends BaseController
{
    @Autowired
    private IHardwareMotherboardService hardwareMotherboardService;

    /**
     * 查询主板规格列表
     */
    @PreAuthorize("@ss.hasPermi('system:motherboard:list')")
    @GetMapping("/list")
    public TableDataInfo list(HardwareMotherboard hardwareMotherboard)
    {
        startPage();
        List<HardwareMotherboard> list = hardwareMotherboardService.selectHardwareMotherboardList(hardwareMotherboard);
        return getDataTable(list);
    }

    /**
     * 导出主板规格列表
     */
    @PreAuthorize("@ss.hasPermi('system:motherboard:export')")
    @Log(title = "主板规格", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HardwareMotherboard hardwareMotherboard)
    {
        List<HardwareMotherboard> list = hardwareMotherboardService.selectHardwareMotherboardList(hardwareMotherboard);
        ExcelUtil<HardwareMotherboard> util = new ExcelUtil<HardwareMotherboard>(HardwareMotherboard.class);
        util.exportExcel(response, list, "主板规格数据");
    }

    /**
     * 获取主板规格详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:motherboard:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(hardwareMotherboardService.selectHardwareMotherboardById(id));
    }

    /**
     * 新增主板规格
     */
    @PreAuthorize("@ss.hasPermi('system:motherboard:add')")
    @Log(title = "主板规格", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HardwareMotherboard hardwareMotherboard)
    {
        return toAjax(hardwareMotherboardService.insertHardwareMotherboard(hardwareMotherboard));
    }

    /**
     * 修改主板规格
     */
    @PreAuthorize("@ss.hasPermi('system:motherboard:edit')")
    @Log(title = "主板规格", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HardwareMotherboard hardwareMotherboard)
    {
        return toAjax(hardwareMotherboardService.updateHardwareMotherboard(hardwareMotherboard));
    }

    /**
     * 删除主板规格
     */
    @PreAuthorize("@ss.hasPermi('system:motherboard:remove')")
    @Log(title = "主板规格", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(hardwareMotherboardService.deleteHardwareMotherboardByIds(ids));
    }
}
