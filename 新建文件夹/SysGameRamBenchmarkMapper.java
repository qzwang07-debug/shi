package com.zNova.system.mapper;

import java.util.List;
import com.zNova.system.domain.SysGameRamBenchmark;

/**
 * 内存性能曲线Mapper接口
 * 
 * @author wyz
 * @date 2025-11-30
 */
public interface SysGameRamBenchmarkMapper 
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
     * 删除内存性能曲线
     * 
     * @param id 内存性能曲线主键
     * @return 结果
     */
    public int deleteSysGameRamBenchmarkById(Long id);

    /**
     * 批量删除内存性能曲线
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysGameRamBenchmarkByIds(Long[] ids);
}
