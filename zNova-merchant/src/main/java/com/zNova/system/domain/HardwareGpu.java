package com.zNova.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zNova.common.annotation.Excel;
import com.zNova.common.core.domain.BaseEntity;

/**
 * 显卡硬件对象 hardware_gpu
 * 
 * @author wyz
 * @date 2025-11-13
 */
public class HardwareGpu extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private Long id;

    /** 品牌 */
    @Excel(name = "品牌")
    private String brand;

    /** 型号 */
    @Excel(name = "型号")
    private String model;

    /** 性能跑分 */
    @Excel(name = "性能跑分")
    private BigDecimal performanceScore;

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

    public void setPerformanceScore(BigDecimal performanceScore) 
    {
        this.performanceScore = performanceScore;
    }

    public BigDecimal getPerformanceScore() 
    {
        return performanceScore;
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
            .append("performanceScore", getPerformanceScore())
            .append("tdp", getTdp())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
