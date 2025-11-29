package com.zNova.system.mapper;

import java.util.List;
import com.zNova.system.domain.HardwarePowerSupply;

/**
 * 电源规格Mapper接口
 * 
 * @author ruoyi
 * @date 2025-11-29
 */
public interface HardwarePowerSupplyMapper 
{
    /**
     * 查询电源规格
     * 
     * @param id 电源规格主键
     * @return 电源规格
     */
    public HardwarePowerSupply selectHardwarePowerSupplyById(Long id);

    /**
     * 查询电源规格列表
     * 
     * @param hardwarePowerSupply 电源规格
     * @return 电源规格集合
     */
    public List<HardwarePowerSupply> selectHardwarePowerSupplyList(HardwarePowerSupply hardwarePowerSupply);

    /**
     * 新增电源规格
     * 
     * @param hardwarePowerSupply 电源规格
     * @return 结果
     */
    public int insertHardwarePowerSupply(HardwarePowerSupply hardwarePowerSupply);

    /**
     * 修改电源规格
     * 
     * @param hardwarePowerSupply 电源规格
     * @return 结果
     */
    public int updateHardwarePowerSupply(HardwarePowerSupply hardwarePowerSupply);

    /**
     * 删除电源规格
     * 
     * @param id 电源规格主键
     * @return 结果
     */
    public int deleteHardwarePowerSupplyById(Long id);

    /**
     * 批量删除电源规格
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteHardwarePowerSupplyByIds(Long[] ids);
}
