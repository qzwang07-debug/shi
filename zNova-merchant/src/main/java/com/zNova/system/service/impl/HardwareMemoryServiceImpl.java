package com.zNova.system.service.impl;

import java.util.List;
import com.zNova.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zNova.system.mapper.HardwareMemoryMapper;
import com.zNova.system.domain.HardwareMemory;
import com.zNova.system.service.IHardwareMemoryService;

/**
 * 内存规格Service业务层处理
 * 
 * @author wyz
 * @date 2025-11-14
 */
@Service
public class HardwareMemoryServiceImpl implements IHardwareMemoryService 
{
    @Autowired
    private HardwareMemoryMapper hardwareMemoryMapper;

    /**
     * 查询内存规格
     * 
     * @param id 内存规格主键
     * @return 内存规格
     */
    @Override
    public HardwareMemory selectHardwareMemoryById(Long id)
    {
        return hardwareMemoryMapper.selectHardwareMemoryById(id);
    }

    /**
     * 查询内存规格列表
     * 
     * @param hardwareMemory 内存规格
     * @return 内存规格
     */
    @Override
    public List<HardwareMemory> selectHardwareMemoryList(HardwareMemory hardwareMemory)
    {
        return hardwareMemoryMapper.selectHardwareMemoryList(hardwareMemory);
    }

    /**
     * 新增内存规格
     * 
     * @param hardwareMemory 内存规格
     * @return 结果
     */
    @Override
    public int insertHardwareMemory(HardwareMemory hardwareMemory)
    {
        hardwareMemory.setCreateTime(DateUtils.getNowDate());
        return hardwareMemoryMapper.insertHardwareMemory(hardwareMemory);
    }

    /**
     * 修改内存规格
     * 
     * @param hardwareMemory 内存规格
     * @return 结果
     */
    @Override
    public int updateHardwareMemory(HardwareMemory hardwareMemory)
    {
        hardwareMemory.setUpdateTime(DateUtils.getNowDate());
        return hardwareMemoryMapper.updateHardwareMemory(hardwareMemory);
    }

    /**
     * 批量删除内存规格
     * 
     * @param ids 需要删除的内存规格主键
     * @return 结果
     */
    @Override
    public int deleteHardwareMemoryByIds(Long[] ids)
    {
        return hardwareMemoryMapper.deleteHardwareMemoryByIds(ids);
    }

    /**
     * 删除内存规格信息
     * 
     * @param id 内存规格主键
     * @return 结果
     */
    @Override
    public int deleteHardwareMemoryById(Long id)
    {
        return hardwareMemoryMapper.deleteHardwareMemoryById(id);
    }
}
