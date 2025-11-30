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
import com.zNova.system.domain.SysGame;
import com.zNova.system.service.ISysGameService;
import com.zNova.common.utils.poi.ExcelUtil;
import com.zNova.common.core.page.TableDataInfo;

/**
 * 游戏性能管理Controller
 * 
 * @author wyz
 * @date 2025-11-30
 */
@RestController
@RequestMapping("/system/gameInfo")
public class SysGameController extends BaseController
{
    @Autowired
    private ISysGameService sysGameService;

    /**
     * 查询游戏性能管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:gameInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysGame sysGame)
    {
        startPage();
        List<SysGame> list = sysGameService.selectSysGameList(sysGame);
        return getDataTable(list);
    }

    /**
     * 导出游戏性能管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:gameInfo:export')")
    @Log(title = "游戏性能管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysGame sysGame)
    {
        List<SysGame> list = sysGameService.selectSysGameList(sysGame);
        ExcelUtil<SysGame> util = new ExcelUtil<SysGame>(SysGame.class);
        util.exportExcel(response, list, "游戏性能管理数据");
    }

    /**
     * 获取游戏性能管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:gameInfo:query')")
    @GetMapping(value = "/{gameId}")
    public AjaxResult getInfo(@PathVariable("gameId") Long gameId)
    {
        return success(sysGameService.selectSysGameByGameId(gameId));
    }

    /**
     * 新增游戏性能管理
     */
    @PreAuthorize("@ss.hasPermi('system:gameInfo:add')")
    @Log(title = "游戏性能管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysGame sysGame)
    {
        return toAjax(sysGameService.insertSysGame(sysGame));
    }

    /**
     * 修改游戏性能管理
     */
    @PreAuthorize("@ss.hasPermi('system:gameInfo:edit')")
    @Log(title = "游戏性能管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysGame sysGame)
    {
        return toAjax(sysGameService.updateSysGame(sysGame));
    }

    /**
     * 删除游戏性能管理
     */
    @PreAuthorize("@ss.hasPermi('system:gameInfo:remove')")
    @Log(title = "游戏性能管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{gameIds}")
    public AjaxResult remove(@PathVariable Long[] gameIds)
    {
        return toAjax(sysGameService.deleteSysGameByGameIds(gameIds));
    }
}
