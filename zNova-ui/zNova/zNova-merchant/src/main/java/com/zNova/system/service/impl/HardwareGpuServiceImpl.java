package com.zNova.system.service.impl;

import java.util.List;
import com.zNova.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zNova.system.mapper.HardwareGpuMapper;
import com.zNova.system.domain.HardwareGpu;
import com.zNova.system.service.IHardwareGpuService;

/**
 * 显卡硬件Service业务层处理
 * 
 * @author wyz
 * @date 2025-11-13
 */
@Service
public class HardwareGpuServiceImpl implements IHardwareGpuService 
{
    @Autowired
    private HardwareGpuMapper hardwareGpuMapper;

    /**
     * 查询显卡硬件
     * 
     * @param id 显卡硬件主键
     * @return 显卡硬件
     */
    @Override
    public HardwareGpu selectHardwareGpuById(Long id)
    {
        return hardwareGpuMapper.selectHardwareGpuById(id);
    }

    /**
     * 查询显卡硬件列表
     * 
     * @param hardwareGpu 显卡硬件
     * @return 显卡硬件
     */
    @Override
    public List<HardwareGpu> selectHardwareGpuList(HardwareGpu hardwareGpu)
    {
        return hardwareGpuMapper.selectHardwareGpuList(hardwareGpu);
    }

    /**
     * 新增显卡硬件
     * 
     * @param hardwareGpu 显卡硬件
     * @return 结果
     */
    @Override
    public int insertHardwareGpu(HardwareGpu hardwareGpu)
    {
        hardwareGpu.setCreateTime(DateUtils.getNowDate());
        return hardwareGpuMapper.insertHardwareGpu(hardwareGpu);
    }

    /**
     * 修改显卡硬件
     * 
     * @param hardwareGpu 显卡硬件
     * @return 结果
     */
    @Override
    public int updateHardwareGpu(HardwareGpu hardwareGpu)
    {
        hardwareGpu.setUpdateTime(DateUtils.getNowDate());
        return hardwareGpuMapper.updateHardwareGpu(hardwareGpu);
    }

    /**
     * 批量删除显卡硬件
     * 
     * @param ids 需要删除的显卡硬件主键
     * @return 结果
     */
    @Override
    public int deleteHardwareGpuByIds(Long[] ids)
    {
        return hardwareGpuMapper.deleteHardwareGpuByIds(ids);
    }

    /**
     * 删除显卡硬件信息
     * 
     * @param id 显卡硬件主键
     * @return 结果
     */
    @Override
    public int deleteHardwareGpuById(Long id)
    {
        return hardwareGpuMapper.deleteHardwareGpuById(id);
    }
}
