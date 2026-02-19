package com.zNova.framework.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.zNova.common.constant.CacheConstants;
import com.zNova.common.constant.Constants;
import com.zNova.common.constant.UserConstants;
import com.zNova.common.core.domain.entity.AppUser;
import com.zNova.common.core.domain.model.RegisterBody;
import com.zNova.common.core.redis.RedisCache;
import com.zNova.common.exception.user.CaptchaException;
import com.zNova.common.exception.user.CaptchaExpireException;
import com.zNova.common.utils.DateUtils;
import com.zNova.common.utils.MessageUtils;
import com.zNova.common.utils.SecurityUtils;
import com.zNova.common.utils.StringUtils;
import com.zNova.framework.manager.AsyncManager;
import com.zNova.framework.manager.factory.AsyncFactory;
import com.zNova.system.service.IAppUserService;
import com.zNova.system.service.ISysConfigService;
import java.math.BigDecimal;

/**

 C端用户注册服务

 @author ruoyi
 */
@Component
public class AppRegisterService
{
    @Autowired
    private IAppUserService appUserService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    /**

     C端用户注册
     */
    public String register(RegisterBody registerBody)
    {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();
        AppUser appUser = new AppUser();
        appUser.setUsername(username);

// 验证码开关
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username))
        {
            msg = "用户名不能为空";
        }
        else if (StringUtils.isEmpty(password))
        {
            msg = "用户密码不能为空";
        }
        else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            msg = "账户长度必须在2到20个字符之间";
        }
        else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            msg = "密码长度必须在5到20个字符之间";
        }
        else if (appUserService.selectAppUserByUserName(username) != null)
        {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        }
        else
        {
// 设置昵称，直接使用用户名作为昵称
            appUser.setNickname(username);

            // 密码加密
            appUser.setPassword(SecurityUtils.encryptPassword(password));

            // 设置默认值
            appUser.setStatus("0");
            appUser.setDelFlag("0");
            appUser.setCreateTime(DateUtils.getNowDate());
            // 初始化信用分为500
            appUser.setCreditScore(500);
            // 初始化余额为0
            appUser.setBalance(BigDecimal.ZERO);

            // 插入用户
            int rows = appUserService.insertAppUser(appUser);
            if (rows > 0)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success")));
            }
            else
            {
                msg = "注册失败,请联系系统管理人员";
            }


        }
        return msg;
    }

    /**

     校验验证码
     @param username 用户名
     @param code 验证码
     @param uuid 唯一标识
     @return 结果 */ public void validateCaptcha(String username, String code, String uuid) {
         String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey); redisCache.deleteObject(verifyKey);
        if (captcha == null) { throw new CaptchaExpireException(); }
        if (!code.equalsIgnoreCase(captcha)) { throw new CaptchaException();
        }
    }
}
