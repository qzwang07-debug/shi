package com.zNova.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zNova.common.annotation.Excel;
import com.zNova.common.core.domain.BaseEntity;

/**
 * 用户装机配置对象 sys_user_build
 * 
 * @author wyz
 * @date 2025-11-29
 */
public class UserBuild extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 配置单ID */
    private Long buildId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 配置单标题(如:我的吃鸡神机) */
    @Excel(name = "配置单标题(如:我的吃鸡神机)")
    private String title;

    /** CPU型号 */
    @Excel(name = "CPU型号")
    private String cpuModel;

    /** CPU金额 */
    @Excel(name = "CPU金额")
    private BigDecimal cpuPrice;

    /** CPU的TDP值（单位：W） */
    @Excel(name = "CPU的TDP值", readConverterExp = "单=位：W")
    private Long cpuTdp;

    /** 主板品牌 */
    @Excel(name = "主板品牌")
    private String moboBrand;

    /** 主板型号 */
    @Excel(name = "主板型号")
    private String moboModel;

    /** 主板系列 */
    @Excel(name = "主板系列")
    private String moboSeries;

    /** 主板金额 */
    @Excel(name = "主板金额")
    private BigDecimal moboPrice;

    /** 内存品牌 */
    @Excel(name = "内存品牌")
    private String ramBrand;

    /** 内存接口(如DDR4/DDR5) */
    @Excel(name = "内存接口(如DDR4/DDR5)")
    private String ramInterface;

    /** 内存频率(如3200MHz) */
    @Excel(name = "内存频率(如3200MHz)")
    private String ramFrequency;

    /** 内存容量(如16GB) */
    @Excel(name = "内存容量(如16GB)")
    private String ramCapacity;

    /** 内存金额 */
    @Excel(name = "内存金额")
    private BigDecimal ramPrice;

    /** 固态全称 */
    @Excel(name = "固态全称")
    private String ssdFullName;

    /** 固态金额 */
    @Excel(name = "固态金额")
    private BigDecimal ssdPrice;

    /** 显卡品牌 */
    @Excel(name = "显卡品牌")
    private String gpuBrand;

    /** 显卡型号 */
    @Excel(name = "显卡型号")
    private String gpuModel;

    /** 显卡系列 */
    @Excel(name = "显卡系列")
    private String gpuSeries;

    /** 显卡金额 */
    @Excel(name = "显卡金额")
    private BigDecimal gpuPrice;

    /** 散热器全称 */
    @Excel(name = "散热器全称")
    private String coolerFullName;

    /** 散热器金额 */
    @Excel(name = "散热器金额")
    private BigDecimal coolerPrice;

    /** 电源品牌 */
    @Excel(name = "电源品牌")
    private String psuBrand;

    /** 电源瓦数 */
    @Excel(name = "电源瓦数")
    private String psuWattage;

    /** 电源系列 */
    @Excel(name = "电源系列")
    private String psuSeries;

    /** 电源金额 */
    @Excel(name = "电源金额")
    private BigDecimal psuPrice;

    /** 机箱全称 */
    @Excel(name = "机箱全称")
    private String caseFullName;

    /** 机箱金额 */
    @Excel(name = "机箱金额")
    private BigDecimal casePrice;

    /** 风扇全称 */
    @Excel(name = "风扇全称")
    private String fanFullName;

    /** 风扇金额 */
    @Excel(name = "风扇金额")
    private BigDecimal fanPrice;

    /** 总金额 */
    @Excel(name = "总金额")
    private BigDecimal totalPrice;

    /** 配置单总评分 */
    @Excel(name = "配置单总评分")
    private Long performanceScore;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setBuildId(Long buildId) 
    {
        this.buildId = buildId;
    }

    public Long getBuildId() 
    {
        return buildId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setCpuModel(String cpuModel) 
    {
        this.cpuModel = cpuModel;
    }

    public String getCpuModel() 
    {
        return cpuModel;
    }

    public void setCpuPrice(BigDecimal cpuPrice) 
    {
        this.cpuPrice = cpuPrice;
    }

    public BigDecimal getCpuPrice() 
    {
        return cpuPrice;
    }

    public void setCpuTdp(Long cpuTdp) 
    {
        this.cpuTdp = cpuTdp;
    }

    public Long getCpuTdp() 
    {
        return cpuTdp;
    }

    public void setMoboBrand(String moboBrand) 
    {
        this.moboBrand = moboBrand;
    }

    public String getMoboBrand() 
    {
        return moboBrand;
    }

    public void setMoboModel(String moboModel) 
    {
        this.moboModel = moboModel;
    }

    public String getMoboModel() 
    {
        return moboModel;
    }

    public void setMoboSeries(String moboSeries) 
    {
        this.moboSeries = moboSeries;
    }

    public String getMoboSeries() 
    {
        return moboSeries;
    }

    public void setMoboPrice(BigDecimal moboPrice) 
    {
        this.moboPrice = moboPrice;
    }

    public BigDecimal getMoboPrice() 
    {
        return moboPrice;
    }

    public void setRamBrand(String ramBrand) 
    {
        this.ramBrand = ramBrand;
    }

    public String getRamBrand() 
    {
        return ramBrand;
    }

    public void setRamInterface(String ramInterface) 
    {
        this.ramInterface = ramInterface;
    }

    public String getRamInterface() 
    {
        return ramInterface;
    }

    public void setRamFrequency(String ramFrequency) 
    {
        this.ramFrequency = ramFrequency;
    }

    public String getRamFrequency() 
    {
        return ramFrequency;
    }

    public void setRamCapacity(String ramCapacity) 
    {
        this.ramCapacity = ramCapacity;
    }

    public String getRamCapacity() 
    {
        return ramCapacity;
    }

    public void setRamPrice(BigDecimal ramPrice) 
    {
        this.ramPrice = ramPrice;
    }

    public BigDecimal getRamPrice() 
    {
        return ramPrice;
    }

    public void setSsdFullName(String ssdFullName) 
    {
        this.ssdFullName = ssdFullName;
    }

    public String getSsdFullName() 
    {
        return ssdFullName;
    }

    public void setSsdPrice(BigDecimal ssdPrice) 
    {
        this.ssdPrice = ssdPrice;
    }

    public BigDecimal getSsdPrice() 
    {
        return ssdPrice;
    }

    public void setGpuBrand(String gpuBrand) 
    {
        this.gpuBrand = gpuBrand;
    }

    public String getGpuBrand() 
    {
        return gpuBrand;
    }

    public void setGpuModel(String gpuModel) 
    {
        this.gpuModel = gpuModel;
    }

    public String getGpuModel() 
    {
        return gpuModel;
    }

    public void setGpuSeries(String gpuSeries) 
    {
        this.gpuSeries = gpuSeries;
    }

    public String getGpuSeries() 
    {
        return gpuSeries;
    }

    public void setGpuPrice(BigDecimal gpuPrice) 
    {
        this.gpuPrice = gpuPrice;
    }

    public BigDecimal getGpuPrice() 
    {
        return gpuPrice;
    }

    public void setCoolerFullName(String coolerFullName) 
    {
        this.coolerFullName = coolerFullName;
    }

    public String getCoolerFullName() 
    {
        return coolerFullName;
    }

    public void setCoolerPrice(BigDecimal coolerPrice) 
    {
        this.coolerPrice = coolerPrice;
    }

    public BigDecimal getCoolerPrice() 
    {
        return coolerPrice;
    }

    public void setPsuBrand(String psuBrand) 
    {
        this.psuBrand = psuBrand;
    }

    public String getPsuBrand() 
    {
        return psuBrand;
    }

    public void setPsuWattage(String psuWattage) 
    {
        this.psuWattage = psuWattage;
    }

    public String getPsuWattage() 
    {
        return psuWattage;
    }

    public void setPsuSeries(String psuSeries) 
    {
        this.psuSeries = psuSeries;
    }

    public String getPsuSeries() 
    {
        return psuSeries;
    }

    public void setPsuPrice(BigDecimal psuPrice) 
    {
        this.psuPrice = psuPrice;
    }

    public BigDecimal getPsuPrice() 
    {
        return psuPrice;
    }

    public void setCaseFullName(String caseFullName) 
    {
        this.caseFullName = caseFullName;
    }

    public String getCaseFullName() 
    {
        return caseFullName;
    }

    public void setCasePrice(BigDecimal casePrice) 
    {
        this.casePrice = casePrice;
    }

    public BigDecimal getCasePrice() 
    {
        return casePrice;
    }

    public void setFanFullName(String fanFullName) 
    {
        this.fanFullName = fanFullName;
    }

    public String getFanFullName() 
    {
        return fanFullName;
    }

    public void setFanPrice(BigDecimal fanPrice) 
    {
        this.fanPrice = fanPrice;
    }

    public BigDecimal getFanPrice() 
    {
        return fanPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) 
    {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice() 
    {
        return totalPrice;
    }

    public void setPerformanceScore(Long performanceScore) 
    {
        this.performanceScore = performanceScore;
    }

    public Long getPerformanceScore() 
    {
        return performanceScore;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("buildId", getBuildId())
            .append("userId", getUserId())
            .append("title", getTitle())
            .append("cpuModel", getCpuModel())
            .append("cpuPrice", getCpuPrice())
            .append("cpuTdp", getCpuTdp())
            .append("moboBrand", getMoboBrand())
            .append("moboModel", getMoboModel())
            .append("moboSeries", getMoboSeries())
            .append("moboPrice", getMoboPrice())
            .append("ramBrand", getRamBrand())
            .append("ramInterface", getRamInterface())
            .append("ramFrequency", getRamFrequency())
            .append("ramCapacity", getRamCapacity())
            .append("ramPrice", getRamPrice())
            .append("ssdFullName", getSsdFullName())
            .append("ssdPrice", getSsdPrice())
            .append("gpuBrand", getGpuBrand())
            .append("gpuModel", getGpuModel())
            .append("gpuSeries", getGpuSeries())
            .append("gpuPrice", getGpuPrice())
            .append("coolerFullName", getCoolerFullName())
            .append("coolerPrice", getCoolerPrice())
            .append("psuBrand", getPsuBrand())
            .append("psuWattage", getPsuWattage())
            .append("psuSeries", getPsuSeries())
            .append("psuPrice", getPsuPrice())
            .append("caseFullName", getCaseFullName())
            .append("casePrice", getCasePrice())
            .append("fanFullName", getFanFullName())
            .append("fanPrice", getFanPrice())
            .append("totalPrice", getTotalPrice())
            .append("performanceScore", getPerformanceScore())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
