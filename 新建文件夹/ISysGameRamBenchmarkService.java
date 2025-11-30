package com.zNova.system.service;

import java.util.List;
import com.zNova.system.domain.SysGameRamBenchmark;

/**
 * 内存性能曲线Service接口
 * 
 * @author wyz
 * @date 2025-11-30
 */
public interface ISysGameRamBenchmarkService 
{
    /**
     * 查询内存性能曲线
     * 
     * @param id 内存性能曲线主键
     * @return 内存性能曲线
     */
    public SysGameRamBenchmark selectSysGameRamBenchmarkById(Long id);

    /**
     * 查询内存性能曲线列表
     * 
     * @param sysGameRamBenchmark 内存性能曲线
     * @return 内存性能曲线集合
     */
    public List<SysGameRamBenchmark> selectSysGameRamBenchmarkList(SysGameRamBenchmark sysGameRamBenchmark);

    /**
     * 新增内存性能曲线
     * 
     * @param sysGameRamBenchmark 内存性能曲线
     * @return 结果
     */
    public int insertSysGameRamBenchmark(SysGameRamBenchmark sysGameRamBenchmark);

    /**
     * 修改内存性能曲线
     * 
     * @param sysGameRamBenchmark 内存性能曲线
     * @return 结果
     */
    public int updateSysGameRamBenchmark(SysGameRamBenchmark sysGameRamBenchmark);

    /**
     * 批量删除内存性能曲线
     * 
     * @param ids 需要删除的内存性能曲线主键集合
     * @return 结果
     */
    public int deleteSysGameRamBenchmarkByIds(Long[] ids);

    /**
     * 删除内存性能曲线信息
     * 
     * @param id 内存性能曲线主键
     * @return 结果
     */
    public int deleteSysGameRamBenchmarkById(Long id);
}
