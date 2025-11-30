package com.zNova.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.zNova.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.zNova.system.domain.SysGameBenchmark;
import com.zNova.system.mapper.SysGameMapper;
import com.zNova.system.domain.SysGame;
import com.zNova.system.service.ISysGameService;

/**
 * 游戏性能管理Service业务层处理
 * 
 * @author wyz
 * @date 2025-11-30
 */
@Service
public class SysGameServiceImpl implements ISysGameService 
{
    @Autowired
    private SysGameMapper sysGameMapper;

    /**
     * 查询游戏性能管理
     * 
     * @param gameId 游戏性能管理主键
     * @return 游戏性能管理
     */
    @Override
    public SysGame selectSysGameByGameId(Long gameId)
    {
        return sysGameMapper.selectSysGameByGameId(gameId);
    }

    /**
     * 查询游戏性能管理列表
     * 
     * @param sysGame 游戏性能管理
     * @return 游戏性能管理
     */
    @Override
    public List<SysGame> selectSysGameList(SysGame sysGame)
    {
        return sysGameMapper.selectSysGameList(sysGame);
    }

    /**
     * 新增游戏性能管理
     * 
     * @param sysGame 游戏性能管理
     * @return 结果
     */
    @Transactional
    @Override
    public int insertSysGame(SysGame sysGame)
    {
        int rows = sysGameMapper.insertSysGame(sysGame);
        insertSysGameBenchmark(sysGame);
        return rows;
    }

    /**
     * 修改游戏性能管理
     * 
     * @param sysGame 游戏性能管理
     * @return 结果
     */
    @Transactional
    @Override
    public int updateSysGame(SysGame sysGame)
    {
        sysGameMapper.deleteSysGameBenchmarkByGameId(sysGame.getGameId());
        insertSysGameBenchmark(sysGame);
        return sysGameMapper.updateSysGame(sysGame);
    }

    /**
     * 批量删除游戏性能管理
     * 
     * @param gameIds 需要删除的游戏性能管理主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSysGameByGameIds(Long[] gameIds)
    {
        sysGameMapper.deleteSysGameBenchmarkByGameIds(gameIds);
        return sysGameMapper.deleteSysGameByGameIds(gameIds);
    }

    /**
     * 删除游戏性能管理信息
     * 
     * @param gameId 游戏性能管理主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSysGameByGameId(Long gameId)
    {
        sysGameMapper.deleteSysGameBenchmarkByGameId(gameId);
        return sysGameMapper.deleteSysGameByGameId(gameId);
    }

    /**
     * 新增游戏性能基准信息
     * 
     * @param sysGame 游戏性能管理对象
     */
    public void insertSysGameBenchmark(SysGame sysGame)
    {
        List<SysGameBenchmark> sysGameBenchmarkList = sysGame.getSysGameBenchmarkList();
        Long gameId = sysGame.getGameId();
        if (StringUtils.isNotNull(sysGameBenchmarkList))
        {
            List<SysGameBenchmark> list = new ArrayList<SysGameBenchmark>();
            for (SysGameBenchmark sysGameBenchmark : sysGameBenchmarkList)
            {
                sysGameBenchmark.setGameId(gameId);
                list.add(sysGameBenchmark);
            }
            if (list.size() > 0)
            {
                sysGameMapper.batchSysGameBenchmark(list);
            }
        }
    }
}
