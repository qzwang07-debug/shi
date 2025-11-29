package com.zNova.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zNova.common.annotation.Excel;
import com.zNova.common.core.domain.BaseEntity;

/**
 * 电源规格对象 hardware_power_supply
 * 
 * @author ruoyi
 * @date 2025-11-29
 */
public class HardwarePowerSupply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 总瓦数(W) */
    @Excel(name = "总瓦数(W)")
    private Long wattage;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setWattage(Long wattage) 
    {
        this.wattage = wattage;
    }

    public Long getWattage() 
    {
        return wattage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("wattage", getWattage())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
