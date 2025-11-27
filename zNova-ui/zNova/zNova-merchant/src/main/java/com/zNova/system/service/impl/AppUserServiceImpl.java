package com.zNova.system.service.impl;

import java.util.List;
import java.util.Date;
import com.zNova.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zNova.system.mapper.AppUserMapper;
import com.zNova.system.service.IAppUserService;
import com.zNova.common.core.domain.entity.AppUser; // 明确导入

/**
 * 前台用户Service业务层处理
 *
 * @author wyz
 * @date 2025-11-21
 */
@Service
public class AppUserServiceImpl implements IAppUserService
{
    @Autowired
    private AppUserMapper appUserMapper;

    /**
     * 查询前台用户
     *
     * @param userId 前台用户主键
     * @return 前台用户
     */
    @Override
    public AppUser selectAppUserByUserId(Long userId)
    {
        // 修复：确保返回类型一致
        return appUserMapper.selectAppUserByUserId(userId);
    }

    /**
     * 根据用户名查询前台用户
     *
     * @param username 用户账号
     * @return 前台用户
     */
    @Override
    public AppUser selectAppUserByUserName(String username)
    {
        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        List<AppUser> users = appUserMapper.selectAppUserList(appUser);
        if (users != null && !users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

    /**
     * 查询前台用户列表
     *
     * @param appUser 前台用户
     * @return 前台用户
     */
    @Override
    public List<AppUser> selectAppUserList(AppUser appUser)
    {
        return appUserMapper.selectAppUserList(appUser);
    }

    /**
     * 新增前台用户
     *
     * @param appUser 前台用户
     * @return 结果
     */
    @Override
    public int insertAppUser(AppUser appUser)
    {
        appUser.setCreateTime(DateUtils.getNowDate());
        return appUserMapper.insertAppUser(appUser);
    }

    /**
     * 修改前台用户
     *
     * @param appUser 前台用户
     * @return 结果
     */
    @Override
    public int updateAppUser(AppUser appUser)
    {
        appUser.setUpdateTime(DateUtils.getNowDate());
        return appUserMapper.updateAppUser(appUser);
    }

    /**
     * 批量删除前台用户
     *
     * @param userIds 需要删除的前台用户主键
     * @return 结果
     */
    @Override
    public int deleteAppUserByUserIds(Long[] userIds)
    {
        return appUserMapper.deleteAppUserByUserIds(userIds);
    }

    /**
     * 删除前台用户信息
     *
     * @param userId 前台用户主键
     * @return 结果
     */
    @Override
    public int deleteAppUserByUserId(Long userId)
    {
        return appUserMapper.deleteAppUserByUserId(userId);
    }

    /**
     * 更新登录信息
     *
     * @param userId 用户ID
     * @param loginIp 登录IP
     * @param loginDate 登录时间
     * @return 结果
     */
    @Override
    public int updateLoginInfo(Long userId, String loginIp, Date loginDate)
    {
        AppUser appUser = new AppUser();
        appUser.setUserId(userId);
        appUser.setLoginIp(loginIp);
        appUser.setLoginDate(loginDate);
        return appUserMapper.updateAppUser(appUser);
    }
}
