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
import com.zNova.system.domain.HardwareGpu;
import com.zNova.system.service.IHardwareGpuService;
import com.zNova.common.utils.poi.ExcelUtil;
import com.zNova.common.core.page.TableDataInfo;

/**
 * 显卡硬件Controller
 * 
 * @author wyz
 * @date 2025-11-13
 */
@RestController
@RequestMapping("/system/gpu")
public class HardwareGpuController extends BaseController
{
    @Autowired
    private IHardwareGpuService hardwareGpuService;

    /**
     * 查询显卡硬件列表
     */
    @PreAuthorize("@ss.hasPermi('system:gpu:list')")
    @GetMapping("/list")
    public TableDataInfo list(HardwareGpu hardwareGpu)
    {
        startPage();
        List<HardwareGpu> list = hardwareGpuService.selectHardwareGpuList(hardwareGpu);
        return getDataTable(list);
    }

    /**
     * 导出显卡硬件列表
     */
    @PreAuthorize("@ss.hasPermi('system:gpu:export')")
    @Log(title = "显卡硬件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HardwareGpu hardwareGpu)
    {
        List<HardwareGpu> list = hardwareGpuService.selectHardwareGpuList(hardwareGpu);
        ExcelUtil<HardwareGpu> util = new ExcelUtil<HardwareGpu>(HardwareGpu.class);
        util.exportExcel(response, list, "显卡硬件数据");
    }

    /**
     * 获取显卡硬件详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:gpu:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(hardwareGpuService.selectHardwareGpuById(id));
    }

    /**
     * 新增显卡硬件
     */
    @PreAuthorize("@ss.hasPermi('system:gpu:add')")
    @Log(title = "显卡硬件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HardwareGpu hardwareGpu)
    {
        return toAjax(hardwareGpuService.insertHardwareGpu(hardwareGpu));
    }

    /**
     * 修改显卡硬件
     */
    @PreAuthorize("@ss.hasPermi('system:gpu:edit')")
    @Log(title = "显卡硬件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HardwareGpu hardwareGpu)
    {
        return toAjax(hardwareGpuService.updateHardwareGpu(hardwareGpu));
    }

    /**
     * 删除显卡硬件
     */
    @PreAuthorize("@ss.hasPermi('system:gpu:remove')")
    @Log(title = "显卡硬件", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(hardwareGpuService.deleteHardwareGpuByIds(ids));
    }
}
