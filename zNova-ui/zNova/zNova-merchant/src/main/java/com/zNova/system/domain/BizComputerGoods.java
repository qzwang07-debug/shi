package com.zNova.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zNova.common.annotation.Excel;
import com.zNova.common.core.domain.BaseEntity;

/**
 * 电脑商品对象 biz_product
 *
 * @author wyz
 * @date 2025-11-09
 */
public class BizComputerGoods extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 商品ID */
    private Long id;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String productName;

    /** 商品类型（1-租赁 2-销售 3-租售一体） */
    @Excel(name = "商品类型", readConverterExp = "1=租赁,2=销售,3=租售一体")
    private String productType;

    /** 日租金 */
    @Excel(name = "日租金")
    private BigDecimal rentPrice;

    /** 销售价格 */
    @Excel(name = "销售价格")
    private BigDecimal salePrice;

    /** 库存数量 */
    private Long stockQuantity;

    /** 可租赁数量 */
    private Long availableRent;

    /** CPU */
    @Excel(name = "CPU")
    private String cpu;

    /** 主板 */
    @Excel(name = "主板")
    private String motherboard;

    /** 内存 */
    @Excel(name = "内存")
    private String memory;

    /** 固态硬盘 */
    @Excel(name = "固态")
    private String ssd;

    /** 显卡 */
    @Excel(name = "显卡")
    private String graphicsCard;

    /** 散热器 */
    @Excel(name = "散热器")
    private String cooler;

    /** 电源 */
    @Excel(name = "电源")
    private String powerSupply;

    /** 机箱 */
    @Excel(name = "机箱")
    private String chassis;

    /** 图片URL */
    @Excel(name = "图片")
    private String imageUrl;

    /** 状态（0正常 1停用） */
    private String status;

    /** CPU品牌（AMD/Intel） */
    private String cpuBrand;

    /** CPU多核分数 */
    private Integer cpuMultiCoreScore;

    /** CPU单核分数 */
    private Integer cpuSingleCoreScore;

    /** 内存类型（DDR4/DDR5） */
    private String memoryType;

    /** 内存频率（MHz） */
    private Integer memoryFrequency;

    /** 显卡跑分 */
    private Integer gpuScore;

    /** 显卡芯片型号（关联hardware_gpu.model） */
    private String gpuModel;  // 新增字段，对应数据库gpu_model

    // 新增gpuModel的getter/setter
    public String getGpuModel() {
        return gpuModel;
    }
    public void setGpuModel(String gpuModel) {
        this.gpuModel = gpuModel;
    }

    // getter和setter方法
    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductType(String productType)
    {
        this.productType = productType;
    }

    public String getProductType()
    {
        return productType;
    }

    public void setRentPrice(BigDecimal rentPrice)
    {
        this.rentPrice = rentPrice;
    }

    public BigDecimal getRentPrice()
    {
        return rentPrice;
    }

    public void setSalePrice(BigDecimal salePrice)
    {
        this.salePrice = salePrice;
    }

    public BigDecimal getSalePrice()
    {
        return salePrice;
    }

    public void setStockQuantity(Long stockQuantity)
    {
        this.stockQuantity = stockQuantity;
    }

    public Long getStockQuantity()
    {
        return stockQuantity;
    }

    public void setAvailableRent(Long availableRent)
    {
        this.availableRent = availableRent;
    }

    public Long getAvailableRent()
    {
        return availableRent;
    }

    public void setCpu(String cpu)
    {
        this.cpu = cpu;
    }

    public String getCpu()
    {
        return cpu;
    }

    public void setMotherboard(String motherboard)
    {
        this.motherboard = motherboard;
    }

    public String getMotherboard()
    {
        return motherboard;
    }

    public void setMemory(String memory)
    {
        this.memory = memory;
    }

    public String getMemory()
    {
        return memory;
    }

    public void setSsd(String ssd)
    {
        this.ssd = ssd;
    }

    public String getSsd()
    {
        return ssd;
    }

    public void setGraphicsCard(String graphicsCard)
    {
        this.graphicsCard = graphicsCard;
    }

    public String getGraphicsCard()
    {
        return graphicsCard;
    }

    public void setCooler(String cooler)
    {
        this.cooler = cooler;
    }

    public String getCooler()
    {
        return cooler;
    }

    public void setPowerSupply(String powerSupply)
    {
        this.powerSupply = powerSupply;
    }

    public String getPowerSupply()
    {
        return powerSupply;
    }

    public void setChassis(String chassis)
    {
        this.chassis = chassis;
    }

    public String getChassis()
    {
        return chassis;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setCpuBrand(String cpuBrand)
    {
        this.cpuBrand = cpuBrand;
    }

    public String getCpuBrand()
    {
        return cpuBrand;
    }

    public void setCpuMultiCoreScore(Integer cpuMultiCoreScore)
    {
        this.cpuMultiCoreScore = cpuMultiCoreScore;
    }

    public Integer getCpuMultiCoreScore()
    {
        return cpuMultiCoreScore;
    }

    public void setCpuSingleCoreScore(Integer cpuSingleCoreScore)
    {
        this.cpuSingleCoreScore = cpuSingleCoreScore;
    }

    public Integer getCpuSingleCoreScore()
    {
        return cpuSingleCoreScore;
    }

    public void setMemoryType(String memoryType)
    {
        this.memoryType = memoryType;
    }

    public String getMemoryType()
    {
        return memoryType;
    }

    public void setMemoryFrequency(Integer memoryFrequency)
    {
        this.memoryFrequency = memoryFrequency;
    }

    public Integer getMemoryFrequency()
    {
        return memoryFrequency;
    }

    public void setGpuScore(Integer gpuScore)
    {
        this.gpuScore = gpuScore;
    }

    public Integer getGpuScore()
    {
        return gpuScore;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("productName", getProductName())
                .append("productType", getProductType())
                .append("rentPrice", getRentPrice())
                .append("salePrice", getSalePrice())
                .append("cpu", getCpu())
                .append("motherboard", getMotherboard())
                .append("memory", getMemory())
                .append("ssd", getSsd())
                .append("graphicsCard", getGraphicsCard())
                .append("cooler", getCooler())
                .append("powerSupply", getPowerSupply())
                .append("chassis", getChassis())
                .append("imageUrl", getImageUrl())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("cpuBrand", getCpuBrand())
                .append("cpuMultiCoreScore", getCpuMultiCoreScore())
                .append("cpuSingleCoreScore", getCpuSingleCoreScore())
                .append("memoryType", getMemoryType())
                .append("memoryFrequency", getMemoryFrequency())
                .append("gpuScore", getGpuScore())
                .append("gpuModel", getGpuModel())

                .toString();
    }
}