package com.zNova.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zNova.common.annotation.Excel;
import com.zNova.common.core.domain.BaseEntity;

/**
 * 内存性能曲线对象 sys_game_ram_benchmark
 * 
 * @author wyz
 * @date 2025-11-30
 */
public class SysGameRamBenchmark extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 关联游戏ID */
    @Excel(name = "关联游戏ID")
    private Long gameId;

    /** 内存代数(DDR4, DDR5) */
    @Excel(name = "内存代数(DDR4, DDR5)")
    private String ramType;

    /** 内存频率(MHz) */
    @Excel(name = "内存频率(MHz)")
    private Long frequency;

    /** 帧率比系数(如 1.208) */
    @Excel(name = "帧率比系数(如 1.208)")
    private BigDecimal ratio;

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

    public void setRamType(String ramType) 
    {
        this.ramType = ramType;
    }

    public String getRamType() 
    {
        return ramType;
    }

    public void setFrequency(Long frequency) 
    {
        this.frequency = frequency;
    }

    public Long getFrequency() 
    {
        return frequency;
    }

    public void setRatio(BigDecimal ratio) 
    {
        this.ratio = ratio;
    }

    public BigDecimal getRatio() 
    {
        return ratio;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("gameId", getGameId())
            .append("ramType", getRamType())
            .append("frequency", getFrequency())
            .append("ratio", getRatio())
            .toString();
    }
}
