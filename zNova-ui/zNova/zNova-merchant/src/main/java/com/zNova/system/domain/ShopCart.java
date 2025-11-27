package com.zNova.system.domain;

import java.math.BigDecimal;

import com.zNova.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.baomidou.mybatisplus.annotation.TableField; // 如果用MP
// 如果是纯若依MyBatis，使用: import com.zNova.common.annotation.Excel;

/**
 * 购物车对象 shop_cart
 *
 * @author zNova
 * @date 2024-05-20
 */
public class ShopCart extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 购物车ID */
    private Long cartId;

    /** 用户ID */
    private Long userId;

    /** 商品ID */
    private Long productId;

    /** 购买数量 */
    private Integer quantity;

    /** 业务类型（1租赁 2购买） */
    private String businessType;

    /** 是否选中（0否 1是） */
    private String isChecked;

    /* ================= 扩展字段 (不存数据库) ================= */

    /** 商品名称 */
    @TableField(exist = false) // MP注解，如果是原生MyBatis可以忽略，只要表中没这字段即可
    private String productName;

    /** 商品图片 */
    @TableField(exist = false)
    private String productImg;

    /** 动态价格 (根据 businessType 显示日租金或售价) */
    @TableField(exist = false)
    private BigDecimal price;

    // Getters and Setters
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getIsChecked() { return isChecked; }
    public void setIsChecked(String isChecked) { this.isChecked = isChecked; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImg() { return productImg; }
    public void setProductImg(String productImg) { this.productImg = productImg; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("cartId", getCartId())
                .append("userId", getUserId())
                .append("productId", getProductId())
                .append("quantity", getQuantity())
                .append("businessType", getBusinessType())
                .append("price", getPrice())
                .toString();
    }
}