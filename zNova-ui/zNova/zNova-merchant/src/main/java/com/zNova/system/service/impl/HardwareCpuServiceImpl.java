package com.zNova.system.service.impl;

import java.util.List;
import com.zNova.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zNova.system.mapper.HardwareCpuMapper;
import com.zNova.system.domain.HardwareCpu;
import com.zNova.system.service.IHardwareCpuService;

/**
 * CPU硬件Service业务层处理
 * 
 * @author wyz
 * @date 2025-11-13
 */
@Service
public class HardwareCpuServiceImpl implements IHardwareCpuService 
{
    @Autowired
    private HardwareCpuMapper hardwareCpuMapper;

    /**
     * 查询CPU硬件
     * 
     * @param id CPU硬件主键
     * @return CPU硬件
     */
    @Override
    public HardwareCpu selectHardwareCpuById(Long id)
    {
        return hardwareCpuMapper.selectHardwareCpuById(id);
    }

    /**
     * 查询CPU硬件列表
     * 
     * @param hardwareCpu CPU硬件
     * @return CPU硬件
     */
    @Override
    public List<HardwareCpu> selectHardwareCpuList(HardwareCpu hardwareCpu)
    {
        return hardwareCpuMapper.selectHardwareCpuList(hardwareCpu);
    }

    /**
     * 新增CPU硬件
     * 
     * @param hardwareCpu CPU硬件
     * @return 结果
     */
    @Override
    public int insertHardwareCpu(HardwareCpu hardwareCpu)
    {
        hardwareCpu.setCreateTime(DateUtils.getNowDate());
        return hardwareCpuMapper.insertHardwareCpu(hardwareCpu);
    }

    /**
     * 修改CPU硬件
     * 
     * @param hardwareCpu CPU硬件
     * @return 结果
     */
    @Override
    public int updateHardwareCpu(HardwareCpu hardwareCpu)
    {
        hardwareCpu.setUpdateTime(DateUtils.getNowDate());
        return hardwareCpuMapper.updateHardwareCpu(hardwareCpu);
    }

    /**
     * 批量删除CPU硬件
     * 
     * @param ids 需要删除的CPU硬件主键
     * @return 结果
     */
    @Override
    public int deleteHardwareCpuByIds(Long[] ids)
    {
        return hardwareCpuMapper.deleteHardwareCpuByIds(ids);
    }

    /**
     * 删除CPU硬件信息
     * 
     * @param id CPU硬件主键
     * @return 结果
     */
    @Override
    public int deleteHardwareCpuById(Long id)
    {
        return hardwareCpuMapper.deleteHardwareCpuById(id);
    }
}
