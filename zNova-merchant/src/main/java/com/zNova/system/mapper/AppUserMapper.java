package com.zNova.system.mapper;

import com.zNova.common.core.domain.entity.AppUser;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
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

    /**
     * 信用分增量更新（原子操作，避免并发问题）
     * 
     * @param userId 用户ID
     * @param delta 增量（正数加分，负数减分）
     * @return 结果
     */
    public int updateCreditScoreDelta(@Param("userId") Long userId, @Param("delta") int delta);

    /**
     * 冻结押金：增加用户的冻结押金金额（下单时调用）
     * 
     * @param userId 用户ID
     * @param amount 冻结金额
     * @return 结果
     */
    public int freezeDeposit(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 解冻押金：减少冻结押金，增加可用余额（商家确认归还后调用）
     * 
     * @param userId 用户ID
     * @param amount 解冻金额（从冻结押金中扣除）
     * @param refundAmount 返还金额（加到余额中，可能因扣款而小于amount）
     * @return 结果
     */
    public int unfreezeDeposit(@Param("userId") Long userId, @Param("amount") BigDecimal amount, @Param("refundAmount") BigDecimal refundAmount);

    /**
     * 扣除冻结押金（逾期扣款时使用，不返还到余额）
     * 
     * @param userId 用户ID
     * @param amount 扣除金额
     * @return 结果
     */
    public int deductFrozenDeposit(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
