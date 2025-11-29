package com.zNova.system.service.impl;

import java.util.List;
import com.zNova.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zNova.system.mapper.UserBuildMapper;
import com.zNova.system.domain.UserBuild;
import com.zNova.system.service.IUserBuildService;

/**
 * 用户装机配置Service业务层处理
 * 
 * @author wyz
 * @date 2025-11-29
 */
@Service
public class UserBuildServiceImpl implements IUserBuildService 
{
    @Autowired
    private UserBuildMapper userBuildMapper;

    /**
     * 查询用户装机配置
     * 
     * @param buildId 用户装机配置主键
     * @return 用户装机配置
     */
    @Override
    public UserBuild selectUserBuildByBuildId(Long buildId)
    {
        return userBuildMapper.selectUserBuildByBuildId(buildId);
    }

    /**
     * 查询用户装机配置列表
     * 
     * @param userBuild 用户装机配置
     * @return 用户装机配置
     */
    @Override
    public List<UserBuild> selectUserBuildList(UserBuild userBuild)
    {
        return userBuildMapper.selectUserBuildList(userBuild);
    }

    /**
     * 新增用户装机配置
     * 
     * @param userBuild 用户装机配置
     * @return 结果
     */
    @Override
    public int insertUserBuild(UserBuild userBuild)
    {
        userBuild.setCreateTime(DateUtils.getNowDate());
        return userBuildMapper.insertUserBuild(userBuild);
    }

    /**
     * 修改用户装机配置
     * 
     * @param userBuild 用户装机配置
     * @return 结果
     */
    @Override
    public int updateUserBuild(UserBuild userBuild)
    {
        userBuild.setUpdateTime(DateUtils.getNowDate());
        return userBuildMapper.updateUserBuild(userBuild);
    }

    /**
     * 批量删除用户装机配置
     * 
     * @param buildIds 需要删除的用户装机配置主键
     * @return 结果
     */
    @Override
    public int deleteUserBuildByBuildIds(Long[] buildIds)
    {
        return userBuildMapper.deleteUserBuildByBuildIds(buildIds);
    }

    /**
     * 删除用户装机配置信息
     * 
     * @param buildId 用户装机配置主键
     * @return 结果
     */
    @Override
    public int deleteUserBuildByBuildId(Long buildId)
    {
        return userBuildMapper.deleteUserBuildByBuildId(buildId);
    }
}
