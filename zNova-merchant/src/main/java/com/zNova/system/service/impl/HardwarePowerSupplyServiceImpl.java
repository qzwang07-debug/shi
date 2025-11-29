package com.zNova.system.service.impl;

import java.util.List;
import com.zNova.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zNova.system.mapper.HardwarePowerSupplyMapper;
import com.zNova.system.domain.HardwarePowerSupply;
import com.zNova.system.service.IHardwarePowerSupplyService;

/**
 * 电源规格Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-29
 */
@Service
public class HardwarePowerSupplyServiceImpl implements IHardwarePowerSupplyService 
{
    @Autowired
    private HardwarePowerSupplyMapper hardwarePowerSupplyMapper;

    /**
     * 查询电源规格
     * 
     * @param id 电源规格主键
     * @return 电源规格
     */
    @Override
    public HardwarePowerSupply selectHardwarePowerSupplyById(Long id)
    {
        return hardwarePowerSupplyMapper.selectHardwarePowerSupplyById(id);
    }

    /**
     * 查询电源规格列表
     * 
     * @param hardwarePowerSupply 电源规格
     * @return 电源规格
     */
    @Override
    public List<HardwarePowerSupply> selectHardwarePowerSupplyList(HardwarePowerSupply hardwarePowerSupply)
    {
        return hardwarePowerSupplyMapper.selectHardwarePowerSupplyList(hardwarePowerSupply);
    }

    /**
     * 新增电源规格
     * 
     * @param hardwarePowerSupply 电源规格
     * @return 结果
     */
    @Override
    public int insertHardwarePowerSupply(HardwarePowerSupply hardwarePowerSupply)
    {
        hardwarePowerSupply.setCreateTime(DateUtils.getNowDate());
        return hardwarePowerSupplyMapper.insertHardwarePowerSupply(hardwarePowerSupply);
    }

    /**
     * 修改电源规格
     * 
     * @param hardwarePowerSupply 电源规格
     * @return 结果
     */
    @Override
    public int updateHardwarePowerSupply(HardwarePowerSupply hardwarePowerSupply)
    {
        hardwarePowerSupply.setUpdateTime(DateUtils.getNowDate());
        return hardwarePowerSupplyMapper.updateHardwarePowerSupply(hardwarePowerSupply);
    }

    /**
     * 批量删除电源规格
     * 
     * @param ids 需要删除的电源规格主键
     * @return 结果
     */
    @Override
    public int deleteHardwarePowerSupplyByIds(Long[] ids)
    {
        return hardwarePowerSupplyMapper.deleteHardwarePowerSupplyByIds(ids);
    }

    /**
     * 删除电源规格信息
     * 
     * @param id 电源规格主键
     * @return 结果
     */
    @Override
    public int deleteHardwarePowerSupplyById(Long id)
    {
        return hardwarePowerSupplyMapper.deleteHardwarePowerSupplyById(id);
    }
}
