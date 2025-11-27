package com.zNova.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zNova.common.annotation.Excel;
import com.zNova.common.core.domain.BaseEntity;

/**
 * 主板规格对象 hardware_motherboard
 * 
 * @author ruoyi
 * @date 2025-11-13
 */
public class HardwareMotherboard extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** CPU接口 */
    @Excel(name = "CPU接口")
    private String socketType;

    /** 内存接口 */
    @Excel(name = "内存接口")
    private String memoryType;

    /** 功耗(W) */
    @Excel(name = "功耗(W)")
    private Long maxCpuPower;

    /** 芯片组 */
    @Excel(name = "芯片组")
    private String chipset;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setSocketType(String socketType) 
    {
        this.socketType = socketType;
    }

    public String getSocketType() 
    {
        return socketType;
    }

    public void setMemoryType(String memoryType) 
    {
        this.memoryType = memoryType;
    }

    public String getMemoryType() 
    {
        return memoryType;
    }

    public void setMaxCpuPower(Long maxCpuPower) 
    {
        this.maxCpuPower = maxCpuPower;
    }

    public Long getMaxCpuPower() 
    {
        return maxCpuPower;
    }

    public void setChipset(String chipset) 
    {
        this.chipset = chipset;
    }

    public String getChipset() 
    {
        return chipset;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("socketType", getSocketType())
            .append("memoryType", getMemoryType())
            .append("maxCpuPower", getMaxCpuPower())
            .append("chipset", getChipset())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
