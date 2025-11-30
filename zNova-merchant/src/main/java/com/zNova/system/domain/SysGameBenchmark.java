package com.zNova.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zNova.common.annotation.Excel;
import com.zNova.common.core.domain.BaseEntity;

/**
 * 游戏性能基准对象 sys_game_benchmark
 * 
 * @author wyz
 * @date 2025-11-30
 */
public class SysGameBenchmark extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 关联游戏ID */
    @Excel(name = "关联游戏ID")
    private Long gameId;

    /** 基准类型(CPU_INTEL, CPU_AMD, GPU_NVIDIA, GPU_AMD) */
    @Excel(name = "基准类型(CPU_INTEL, CPU_AMD, GPU_NVIDIA, GPU_AMD)")
    private String platformType;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private BigDecimal baseScore;

    /** 基准测试帧率(分子) */
    @Excel(name = "基准测试帧率(分子)")
    private Long baseFps;

    /** 分辨率基准 */
    @Excel(name = "分辨率基准")
    private String resolution;

    /** 分数段下限 */
    @Excel(name = "分数段下限")
    private Long minScore;

    /** 分数段上限 */
    @Excel(name = "分数段上限")
    private Long maxScore;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setGameId(Long gameId) 
    {
        this.gameId = gameId;
    }

    public Long getGameId() 
    {
        return gameId;
    }
    public void setPlatformType(String platformType) 
    {
        this.platformType = platformType;
    }

    public String getPlatformType() 
    {
        return platformType;
    }
    public void setBaseScore(BigDecimal baseScore) 
    {
        this.baseScore = baseScore;
    }

    public BigDecimal getBaseScore() 
    {
        return baseScore;
    }
    public void setBaseFps(Long baseFps) 
    {
        this.baseFps = baseFps;
    }

    public Long getBaseFps() 
    {
        return baseFps;
    }
    public void setResolution(String resolution) 
    {
        this.resolution = resolution;
    }

    public String getResolution() 
    {
        return resolution;
    }
    public void setMinScore(Long minScore) 
    {
        this.minScore = minScore;
    }

    public Long getMinScore() 
    {
        return minScore;
    }
    public void setMaxScore(Long maxScore) 
    {
        this.maxScore = maxScore;
    }

    public Long getMaxScore() 
    {
        return maxScore;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("gameId", getGameId())
            .append("platformType", getPlatformType())
            .append("baseScore", getBaseScore())
            .append("baseFps", getBaseFps())
            .append("resolution", getResolution())
            .append("minScore", getMinScore())
            .append("maxScore", getMaxScore())
            .toString();
    }
}
