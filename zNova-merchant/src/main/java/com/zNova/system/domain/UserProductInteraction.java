package com.zNova.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zNova.common.annotation.Excel;
import com.zNova.common.core.domain.BaseEntity;

/**
 * 用户商品交互对象 user_product_interaction
 * 用于协同过滤推荐算法
 * 
 * @author zNova
 * @date 2025-01-21
 */
public class UserProductInteraction extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 商品ID */
    @Excel(name = "商品ID")
    private Long productId;

    /** 交互类型(1=下单/支付 2=浏览) */
    @Excel(name = "交互类型", readConverterExp = "1=下单/支付,2=浏览")
    private String interactionType;

    /** 权重 */
    @Excel(name = "权重")
    private Integer weight;

    /** 关联订单ID */
    @Excel(name = "关联订单ID")
    private Long orderId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }

    public void setInteractionType(String interactionType) 
    {
        this.interactionType = interactionType;
    }

    public String getInteractionType() 
    {
        return interactionType;
    }

    public void setWeight(Integer weight) 
    {
        this.weight = weight;
    }

    public Integer getWeight() 
    {
        return weight;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("productId", getProductId())
            .append("interactionType", getInteractionType())
            .append("weight", getWeight())
            .append("orderId", getOrderId())
            .append("createTime", getCreateTime())
            .toString();
    }
}
