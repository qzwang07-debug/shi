package com.zNova.system.mapper;

import java.util.List;
import com.zNova.system.domain.HardwareMotherboard;

/**
 * 主板规格Mapper接口
 * 
 * @author ruoyi
 * @date 2025-11-13
 */
public interface HardwareMotherboardMapper 
{
    /**
     * 查询主板规格
     * 
     * @param id 主板规格主键
     * @return 主板规格
     */
    public HardwareMotherboard selectHardwareMotherboardById(Long id);

    /**
     * 查询主板规格列表
     * 
     * @param hardwareMotherboard 主板规格
     * @return 主板规格集合
     */
    public List<HardwareMotherboard> selectHardwareMotherboardList(HardwareMotherboard hardwareMotherboard);

    /**
     * 新增主板规格
     * 
     * @param hardwareMotherboard 主板规格
     * @return 结果
     */
    public int insertHardwareMotherboard(HardwareMotherboard hardwareMotherboard);

    /**
     * 修改主板规格
     * 
     * @param hardwareMotherboard 主板规格
     * @return 结果
     */
    public int updateHardwareMotherboard(HardwareMotherboard hardwareMotherboard);

    /**
     * 删除主板规格
     * 
     * @param id 主板规格主键
     * @return 结果
     */
    public int deleteHardwareMotherboardById(Long id);

    /**
     * 批量删除主板规格
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteHardwareMotherboardByIds(Long[] ids);
}
