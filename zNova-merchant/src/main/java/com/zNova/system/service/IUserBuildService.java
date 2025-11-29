package com.zNova.system.service;

import java.util.List;
import com.zNova.system.domain.UserBuild;

/**
 * 用户装机配置Service接口
 * 
 * @author wyz
 * @date 2025-11-29
 */
public interface IUserBuildService 
{
    /**
     * 查询用户装机配置
     * 
     * @param buildId 用户装机配置主键
     * @return 用户装机配置
     */
    public UserBuild selectUserBuildByBuildId(Long buildId);

    /**
     * 查询用户装机配置列表
     * 
     * @param userBuild 用户装机配置
     * @return 用户装机配置集合
     */
    public List<UserBuild> selectUserBuildList(UserBuild userBuild);

    /**
     * 新增用户装机配置
     * 
     * @param userBuild 用户装机配置
     * @return 结果
     */
    public int insertUserBuild(UserBuild userBuild);

    /**
     * 修改用户装机配置
     * 
     * @param userBuild 用户装机配置
     * @return 结果
     */
    public int updateUserBuild(UserBuild userBuild);

    /**
     * 批量删除用户装机配置
     * 
     * @param buildIds 需要删除的用户装机配置主键集合
     * @return 结果
     */
    public int deleteUserBuildByBuildIds(Long[] buildIds);

    /**
     * 删除用户装机配置信息
     * 
     * @param buildId 用户装机配置主键
     * @return 结果
     */
    public int deleteUserBuildByBuildId(Long buildId);
}
