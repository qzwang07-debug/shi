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
import com.zNova.system.domain.SysGameRamBenchmark;
import com.zNova.system.service.ISysGameRamBenchmarkService;
import com.zNova.common.utils.poi.ExcelUtil;
import com.zNova.common.core.page.TableDataInfo;

/**
 * 内存性能曲线Controller
 * 
 * @author wyz
 * @date 2025-11-30
 */
@RestController
@RequestMapping("/system/ram")
public class SysGameRamBenchmarkController extends BaseController
{
    @Autowired
    private ISysGameRamBenchmarkService sysGameRamBenchmarkService;

    /**
     * 查询内存性能曲线列表
     */
    @PreAuthorize("@ss.hasPermi('system:ram:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysGameRamBenchmark sysGameRamBenchmark)
    {
        startPage();
        List<SysGameRamBenchmark> list = sysGameRamBenchmarkService.selectSysGameRamBenchmarkList(sysGameRamBenchmark);
        return getDataTable(list);
    }

    /**
     * 导出内存性能曲线列表
     */
    @PreAuthorize("@ss.hasPermi('system:ram:export')")
    @Log(title = "内存性能曲线", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysGameRamBenchmark sysGameRamBenchmark)
    {
        List<SysGameRamBenchmark> list = sysGameRamBenchmarkService.selectSysGameRamBenchmarkList(sysGameRamBenchmark);
        ExcelUtil<SysGameRamBenchmark> util = new ExcelUtil<SysGameRamBenchmark>(SysGameRamBenchmark.class);
        util.exportExcel(response, list, "内存性能曲线数据");
    }

    /**
     * 获取内存性能曲线详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:ram:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysGameRamBenchmarkService.selectSysGameRamBenchmarkById(id));
    }

    /**
     * 新增内存性能曲线
     */
    @PreAuthorize("@ss.hasPermi('system:ram:add')")
    @Log(title = "内存性能曲线", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysGameRamBenchmark sysGameRamBenchmark)
    {
        return toAjax(sysGameRamBenchmarkService.insertSysGameRamBenchmark(sysGameRamBenchmark));
    }

    /**
     * 修改内存性能曲线
     */
    @PreAuthorize("@ss.hasPermi('system:ram:edit')")
    @Log(title = "内存性能曲线", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysGameRamBenchmark sysGameRamBenchmark)
    {
        return toAjax(sysGameRamBenchmarkService.updateSysGameRamBenchmark(sysGameRamBenchmark));
    }

    /**
     * 删除内存性能曲线
     */
    @PreAuthorize("@ss.hasPermi('system:ram:remove')")
    @Log(title = "内存性能曲线", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysGameRamBenchmarkService.deleteSysGameRamBenchmarkByIds(ids));
    }
}
