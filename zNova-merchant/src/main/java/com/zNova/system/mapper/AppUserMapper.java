package com.zNova.system.mapper;

import com.zNova.common.core.domain.entity.AppUser;

import java.util.List;

/**
 * 前台用户Mapper接口
 * 
 * @author wyz
 * @date 2025-11-21
 */
public interface AppUserMapper 
{
    /**
     * 查询前台用户
     * 
     * @param userId 前台用户主键
     * @return 前台用户
     */
    public AppUser selectAppUserByUserId(Long userId);

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
     * 删除前台用户
     * 
     * @param userId 前台用户主键
     * @return 结果
     */
    public int deleteAppUserByUserId(Long userId);

    /**
     * 批量删除前台用户
     * 
     * @param userIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAppUserByUserIds(Long[] userIds);
}
