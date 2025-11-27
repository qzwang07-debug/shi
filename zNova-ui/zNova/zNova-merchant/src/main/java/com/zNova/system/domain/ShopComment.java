package com.zNova.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zNova.common.annotation.Excel;
import com.zNova.common.core.domain.BaseEntity;

/**
 * 商品评价对象 shop_comment
 *
 * @author wyz
 * @date 2025-11-21
 */
public class ShopComment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评论ID */
    private Long commentId;

    /** 关联商品ID */
    @Excel(name = "关联商品ID")
    private Long productId;

    /** 评论者ID (关联 app_user) */
    @Excel(name = "评论者ID (关联 app_user)")
    private Long userId;

    /** 关联订单ID (买了才能评) */
    @Excel(name = "关联订单ID (买了才能评)")
    private Long orderId;

    /** 评分星级(1-5) */
    @Excel(name = "评分星级(1-5)")
    private Integer star;

    /** 评论内容 */
    @Excel(name = "评论内容")
    private String content;

    /** 商家回复内容 */
    @Excel(name = "商家回复内容")
    private String replyContent;

    /** 商家回复时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "商家回复时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date replyTime;

    /** 是否匿名(0否 1是) */
    @Excel(name = "是否匿名(0否 1是)")
    private String isAnonymous;

    /** 删除标志 */
    private String delFlag;

    /** 非数据库字段：评论人昵称 */
    private String nickname;

    /** 非数据库字段：评论人头像 */
    private String avatar;

    public void setCommentId(Long commentId)
    {
        this.commentId = commentId;
    }

    public Long getCommentId()
    {
        return commentId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public Long getProductId()
    {
        return productId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setStar(Integer star)
    {
        this.star = star;
    }

    public Integer getStar()
    {
        return star;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }

    public void setReplyContent(String replyContent)
    {
        this.replyContent = replyContent;
    }

    public String getReplyContent()
    {
        return replyContent;
    }

    public void setReplyTime(Date replyTime)
    {
        this.replyTime = replyTime;
    }

    public Date getReplyTime()
    {
        return replyTime;
    }

    public void setIsAnonymous(String isAnonymous)
    {
        this.isAnonymous = isAnonymous;
    }

    public String getIsAnonymous()
    {
        return isAnonymous;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("commentId", getCommentId())
                .append("productId", getProductId())
                .append("userId", getUserId())
                .append("orderId", getOrderId())
                .append("star", getStar())
                .append("content", getContent())
                .append("replyContent", getReplyContent())
                .append("replyTime", getReplyTime())
                .append("isAnonymous", getIsAnonymous())
                .append("createTime", getCreateTime())
                .append("delFlag", getDelFlag())
                .append("nickname", getNickname())
                .append("avatar", getAvatar())
                .toString();
    }
}
