package com.zNova.system.mapper;

import java.util.List;
import com.zNova.system.domain.HardwareMemory;
import org.apache.ibatis.annotations.Param;

/**
 * 内存规格Mapper接口
 * 
 * @author wyz
 * @date 2025-11-14
 */
public interface HardwareMemoryMapper 
{


    /**
     * 查询内存规格
     * 
     * @param id 内存规格主键
     * @return 内存规格
     */
    public HardwareMemory selectHardwareMemoryById(Long id);

    /**
     * 查询内存规格列表
     * 
     * @param hardwareMemory 内存规格
     * @return 内存规格集合
     */
    public List<HardwareMemory> selectHardwareMemoryList(HardwareMemory hardwareMemory);

    /**
     * 新增内存规格
     * 
     * @param hardwareMemory 内存规格
     * @return 结果
     */
    public int insertHardwareMemory(HardwareMemory hardwareMemory);

    /**
     * 修改内存规格
     * 
     * @param hardwareMemory 内存规格
     * @return 结果
     */
    public int updateHardwareMemory(HardwareMemory hardwareMemory);

    /**
     * 删除内存规格
     * 
     * @param id 内存规格主键
     * @return 结果
     */
    public int deleteHardwareMemoryById(Long id);

    /**
     * 批量删除内存规格
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteHardwareMemoryByIds(Long[] ids);

    // 新增：通过type和frequency查询内存
    HardwareMemory selectHardwareMemoryByTypeAndFrequency(
            @Param("type") String type,
            @Param("frequency") Integer frequency
    );
}
