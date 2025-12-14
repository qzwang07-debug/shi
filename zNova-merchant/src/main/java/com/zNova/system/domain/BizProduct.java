package com.zNova.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zNova.common.annotation.Excel;
import com.zNova.common.core.domain.BaseEntity;

/**
 * 商品对象 biz_product
 *
 * @author wyz
 * @date 2025-11-24
 */
public class BizProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private Long id;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String productName;

    /** 商品类型（1-租赁 2-销售 3-租售一体） */
    @Excel(name = "商品类型", readConverterExp = "1=-租赁,2=-销售,3=-租售一体")
    private String productType;

    /** 日租金 */
    @Excel(name = "日租金")
    private BigDecimal rentPrice;

    /** 销售价格 */
    @Excel(name = "销售价格")
    private BigDecimal salePrice;

    /** 库存数量 */
    @Excel(name = "库存数量")
    private Long stockQuantity;

    /** 可租赁数量 */
    @Excel(name = "可租赁数量")
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
    @Excel(name = "固态硬盘")
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
    @Excel(name = "图片URL")
    private String imageUrl;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 显卡芯片型号（关联 hardware_gpu.model，用于性能分数查询） */
    @Excel(name = "显卡芯片型号", readConverterExp = "关=联,h=ardware_gpu.model，用于性能分数查询")
    private String gpuModel;

    /** 内存类型（如 DDR4、DDR5，关联 hardware_memory.type） */
    @Excel(name = "内存类型", readConverterExp = "如=,D=DR4、DDR5，关联,h=ardware_memory.type")
    private String memoryType;

    /** 内存频率 */
    @Excel(name = "内存频率")
    private Long memoryFrequency;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 性能分数 */
    @Excel(name = "性能分数")
    private Integer performanceScore;

    /** CPU品牌（非数据库字段，联表查询） */
    private String cpuBrand;

    /** CPU单核分数（非数据库字段，联表查询） */
    private Integer cpuSingleCoreScore;

    /** CPU多核分数（非数据库字段，联表查询） */
    private Integer cpuMultiCoreScore;

    /** GPU性能分数（非数据库字段，联表查询） */
    private Integer gpuScore;

    /** 是否可租赁（非数据库字段，用于查询条件） */
    private Boolean hasRent;

    /** 是否可销售（非数据库字段，用于查询条件） */
    private Boolean hasSale;

    // --- 新增代码开始 ---
    /** 部门名称（商家名称），非数据库字段 */
    @Excel(name = "商家名称")
    private String deptName;

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptName() {
        return deptName;
    }
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

    public void setGpuModel(String gpuModel)
    {
        this.gpuModel = gpuModel;
    }

    public String getGpuModel()
    {
        return gpuModel;
    }

    public void setMemoryType(String memoryType)
    {
        this.memoryType = memoryType;
    }

    public String getMemoryType()
    {
        return memoryType;
    }

    public void setMemoryFrequency(Long memoryFrequency)
    {
        this.memoryFrequency = memoryFrequency;
    }

    public Long getMemoryFrequency()
    {
        return memoryFrequency;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setPerformanceScore(Integer performanceScore)
    {
        this.performanceScore = performanceScore;
    }

    public Integer getPerformanceScore()
    {
        return performanceScore;
    }

    public void setCpuBrand(String cpuBrand)
    {
        this.cpuBrand = cpuBrand;
    }

    public String getCpuBrand()
    {
        return cpuBrand;
    }

    public void setCpuSingleCoreScore(Integer cpuSingleCoreScore)
    {
        this.cpuSingleCoreScore = cpuSingleCoreScore;
    }

    public Integer getCpuSingleCoreScore()
    {
        return cpuSingleCoreScore;
    }

    public void setCpuMultiCoreScore(Integer cpuMultiCoreScore)
    {
        this.cpuMultiCoreScore = cpuMultiCoreScore;
    }

    public Integer getCpuMultiCoreScore()
    {
        return cpuMultiCoreScore;
    }

    public void setGpuScore(Integer gpuScore)
    {
        this.gpuScore = gpuScore;
    }

    public Integer getGpuScore()
    {
        return gpuScore;
    }

    public void setHasRent(Boolean hasRent)
    {
        this.hasRent = hasRent;
    }

    public Boolean getHasRent()
    {
        return hasRent;
    }

    public void setHasSale(Boolean hasSale)
    {
        this.hasSale = hasSale;
    }

    public Boolean getHasSale()
    {
        return hasSale;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("productName", getProductName())
                .append("productType", getProductType())
                .append("rentPrice", getRentPrice())
                .append("salePrice", getSalePrice())
                .append("stockQuantity", getStockQuantity())
                .append("availableRent", getAvailableRent())
                .append("cpu", getCpu())
                .append("motherboard", getMotherboard())
                .append("memory", getMemory())
                .append("ssd", getSsd())
                .append("graphicsCard", getGraphicsCard())
                .append("cooler", getCooler())
                .append("powerSupply", getPowerSupply())
                .append("chassis", getChassis())
                .append("imageUrl", getImageUrl())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("gpuModel", getGpuModel())
                .append("memoryType", getMemoryType())
                .append("memoryFrequency", getMemoryFrequency())
                .append("deptId", getDeptId())
                .append("performanceScore", getPerformanceScore())
                .append("cpuBrand", getCpuBrand())
                .append("cpuSingleCoreScore", getCpuSingleCoreScore())
                .append("cpuMultiCoreScore", getCpuMultiCoreScore())
                .append("gpuScore", getGpuScore())
                .append("hasRent", getHasRent())
                .append("hasSale", getHasSale())
                .toString();
    }
}
