package com.zNova.system.service;

import java.util.List;
import com.zNova.system.domain.HardwareMemory;

/**
 * 内存规格Service接口
 * 
 * @author wyz
 * @date 2025-11-14
 */
public interface IHardwareMemoryService 
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
     * 批量删除内存规格
     * 
     * @param ids 需要删除的内存规格主键集合
     * @return 结果
     */
    public int deleteHardwareMemoryByIds(Long[] ids);

    /**
     * 删除内存规格信息
     * 
     * @param id 内存规格主键
     * @return 结果
     */
    public int deleteHardwareMemoryById(Long id);
}
