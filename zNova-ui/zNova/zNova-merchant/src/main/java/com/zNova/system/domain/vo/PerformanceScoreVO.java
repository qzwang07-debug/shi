package com.zNova.system.domain.vo;

import java.io.Serializable;

/**
 * 商品性能评分视图对象（用于前端展示）
 */
public class PerformanceScoreVO implements Serializable {
    private Integer totalScore;        // 总分数
    private Integer cpuScore;          // CPU分数
    private Integer memoryScore;       // 内存分数
    private Integer gpuScore;          // 显卡分数
    private Integer percentage;        // 百分比（相对于满分/用于进度条）
    private static final long serialVersionUID = 1L;


    // 带参构造器（对应所有变量）
    public PerformanceScoreVO(Integer totalScore, Integer cpuScore, Integer memoryScore, Integer gpuScore, Integer percentage) {
        this.totalScore = totalScore;
        this.cpuScore = cpuScore;
        this.memoryScore = memoryScore;
        this.gpuScore = gpuScore;
        this.percentage = percentage;
    }

    // 无参构造器
    public PerformanceScoreVO() {}

    // getter和setter方法（保持与变量一一对应）
    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getCpuScore() {
        return cpuScore;
    }

    public void setCpuScore(Integer cpuScore) {
        this.cpuScore = cpuScore;
    }

    public Integer getMemoryScore() {
        return memoryScore;
    }

    public void setMemoryScore(Integer memoryScore) {
        this.memoryScore = memoryScore;
    }

    public Integer getGpuScore() {
        return gpuScore;
    }

    public void setGpuScore(Integer gpuScore) {
        this.gpuScore = gpuScore;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }
}