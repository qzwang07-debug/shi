package com.zNova.system.service;

import java.util.List;
import com.zNova.system.domain.HardwareCpu;

/**
 * CPU硬件Service接口
 * 
 * @author wyz
 * @date 2025-11-13
 */
public interface IHardwareCpuService 
{
    /**
     * 查询CPU硬件
     * 
     * @param id CPU硬件主键
     * @return CPU硬件
     */
    public HardwareCpu selectHardwareCpuById(Long id);

    /**
     * 查询CPU硬件列表
     * 
     * @param hardwareCpu CPU硬件
     * @return CPU硬件集合
     */
    public List<HardwareCpu> selectHardwareCpuList(HardwareCpu hardwareCpu);

    /**
     * 新增CPU硬件
     * 
     * @param hardwareCpu CPU硬件
     * @return 结果
     */
    public int insertHardwareCpu(HardwareCpu hardwareCpu);

    /**
     * 修改CPU硬件
     * 
     * @param hardwareCpu CPU硬件
     * @return 结果
     */
    public int updateHardwareCpu(HardwareCpu hardwareCpu);

    /**
     * 批量删除CPU硬件
     * 
     * @param ids 需要删除的CPU硬件主键集合
     * @return 结果
     */
    public int deleteHardwareCpuByIds(Long[] ids);

    /**
     * 删除CPU硬件信息
     * 
     * @param id CPU硬件主键
     * @return 结果
     */
    public int deleteHardwareCpuById(Long id);
}
