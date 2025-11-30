package com.zNova.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
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
     * 新增用户装机配置
     */
    @PostMapping
    public AjaxResult add(@RequestBody UserBuild userBuild)
    {
        // 自动设置当前登录用户ID
        userBuild.setUserId(SecurityUtils.getUserId());
        return toAjax(userBuildService.insertUserBuild(userBuild));
    }
}