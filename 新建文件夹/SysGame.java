package com.zNova.system.domain;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zNova.common.annotation.Excel;
import com.zNova.common.core.domain.BaseEntity;

/**
 * 游戏性能管理对象 sys_game
 * 
 * @author wyz
 * @date 2025-11-30
 */
public class SysGame extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 游戏ID */
    private Long gameId;

    /** 游戏名称(如: Black Myth: Wukong) */
    @Excel(name = "游戏名称(如: Black Myth: Wukong)")
    private String gameName;

    /** 游戏描述 */
    @Excel(name = "游戏描述")
    private String description;

    /** 游戏性能基准信息 */
    private List<SysGameBenchmark> sysGameBenchmarkList;

    public void setGameId(Long gameId) 
    {
        this.gameId = gameId;
    }

    public Long getGameId() 
    {
        return gameId;
    }

    public void setGameName(String gameName) 
    {
        this.gameName = gameName;
    }

    public String getGameName() 
    {
        return gameName;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public List<SysGameBenchmark> getSysGameBenchmarkList()
    {
        return sysGameBenchmarkList;
    }

    public void setSysGameBenchmarkList(List<SysGameBenchmark> sysGameBenchmarkList)
    {
        this.sysGameBenchmarkList = sysGameBenchmarkList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("gameId", getGameId())
            .append("gameName", getGameName())
            .append("description", getDescription())
            .append("sysGameBenchmarkList", getSysGameBenchmarkList())
            .toString();
    }
}
