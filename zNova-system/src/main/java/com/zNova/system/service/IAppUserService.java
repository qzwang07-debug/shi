package com.zNova.system.service;

import com.zNova.common.core.domain.entity.AppUser;

import java.util.Date;
import java.util.List;

/**
 * 前台用户Service接口
 *
 * @author wyz
 * @date 2025-11-21
 */
public interface IAppUserService
{
    /**
     * 查询前台用户
     *
     * @param userId 前台用户主键
     * @return 前台用户
     */
    public AppUser selectAppUserByUserId(Long userId);

    /**
     * 根据用户名查询前台用户
     *
     * @param username 用户账号
     * @return 前台用户
     */
    public AppUser selectAppUserByUserName(String username);

    /**
     * 查询前台用户列表
     *
     * @param appUser 前台用户
     * @return 前台用户集合
     */
    public List<AppUser> selectAppUserList(AppUser appUser);

    /**
     * 新增前台用户
     *
     * @param appUser 前台用户
     * @return 结果
     */
    public int insertAppUser(AppUser appUser);

    /**
     * 修改前台用户
     *
     * @param appUser 前台用户
     * @return 结果
     */
    public int updateAppUser(AppUser appUser);

    /**
     * 批量删除前台用户
     *
     * @param userIds 需要删除的前台用户主键集合
     * @return 结果
     */
    public int deleteAppUserByUserIds(Long[] userIds);

    /**
     * 删除前台用户信息
     *
     * @param userId 前台用户主键
     * @return 结果
     */
    public int deleteAppUserByUserId(Long userId);

    /**
     * 更新登录信息
     *
     * @param userId 用户ID
     * @param loginIp 登录IP
     * @param loginDate 登录时间
     * @return 结果
     */
    public int updateLoginInfo(Long userId, String loginIp, Date loginDate);
}
