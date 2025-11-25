package com.zNova.framework.web.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.zNova.common.constant.CacheConstants;
import com.zNova.common.constant.Constants;
import com.zNova.common.core.domain.model.AppLoginUser;
import com.zNova.common.core.redis.RedisCache;
import com.zNova.common.utils.ServletUtils;
import com.zNova.common.utils.StringUtils;
import com.zNova.common.utils.ip.AddressUtils;
import com.zNova.common.utils.ip.IpUtils;
import com.zNova.common.utils.uuid.IdUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * C端用户token验证处理
 *
 * @author ruoyi
 */
@Component
public class AppTokenService
{
    private static final Logger log = LoggerFactory.getLogger(AppTokenService.class);

    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TWENTY = 20 * 60 * 1000L;

    @Autowired
    private RedisCache redisCache;

    public int getExpireTime()
    {
        return expireTime;
    }

    /**
     * 获取C端用户身份信息
     *
     * @return 用户信息
     */
    public AppLoginUser getLoginUser(HttpServletRequest request)
    {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            try
            {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                String userKey = getAppTokenKey(uuid);
                log.debug("C端Token解析，uuid: {}, userKey: {}", uuid, userKey);
                AppLoginUser user = redisCache.getCacheObject(userKey);
                log.debug("从Redis获取用户: {}", user);
                return user;
            }
            catch (Exception e)
            {
                log.error("获取C端用户信息异常'{}'", e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * 设置C端用户身份信息
     */
    public void setLoginUser(AppLoginUser loginUser)
    {
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNotEmpty(loginUser.getToken()))
        {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除C端用户身份信息
     */
    public void delLoginUser(String token)
    {
        if (StringUtils.isNotEmpty(token))
        {
            String userKey = getAppTokenKey(token);
            redisCache.deleteObject(userKey);
        }
    }

    /**
     * 创建C端用户令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(AppLoginUser loginUser)
    {
        String token = IdUtils.fastUUID();
        loginUser.setToken(token);
        setUserAgent(loginUser);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, token);
        claims.put(Constants.JWT_USERNAME, loginUser.getUsername());
        return createToken(claims);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser 登录信息
     * @return 令牌
     */
    public void verifyToken(AppLoginUser loginUser)
    {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TWENTY)
        {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(AppLoginUser loginUser)
    {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存，使用C端用户key前缀
        String userKey = getAppTokenKey(loginUser.getToken());
        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);

        log.debug("刷新C端用户Token: userKey={}, userId={}, username={}",
                userKey, loginUser.getUserId(), loginUser.getUsername());
    }

    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    public void setUserAgent(AppLoginUser loginUser)
    {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr();
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims)
    {
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token)
    {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token)
    {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    private String getToken(HttpServletRequest request)
    {
        String token = request.getHeader(header);
        log.debug("从请求头获取Token，header: {}, token: {}", header, token);
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX))
        {
            token = token.replace(Constants.TOKEN_PREFIX, "");
            log.debug("移除Bearer前缀后的Token: {}", token);
        }
        else
        {
            log.debug("Token为空或格式不正确，header: {}, token: {}", header, token);
        }
        return token;
    }

    /**
     * 获取C端用户token key
     */
    private String getAppTokenKey(String uuid)
    {
        return CacheConstants.APP_LOGIN_TOKEN_KEY + uuid;
    }
}