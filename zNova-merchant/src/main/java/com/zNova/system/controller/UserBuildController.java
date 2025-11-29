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
import com.zNova.system.domain.UserBuild;
import com.zNova.system.service.IUserBuildService;
import com.zNova.common.utils.poi.ExcelUtil;
import com.zNova.common.core.page.TableDataInfo;

/**
 * 用户装机配置Controller
 * 
 * @author wyz
 * @date 2025-11-29
 */
@RestController
@RequestMapping("/system/build")
public class UserBuildController extends BaseController
{
    @Autowired
    private IUserBuildService userBuildService;

    /**
     * 查询用户装机配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:build:list')")
    @GetMapping("/list")
    public TableDataInfo list(UserBuild userBuild)
    {
        startPage();
        List<UserBuild> list = userBuildService.selectUserBuildList(userBuild);
        return getDataTable(list);
    }

    /**
     * 导出用户装机配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:build:export')")
    @Log(title = "用户装机配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserBuild userBuild)
    {
        List<UserBuild> list = userBuildService.selectUserBuildList(userBuild);
        ExcelUtil<UserBuild> util = new ExcelUtil<UserBuild>(UserBuild.class);
        util.exportExcel(response, list, "用户装机配置数据");
    }

    /**
     * 获取用户装机配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:build:query')")
    @GetMapping(value = "/{buildId}")
    public AjaxResult getInfo(@PathVariable("buildId") Long buildId)
    {
        return success(userBuildService.selectUserBuildByBuildId(buildId));
    }

    /**
     * 新增用户装机配置
     */
    @PreAuthorize("@ss.hasPermi('system:build:add')")
    @Log(title = "用户装机配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserBuild userBuild)
    {
        return toAjax(userBuildService.insertUserBuild(userBuild));
    }

    /**
     * 修改用户装机配置
     */
    @PreAuthorize("@ss.hasPermi('system:build:edit')")
    @Log(title = "用户装机配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserBuild userBuild)
    {
        return toAjax(userBuildService.updateUserBuild(userBuild));
    }

    /**
     * 删除用户装机配置
     */
    @PreAuthorize("@ss.hasPermi('system:build:remove')")
    @Log(title = "用户装机配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{buildIds}")
    public AjaxResult remove(@PathVariable Long[] buildIds)
    {
        return toAjax(userBuildService.deleteUserBuildByBuildIds(buildIds));
    }
}
