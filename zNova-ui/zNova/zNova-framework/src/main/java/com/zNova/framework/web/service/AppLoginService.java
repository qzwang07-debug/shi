package com.zNova.framework.web.service;

import com.zNova.common.constant.CacheConstants;
import com.zNova.common.constant.Constants;
import com.zNova.common.constant.UserConstants;
import com.zNova.common.core.domain.entity.AppUser;
import com.zNova.common.core.domain.model.AppLoginUser;
import com.zNova.common.core.redis.RedisCache;
import com.zNova.common.exception.user.*;
import com.zNova.common.utils.DateUtils;
import com.zNova.common.utils.MessageUtils;
import com.zNova.common.utils.StringUtils;
import com.zNova.common.utils.ip.IpUtils;
import com.zNova.framework.manager.AsyncManager;
import com.zNova.framework.manager.factory.AsyncFactory;
import com.zNova.system.service.IAppUserService;
import com.zNova.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * C端用户登录校验服务
 * (注意：这里类名必须是 AppLoginService，不要 Impl)
 *
 * @author ruoyi
 */
@Component  // 使用 @Component 或 @Service 都可以，推荐 Component 保持和若依 SysLoginService 一致
public class AppLoginService
{
    @Autowired
    private IAppUserService appUserService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AppTokenService appTokenService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysConfigService configService;

    /**
     * 登录验证逻辑
     *
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return Token字符串
     */
    public String login(String username, String password, String code, String uuid)
    {
        // 1. 验证码校验
        validateCaptcha(username, code, uuid);

        // 2. 登录前置校验 (判空、IP黑名单等)
        loginPreCheck(username, password);

        // 3. 用户验证 (查库、校验密码、校验状态)
        AppUser user = validateUser(username, password);

        // 4. 记录登录成功信息
        recordLoginInfo(user.getUserId());

        // 5. 生成并返回 Token
        AppLoginUser loginUser = new AppLoginUser(user.getUserId(), user, null);
        return appTokenService.createToken(loginUser);
    }

    /**
     * 核心校验逻辑
     */
    public AppUser validateUser(String username, String password)
    {
        // 查询用户信息
        AppUser user = appUserService.selectAppUserByUserName(username);

        if (StringUtils.isNull(user))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.not.exists")));
            throw new UserNotExistsException();
        }

        // 用户状态校验 (删除)
        if ("2".equals(user.getDelFlag()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.delete")));
            throw new UserNotExistsException();
        }

        // 用户状态校验 (停用)
        if ("1".equals(user.getStatus()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.blocked")));
            throw new UserNotExistsException();
        }

        // 密码校验（使用BCrypt）
        // 注意：数据库里的密码必须是加密过的，如果存的是明文，这里会一直报错
        if (!bCryptPasswordEncoder.matches(password, user.getPassword()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }

        return user;
    }

    /**
     * 记录登录信息
     */
    public void recordLoginInfo(Long userId)
    {
        appUserService.updateLoginInfo(userId, IpUtils.getIpAddr(), DateUtils.getNowDate());
    }

    /**
     * 校验验证码
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
            String captcha = redisCache.getCacheObject(verifyKey);
            if (captcha == null)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
                throw new CaptchaExpireException();
            }
            redisCache.deleteObject(verifyKey);
            if (!code.equalsIgnoreCase(captcha))
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
                throw new CaptchaException();
            }
        }
    }

    /**
     * 登录前置校验
     */
    public void loginPreCheck(String username, String password)
    {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null")));
            throw new UserNotExistsException();
        }
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
    }
}
