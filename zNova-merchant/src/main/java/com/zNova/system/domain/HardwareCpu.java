package com.zNova.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zNova.common.annotation.Excel;
import com.zNova.common.core.domain.BaseEntity;

/**
 * CPU硬件对象 hardware_cpu
 *
 * @author wyz
 * @date 2025-11-13
 */
public class HardwareCpu extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 品牌 */
    @Excel(name = "品牌")
    private String brand;

    /** 型号 */
    @Excel(name = "型号")
    private String model;

    /** 接口 */
    @Excel(name = "接口")
    private String socketType;

    /** 单核性能分 */
    @Excel(name = "单核性能分")
    private BigDecimal singleCoreScore;

    /** 多核性能分 */
    @Excel(name = "多核性能分")
    private BigDecimal multiCoreScore;

    /** 功耗(W) */
    @Excel(name = "功耗(W)")
    private Long tdp;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public String getBrand()
    {
        return brand;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getModel()
    {
        return model;
    }

    public void setSocketType(String socketType)
    {
        this.socketType = socketType;
    }

    public String getSocketType()
    {
        return socketType;
    }

    public void setSingleCoreScore(BigDecimal singleCoreScore)
    {
        this.singleCoreScore = singleCoreScore;
    }

    public BigDecimal getSingleCoreScore()
    {
        return singleCoreScore;
    }

    public void setMultiCoreScore(BigDecimal multiCoreScore)
    {
        this.multiCoreScore = multiCoreScore;
    }

    public BigDecimal getMultiCoreScore()
    {
        return multiCoreScore;
    }

    public void setTdp(Long tdp)
    {
        this.tdp = tdp;
    }

    public Long getTdp()
    {
        return tdp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("brand", getBrand())
            .append("model", getModel())
            .append("socketType", getSocketType())
            .append("singleCoreScore", getSingleCoreScore())
            .append("multiCoreScore", getMultiCoreScore())
            .append("tdp", getTdp())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
