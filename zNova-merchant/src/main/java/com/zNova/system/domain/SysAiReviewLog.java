package com.zNova.system.domain;

import com.zNova.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SysAiReviewLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String hardwareSummary;
    private String aiResponse;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getHardwareSummary()
    {
        return hardwareSummary;
    }

    public void setHardwareSummary(String hardwareSummary)
    {
        this.hardwareSummary = hardwareSummary;
    }

    public String getAiResponse()
    {
        return aiResponse;
    }

    public void setAiResponse(String aiResponse)
    {
        this.aiResponse = aiResponse;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("hardwareSummary", getHardwareSummary())
            .append("aiResponse", getAiResponse())
            .append("createTime", getCreateTime())
            .toString();
    }
}
