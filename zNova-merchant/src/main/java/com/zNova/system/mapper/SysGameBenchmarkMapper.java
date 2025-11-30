package com.zNova.system.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.zNova.system.domain.SysGameBenchmark;
import org.apache.ibatis.annotations.Param;

/**
 * 游戏跑分基准Mapper接口
 * * @author ruoyi
 */
public interface SysGameBenchmarkMapper
{
    /**
     * 查询游戏跑分基准
     * * @param id 游戏跑分基准主键
     * @return 游戏跑分基准
     */
    public SysGameBenchmark selectSysGameBenchmarkById(Long id);

    /**
     * 查询游戏跑分基准列表
     * * @param sysGameBenchmark 游戏跑分基准
     * @return 游戏跑分基准集合
     */
    public List<SysGameBenchmark> selectSysGameBenchmarkList(SysGameBenchmark sysGameBenchmark);

    /**
     * 新增游戏跑分基准
     * * @param sysGameBenchmark 游戏跑分基准
     * @return 结果
     */
    public int insertSysGameBenchmark(SysGameBenchmark sysGameBenchmark);

    /**
     * 修改游戏跑分基准
     * * @param sysGameBenchmark 游戏跑分基准
     * @return 结果
     */
    public int updateSysGameBenchmark(SysGameBenchmark sysGameBenchmark);

    /**
     * 删除游戏跑分基准
     * * @param id 游戏跑分基准主键
     * @return 结果
     */
    public int deleteSysGameBenchmarkById(Long id);

    /**
     * 批量删除游戏跑分基准
     * * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysGameBenchmarkByIds(Long[] ids);

    /**
     * 根据分数区间查找基准配置 (新增核心方法)
     * * @param gameId 游戏ID
     * @param platformType 平台类型 (CPU_INTEL, GPU_NVIDIA等)
     * @param resolution 分辨率 (1080P, 2K, 4K)
     * @param score 硬件跑分
     * @return 匹配的基准数据
     */
    public SysGameBenchmark selectBenchmarkByScore(@Param("gameId") Long gameId,
                                                   @Param("platformType") String platformType,
                                                   @Param("resolution") String resolution,
                                                   @Param("score") BigDecimal score);
}