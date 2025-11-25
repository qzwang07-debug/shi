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
import com.zNova.system.domain.HardwareCpu;
import com.zNova.system.service.IHardwareCpuService;
import com.zNova.common.utils.poi.ExcelUtil;
import com.zNova.common.core.page.TableDataInfo;

/**
 * CPU硬件Controller
 * 
 * @author wyz
 * @date 2025-11-13
 */
@RestController
@RequestMapping("/system/cpu")
public class HardwareCpuController extends BaseController
{
    @Autowired
    private IHardwareCpuService hardwareCpuService;

    /**
     * 查询CPU硬件列表
     */
    @PreAuthorize("@ss.hasPermi('system:cpu:list')")
    @GetMapping("/list")
    public TableDataInfo list(HardwareCpu hardwareCpu)
    {
        startPage();
        List<HardwareCpu> list = hardwareCpuService.selectHardwareCpuList(hardwareCpu);
        return getDataTable(list);
    }

    /**
     * 导出CPU硬件列表
     */
    @PreAuthorize("@ss.hasPermi('system:cpu:export')")
    @Log(title = "CPU硬件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HardwareCpu hardwareCpu)
    {
        List<HardwareCpu> list = hardwareCpuService.selectHardwareCpuList(hardwareCpu);
        ExcelUtil<HardwareCpu> util = new ExcelUtil<HardwareCpu>(HardwareCpu.class);
        util.exportExcel(response, list, "CPU硬件数据");
    }

    /**
     * 获取CPU硬件详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:cpu:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(hardwareCpuService.selectHardwareCpuById(id));
    }

    /**
     * 新增CPU硬件
     */
    @PreAuthorize("@ss.hasPermi('system:cpu:add')")
    @Log(title = "CPU硬件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HardwareCpu hardwareCpu)
    {
        return toAjax(hardwareCpuService.insertHardwareCpu(hardwareCpu));
    }

    /**
     * 修改CPU硬件
     */
    @PreAuthorize("@ss.hasPermi('system:cpu:edit')")
    @Log(title = "CPU硬件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HardwareCpu hardwareCpu)
    {
        return toAjax(hardwareCpuService.updateHardwareCpu(hardwareCpu));
    }

    /**
     * 删除CPU硬件
     */
    @PreAuthorize("@ss.hasPermi('system:cpu:remove')")
    @Log(title = "CPU硬件", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(hardwareCpuService.deleteHardwareCpuByIds(ids));
    }
}
