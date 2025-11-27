package com.zNova.system.service.impl;

import java.util.List;
import com.zNova.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zNova.system.mapper.HardwareMotherboardMapper;
import com.zNova.system.domain.HardwareMotherboard;
import com.zNova.system.service.IHardwareMotherboardService;

/**
 * 主板规格Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-13
 */
@Service
public class HardwareMotherboardServiceImpl implements IHardwareMotherboardService 
{
    @Autowired
    private HardwareMotherboardMapper hardwareMotherboardMapper;

    /**
     * 查询主板规格
     * 
     * @param id 主板规格主键
     * @return 主板规格
     */
    @Override
    public HardwareMotherboard selectHardwareMotherboardById(Long id)
    {
        return hardwareMotherboardMapper.selectHardwareMotherboardById(id);
    }

    /**
     * 查询主板规格列表
     * 
     * @param hardwareMotherboard 主板规格
     * @return 主板规格
     */
    @Override
    public List<HardwareMotherboard> selectHardwareMotherboardList(HardwareMotherboard hardwareMotherboard)
    {
        return hardwareMotherboardMapper.selectHardwareMotherboardList(hardwareMotherboard);
    }

    /**
     * 新增主板规格
     * 
     * @param hardwareMotherboard 主板规格
     * @return 结果
     */
    @Override
    public int insertHardwareMotherboard(HardwareMotherboard hardwareMotherboard)
    {
        hardwareMotherboard.setCreateTime(DateUtils.getNowDate());
        return hardwareMotherboardMapper.insertHardwareMotherboard(hardwareMotherboard);
    }

    /**
     * 修改主板规格
     * 
     * @param hardwareMotherboard 主板规格
     * @return 结果
     */
    @Override
    public int updateHardwareMotherboard(HardwareMotherboard hardwareMotherboard)
    {
        hardwareMotherboard.setUpdateTime(DateUtils.getNowDate());
        return hardwareMotherboardMapper.updateHardwareMotherboard(hardwareMotherboard);
    }

    /**
     * 批量删除主板规格
     * 
     * @param ids 需要删除的主板规格主键
     * @return 结果
     */
    @Override
    public int deleteHardwareMotherboardByIds(Long[] ids)
    {
        return hardwareMotherboardMapper.deleteHardwareMotherboardByIds(ids);
    }

    /**
     * 删除主板规格信息
     * 
     * @param id 主板规格主键
     * @return 结果
     */
    @Override
    public int deleteHardwareMotherboardById(Long id)
    {
        return hardwareMotherboardMapper.deleteHardwareMotherboardById(id);
    }
}
