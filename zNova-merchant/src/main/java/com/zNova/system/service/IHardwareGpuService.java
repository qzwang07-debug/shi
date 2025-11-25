package com.zNova.system.service;

import java.util.List;
import com.zNova.system.domain.HardwareGpu;

/**
 * 显卡硬件Service接口
 * 
 * @author wyz
 * @date 2025-11-13
 */
public interface IHardwareGpuService 
{
    /**
     * 查询显卡硬件
     * 
     * @param id 显卡硬件主键
     * @return 显卡硬件
     */
    public HardwareGpu selectHardwareGpuById(Long id);

    /**
     * 查询显卡硬件列表
     * 
     * @param hardwareGpu 显卡硬件
     * @return 显卡硬件集合
     */
    public List<HardwareGpu> selectHardwareGpuList(HardwareGpu hardwareGpu);

    /**
     * 新增显卡硬件
     * 
     * @param hardwareGpu 显卡硬件
     * @return 结果
     */
    public int insertHardwareGpu(HardwareGpu hardwareGpu);

    /**
     * 修改显卡硬件
     * 
     * @param hardwareGpu 显卡硬件
     * @return 结果
     */
    public int updateHardwareGpu(HardwareGpu hardwareGpu);

    /**
     * 批量删除显卡硬件
     * 
     * @param ids 需要删除的显卡硬件主键集合
     * @return 结果
     */
    public int deleteHardwareGpuByIds(Long[] ids);

    /**
     * 删除显卡硬件信息
     * 
     * @param id 显卡硬件主键
     * @return 结果
     */
    public int deleteHardwareGpuById(Long id);
}
