package com.zNova.system.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zNova.common.annotation.Excel;
import com.zNova.common.core.domain.BaseEntity;

/**
 * 订单主对象 shop_order
 * 
 * @author wyz
 * @date 2025-11-24
 */
public class ShopOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long orderId;

    /** 订单编号(唯一) */
    @Excel(name = "订单编号(唯一)")
    private String orderNo;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 订单总金额 */
    @Excel(name = "订单总金额")
    private BigDecimal totalAmount;

    /** 状态(0待支付 1待发货 2租赁中/已发货 3已完成/已归还 4已取消) */
    @Excel(name = "状态(0待支付 1待发货 2租赁中/已发货 3已完成/已归还 4已取消)")
    private String status;

    /** 支付方式(1微信 2支付宝) */
    @Excel(name = "支付方式(1微信 2支付宝)")
    private String payType;

    /** 支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "支付时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date payTime;

    /** 收货人 */
    @Excel(name = "收货人")
    private String receiverName;

    /** 收货电话 */
    @Excel(name = "收货电话")
    private String receiverPhone;

    /** 收货完整地址 */
    @Excel(name = "收货完整地址")
    private String receiverAddress;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 押金金额(剩余) */
    @Excel(name = "押金金额")
    private BigDecimal depositAmount;

    /** 是否已发生逾期(0否1是) */
    @Excel(name = "是否逾期", readConverterExp = "0=否,1=是")
    private String isOverdue;

    /** 订单明细信息 */
    private List<ShopOrderItem> shopOrderItemList;

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setOrderNo(String orderNo) 
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo() 
    {
        return orderNo;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setPayType(String payType) 
    {
        this.payType = payType;
    }

    public String getPayType() 
    {
        return payType;
    }

    public void setPayTime(Date payTime) 
    {
        this.payTime = payTime;
    }

    public Date getPayTime() 
    {
        return payTime;
    }

    public void setReceiverName(String receiverName) 
    {
        this.receiverName = receiverName;
    }

    public String getReceiverName() 
    {
        return receiverName;
    }

    public void setReceiverPhone(String receiverPhone) 
    {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverPhone() 
    {
        return receiverPhone;
    }

    public void setReceiverAddress(String receiverAddress) 
    {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverAddress() 
    {
        return receiverAddress;
    }

    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }

    public void setDepositAmount(BigDecimal depositAmount) 
    {
        this.depositAmount = depositAmount;
    }

    public BigDecimal getDepositAmount() 
    {
        return depositAmount;
    }

    public void setIsOverdue(String isOverdue) 
    {
        this.isOverdue = isOverdue;
    }

    public String getIsOverdue() 
    {
        return isOverdue;
    }

    public List<ShopOrderItem> getShopOrderItemList()
    {
        return shopOrderItemList;
    }

    public void setShopOrderItemList(List<ShopOrderItem> shopOrderItemList)
    {
        this.shopOrderItemList = shopOrderItemList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("userId", getUserId())
            .append("totalAmount", getTotalAmount())
            .append("status", getStatus())
            .append("payType", getPayType())
            .append("payTime", getPayTime())
            .append("receiverName", getReceiverName())
            .append("receiverPhone", getReceiverPhone())
            .append("receiverAddress", getReceiverAddress())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("depositAmount", getDepositAmount())
            .append("isOverdue", getIsOverdue())
            .append("shopOrderItemList", getShopOrderItemList())
            .toString();
    }
}
