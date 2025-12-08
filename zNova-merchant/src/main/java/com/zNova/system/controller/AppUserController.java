package com.zNova.system.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import org.springframework.web.multipart.MultipartFile;

import com.zNova.common.config.RuoYiConfig;
import com.zNova.common.core.domain.entity.AppUser;
import com.zNova.common.utils.file.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    private FileStorageService fileStorageService;

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

            // 设置默认值
            if (user.getCreditScore() == null) {
                user.setCreditScore(700);
            }
            if (user.getBalance() == null) {
                user.setBalance(BigDecimal.ZERO);
            }

            AjaxResult ajax = AjaxResult.success();
            ajax.put("user", user);
            ajax.put("userId", user.getUserId());
            ajax.put("username", user.getUsername());
            ajax.put("nickname", user.getNickname());
            ajax.put("avatar", user.getAvatar());
            ajax.put("creditScore", user.getCreditScore());
            ajax.put("balance", user.getBalance());
            return ajax;
        }

        return AjaxResult.error("用户信息解析失败");
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/user/profile")
    public AjaxResult updateProfile(@RequestBody AppUser appUser, HttpServletRequest request)
    {
        AppLoginUser loginUser = appTokenService.getLoginUser(request);
        if (loginUser == null)
        {
            return AjaxResult.error("未登录或token已过期");
        }

        Object userObj = loginUser.getUser();
        if (userObj instanceof AppUser) {
            AppUser user = (AppUser) userObj;

            // 只允许修改昵称、性别、手机号
            user.setNickname(appUser.getNickname());
            user.setSex(appUser.getSex());
            user.setPhonenumber(appUser.getPhonenumber());

            int result = appUserService.updateAppUser(user);
            if (result > 0) {
                // 更新登录用户信息
                loginUser.setUser(user);
                appTokenService.setLoginUser(loginUser);
                return AjaxResult.success("修改成功");
            }
            return AjaxResult.error("修改失败");
        }

        return AjaxResult.error("用户信息解析失败");
    }

    /**
     * 修改密码
     */
    @PutMapping("/user/pwd")
    public AjaxResult updatePassword(@RequestBody Map<String, String> params, HttpServletRequest request)
    {
        AppLoginUser loginUser = appTokenService.getLoginUser(request);
        if (loginUser == null)
        {
            return AjaxResult.error("未登录或token已过期");
        }

        Object userObj = loginUser.getUser();
        if (userObj instanceof AppUser) {
            AppUser user = (AppUser) userObj;

            String oldPassword = params.get("oldPassword");
            String newPassword = params.get("newPassword");

            // 校验旧密码
            if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
                return AjaxResult.error("旧密码错误");
            }

            // 更新新密码
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            int result = appUserService.updateAppUser(user);
            if (result > 0) {
                // 更新登录用户信息
                loginUser.setUser(user);
                appTokenService.setLoginUser(loginUser);
                return AjaxResult.success("密码修改成功");
            }
            return AjaxResult.error("密码修改失败");
        }

        return AjaxResult.error("用户信息解析失败");
    }

    /**
     * 头像上传
     */
    @PostMapping("/user/avatar")
    public AjaxResult uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request)
    {
        AppLoginUser loginUser = appTokenService.getLoginUser(request);
        if (loginUser == null)
        {
            return AjaxResult.error("未登录或token已过期");
        }

        Object userObj = loginUser.getUser();
        if (userObj instanceof AppUser) {
            AppUser user = (AppUser) userObj;

            try {
                // 使用 FileStorageService 上传到阿里云 OSS (无需手动拼接路径，插件会处理)
                // 这里的 setPath 是相对路径，例如 "avatar/"
                String objectPath = "avatar/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/";

                FileInfo fileInfo = fileStorageService.of(file)
                        .setPath(objectPath)
                        .upload();

                String avatar = fileInfo.getUrl();

                // 更新用户头像
                user.setAvatar(avatar);
                int result = appUserService.updateAppUser(user);
                if (result > 0) {
                    // 更新登录用户信息
                    loginUser.setUser(user);
                    appTokenService.setLoginUser(loginUser);

                    AjaxResult ajax = AjaxResult.success("头像上传成功");
                    ajax.put("avatar", avatar);
                    return ajax;
                }
                return AjaxResult.error("头像上传失败");
            } catch (Exception e) {
                return AjaxResult.error("头像上传失败：" + e.getMessage());
            }
        }

        return AjaxResult.error("用户信息解析失败");
    }
}
