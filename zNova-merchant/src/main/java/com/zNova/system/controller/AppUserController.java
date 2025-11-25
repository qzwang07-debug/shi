package com.zNova.system.controller;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;

import com.zNova.common.core.domain.entity.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.common.core.domain.model.AppLoginUser;
import com.zNova.common.utils.StringUtils;
import com.zNova.framework.web.service.AppLoginService;
import com.zNova.framework.web.service.AppTokenService;
import com.zNova.system.service.IAppUserService;
import com.zNova.framework.web.service.AppRegisterService;
import com.zNova.common.core.domain.model.RegisterBody;

/**
 * C端用户控制器
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/app")
public class AppUserController
{
    @Autowired
    private IAppUserService appUserService;

    @Autowired
    private AppLoginService appLoginService;

    @Autowired
    private AppTokenService appTokenService;

    @Autowired
    private AppRegisterService appRegisterService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * C端用户登录
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody AppUser appUser)
    {
        String username = appUser.getUsername();
        String password = appUser.getPassword();
        String code = appUser.getCode();
        String uuid = appUser.getUuid();

        // 调用C端登录服务
        String token = appLoginService.login(username, password, code, uuid);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("token", token);
        return ajax;
    }

    /**
     * C端用户注册
     */
    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody registerBody)
    {
        String msg = appRegisterService.register(registerBody);
        if (StringUtils.isEmpty(msg))
        {
            return AjaxResult.success("注册成功");
        }
        else
        {
            return AjaxResult.error(msg);
        }
    }

    /**
     * 获取当前C端用户信息
     */
    @GetMapping("/user/info")
    public AjaxResult getUserInfo(HttpServletRequest request)
    {
        AppLoginUser loginUser = appTokenService.getLoginUser(request);
        if (loginUser == null)
        {
            return AjaxResult.error("未登录或token已过期");
        }

        Object userObj = loginUser.getUser();
        if (userObj instanceof AppUser) {
            AppUser user = (AppUser) userObj;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("user", user);
            ajax.put("userId", user.getUserId());
            ajax.put("username", user.getUsername());
            ajax.put("nickname", user.getNickname());
            ajax.put("avatar", user.getAvatar());
            return ajax;
        }

        return AjaxResult.error("用户信息解析失败");
    }
}
