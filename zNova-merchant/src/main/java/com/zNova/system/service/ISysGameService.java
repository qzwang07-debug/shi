package com.zNova.system.service;

import java.util.List;
import com.zNova.system.domain.SysGame;

/**
 * 游戏性能管理Service接口
 * 
 * @author wyz
 * @date 2025-11-30
 */
public interface ISysGameService 
{
    /**
     * 查询游戏性能管理
     * 
     * @param gameId 游戏性能管理主键
     * @return 游戏性能管理
     */
    public SysGame selectSysGameByGameId(Long gameId);

    /**
     * 查询游戏性能管理列表
     * 
     * @param sysGame 游戏性能管理
     * @return 游戏性能管理集合
     */
    public List<SysGame> selectSysGameList(SysGame sysGame);

    /**
     * 新增游戏性能管理
     * 
     * @param sysGame 游戏性能管理
     * @return 结果
     */
    public int insertSysGame(SysGame sysGame);

    /**
     * 修改游戏性能管理
     * 
     * @param sysGame 游戏性能管理
     * @return 结果
     */
    public int updateSysGame(SysGame sysGame);

    /**
     * 批量删除游戏性能管理
     * 
     * @param gameIds 需要删除的游戏性能管理主键集合
     * @return 结果
     */
    public int deleteSysGameByGameIds(Long[] gameIds);

    /**
     * 删除游戏性能管理信息
     * 
     * @param gameId 游戏性能管理主键
     * @return 结果
     */
    public int deleteSysGameByGameId(Long gameId);
}
