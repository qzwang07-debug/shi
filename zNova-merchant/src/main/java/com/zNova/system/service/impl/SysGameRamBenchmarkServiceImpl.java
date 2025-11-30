package com.zNova.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zNova.system.mapper.SysGameRamBenchmarkMapper;
import com.zNova.system.domain.SysGameRamBenchmark;
import com.zNova.system.service.ISysGameRamBenchmarkService;

/**
 * 内存性能曲线Service业务层处理
 * 
 * @author wyz
 * @date 2025-11-30
 */
@Service
public class SysGameRamBenchmarkServiceImpl implements ISysGameRamBenchmarkService 
{
    @Autowired
    private SysGameRamBenchmarkMapper sysGameRamBenchmarkMapper;

    /**
     * 查询内存性能曲线
     * 
     * @param id 内存性能曲线主键
     * @return 内存性能曲线
     */
    @Override
    public SysGameRamBenchmark selectSysGameRamBenchmarkById(Long id)
    {
        return sysGameRamBenchmarkMapper.selectSysGameRamBenchmarkById(id);
    }

    /**
     * 查询内存性能曲线列表
     * 
     * @param sysGameRamBenchmark 内存性能曲线
     * @return 内存性能曲线
     */
    @Override
    public List<SysGameRamBenchmark> selectSysGameRamBenchmarkList(SysGameRamBenchmark sysGameRamBenchmark)
    {
        return sysGameRamBenchmarkMapper.selectSysGameRamBenchmarkList(sysGameRamBenchmark);
    }

    /**
     * 新增内存性能曲线
     * 
     * @param sysGameRamBenchmark 内存性能曲线
     * @return 结果
     */
    @Override
    public int insertSysGameRamBenchmark(SysGameRamBenchmark sysGameRamBenchmark)
    {
        return sysGameRamBenchmarkMapper.insertSysGameRamBenchmark(sysGameRamBenchmark);
    }

    /**
     * 修改内存性能曲线
     * 
     * @param sysGameRamBenchmark 内存性能曲线
     * @return 结果
     */
    @Override
    public int updateSysGameRamBenchmark(SysGameRamBenchmark sysGameRamBenchmark)
    {
        return sysGameRamBenchmarkMapper.updateSysGameRamBenchmark(sysGameRamBenchmark);
    }

    /**
     * 批量删除内存性能曲线
     * 
     * @param ids 需要删除的内存性能曲线主键
     * @return 结果
     */
    @Override
    public int deleteSysGameRamBenchmarkByIds(Long[] ids)
    {
        return sysGameRamBenchmarkMapper.deleteSysGameRamBenchmarkByIds(ids);
    }

    /**
     * 删除内存性能曲线信息
     * 
     * @param id 内存性能曲线主键
     * @return 结果
     */
    @Override
    public int deleteSysGameRamBenchmarkById(Long id)
    {
        return sysGameRamBenchmarkMapper.deleteSysGameRamBenchmarkById(id);
    }
}
