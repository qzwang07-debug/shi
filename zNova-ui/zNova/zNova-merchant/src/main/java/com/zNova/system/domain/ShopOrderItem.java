package com.zNova.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zNova.common.annotation.Excel;
import com.zNova.common.core.domain.BaseEntity;

/**
 * 订单明细对象 shop_order_item
 * 
 * @author wyz
 * @date 2025-11-24
 */
public class ShopOrderItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 关联主订单 */
    @Excel(name = "关联主订单")
    private Long orderId;

    /** 商品ID */
    @Excel(name = "商品ID")
    private Long productId;

    /** 商品名称快照 */
    @Excel(name = "商品名称快照")
    private String productName;

    /** 图片快照 */
    @Excel(name = "图片快照")
    private String productImg;

    /** 类型(1租赁 2购买) */
    @Excel(name = "类型(1租赁 2购买)")
    private String businessType;

    /** 成交单价(日租金或售价) */
    @Excel(name = "成交单价(日租金或售价)")
    private BigDecimal price;

    /** 数量 */
    @Excel(name = "数量")
    private Long quantity;

    /** 起租时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "起租时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date rentStartTime;

    /** 归还时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "归还时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date rentEndTime;

    /** 租期(天) */
    @Excel(name = "租期(天)")
    private Long rentDays;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }
    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }
    public void setProductName(String productName) 
    {
        this.productName = productName;
    }

    public String getProductName() 
    {
        return productName;
    }
    public void setProductImg(String productImg) 
    {
        this.productImg = productImg;
    }

    public String getProductImg() 
    {
        return productImg;
    }
    public void setBusinessType(String businessType) 
    {
        this.businessType = businessType;
    }

    public String getBusinessType() 
    {
        return businessType;
    }
    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }
    public void setQuantity(Long quantity) 
    {
        this.quantity = quantity;
    }

    public Long getQuantity() 
    {
        return quantity;
    }
    public void setRentStartTime(Date rentStartTime) 
    {
        this.rentStartTime = rentStartTime;
    }

    public Date getRentStartTime() 
    {
        return rentStartTime;
    }
    public void setRentEndTime(Date rentEndTime) 
    {
        this.rentEndTime = rentEndTime;
    }

    public Date getRentEndTime() 
    {
        return rentEndTime;
    }
    public void setRentDays(Long rentDays) 
    {
        this.rentDays = rentDays;
    }

    public Long getRentDays() 
    {
        return rentDays;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("orderId", getOrderId())
            .append("productId", getProductId())
            .append("productName", getProductName())
            .append("productImg", getProductImg())
            .append("businessType", getBusinessType())
            .append("price", getPrice())
            .append("quantity", getQuantity())
            .append("rentStartTime", getRentStartTime())
            .append("rentEndTime", getRentEndTime())
            .append("rentDays", getRentDays())
            .append("deptId", getDeptId())
            .toString();
    }
}
