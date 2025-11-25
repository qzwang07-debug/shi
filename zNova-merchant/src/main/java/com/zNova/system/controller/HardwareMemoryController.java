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
import com.zNova.system.domain.HardwareMemory;
import com.zNova.system.service.IHardwareMemoryService;
import com.zNova.common.utils.poi.ExcelUtil;
import com.zNova.common.core.page.TableDataInfo;

/**
 * 内存规格Controller
 * 
 * @author wyz
 * @date 2025-11-14
 */
@RestController
@RequestMapping("/system/memory")
public class HardwareMemoryController extends BaseController
{
    @Autowired
    private IHardwareMemoryService hardwareMemoryService;

    /**
     * 查询内存规格列表
     */
    @PreAuthorize("@ss.hasPermi('system:memory:list')")
    @GetMapping("/list")
    public TableDataInfo list(HardwareMemory hardwareMemory)
    {
        startPage();
        List<HardwareMemory> list = hardwareMemoryService.selectHardwareMemoryList(hardwareMemory);
        return getDataTable(list);
    }

    /**
     * 导出内存规格列表
     */
    @PreAuthorize("@ss.hasPermi('system:memory:export')")
    @Log(title = "内存规格", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HardwareMemory hardwareMemory)
    {
        List<HardwareMemory> list = hardwareMemoryService.selectHardwareMemoryList(hardwareMemory);
        ExcelUtil<HardwareMemory> util = new ExcelUtil<HardwareMemory>(HardwareMemory.class);
        util.exportExcel(response, list, "内存规格数据");
    }

    /**
     * 获取内存规格详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:memory:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(hardwareMemoryService.selectHardwareMemoryById(id));
    }

    /**
     * 新增内存规格
     */
    @PreAuthorize("@ss.hasPermi('system:memory:add')")
    @Log(title = "内存规格", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HardwareMemory hardwareMemory)
    {
        return toAjax(hardwareMemoryService.insertHardwareMemory(hardwareMemory));
    }

    /**
     * 修改内存规格
     */
    @PreAuthorize("@ss.hasPermi('system:memory:edit')")
    @Log(title = "内存规格", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HardwareMemory hardwareMemory)
    {
        return toAjax(hardwareMemoryService.updateHardwareMemory(hardwareMemory));
    }

    /**
     * 删除内存规格
     */
    @PreAuthorize("@ss.hasPermi('system:memory:remove')")
    @Log(title = "内存规格", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(hardwareMemoryService.deleteHardwareMemoryByIds(ids));
    }
}
