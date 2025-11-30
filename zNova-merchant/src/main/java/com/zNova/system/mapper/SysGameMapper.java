package com.zNova.system.mapper;

import java.util.List;
import com.zNova.system.domain.SysGame;
import com.zNova.system.domain.SysGameBenchmark;

/**
 * 游戏性能管理Mapper接口
 * 
 * @author wyz
 * @date 2025-11-30
 */
public interface SysGameMapper 
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
     * 删除游戏性能管理
     * 
     * @param gameId 游戏性能管理主键
     * @return 结果
     */
    public int deleteSysGameByGameId(Long gameId);

    /**
     * 批量删除游戏性能管理
     * 
     * @param gameIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysGameByGameIds(Long[] gameIds);

    /**
     * 批量删除游戏性能基准
     * 
     * @param gameIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysGameBenchmarkByGameIds(Long[] gameIds);
    
    /**
     * 批量新增游戏性能基准
     * 
     * @param sysGameBenchmarkList 游戏性能基准列表
     * @return 结果
     */
    public int batchSysGameBenchmark(List<SysGameBenchmark> sysGameBenchmarkList);
    

    /**
     * 通过游戏性能管理主键删除游戏性能基准信息
     * 
     * @param gameId 游戏性能管理ID
     * @return 结果
     */
    public int deleteSysGameBenchmarkByGameId(Long gameId);
}
