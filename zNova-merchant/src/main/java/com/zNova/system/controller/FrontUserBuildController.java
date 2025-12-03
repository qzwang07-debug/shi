package com.zNova.system.controller;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.common.core.page.TableDataInfo;
import com.zNova.common.utils.SecurityUtils;
import com.zNova.system.domain.UserBuild;
import com.zNova.system.service.IUserBuildService;

/**
 * 前台用户装机配置Controller
 */
@RestController
@RequestMapping("/front/build")
public class FrontUserBuildController extends BaseController
{
    @Autowired
    private IUserBuildService userBuildService;

    /**
     * 查询当前用户的装机配置列表
     */
    @GetMapping("/list")
    public TableDataInfo list(UserBuild userBuild)
    {
        try {
            // 尝试获取当前用户ID，如果未登录会抛出异常
            Long userId = SecurityUtils.getUserId();
            userBuild.setUserId(userId);
            startPage();
            List<UserBuild> list = userBuildService.selectUserBuildList(userBuild);
            return getDataTable(list);
        } catch (Exception e) {
            // 未登录状态下请求列表，返回空数据，不报错，避免前端弹窗 "会话无效"
            return new TableDataInfo(Collections.emptyList(), 0);
        }
    }

    /**
     * 新增用户装机配置
     */
    @PostMapping
    public AjaxResult add(@RequestBody UserBuild userBuild)
    {
        // 保存操作必须登录，此处不捕获异常，让前端拦截器处理 401 跳转登录
        userBuild.setUserId(SecurityUtils.getUserId());
        return toAjax(userBuildService.insertUserBuild(userBuild));
    }

    /**
     * 修改用户装机配置
     */
    @PutMapping
    public AjaxResult edit(@RequestBody UserBuild userBuild)
    {
        userBuild.setUserId(SecurityUtils.getUserId());
        // 可以在 Service 层增加校验：确保只能修改自己的配置单
        return toAjax(userBuildService.updateUserBuild(userBuild));
    }

    /**
     * 删除用户装机配置
     */
    @DeleteMapping("/{buildIds}")
    public AjaxResult remove(@PathVariable Long[] buildIds)
    {
        // 同样建议在 Service 层校验删除权限
        return toAjax(userBuildService.deleteUserBuildByBuildIds(buildIds));
    }
}