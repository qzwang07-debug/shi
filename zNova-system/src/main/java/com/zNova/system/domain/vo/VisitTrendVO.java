package com.zNova.system.domain.vo;

import java.util.List;

/**
 * 系统访问趋势 VO
 * 用于封装近30日系统访问趋势统计数据
 */
public class VisitTrendVO {

    /**
     * 日期列表，格式：yyyy-MM-dd
     */
    private List<String> dates;

    /**
     * 每天的访问人数列表
     */
    private List<Long> counts;

    // 构造方法
    public VisitTrendVO() {
    }

    public VisitTrendVO(List<String> dates, List<Long> counts) {
        this.dates = dates;
        this.counts = counts;
    }

    // getter 和 setter 方法
    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public List<Long> getCounts() {
        return counts;
    }

    public void setCounts(List<Long> counts) {
        this.counts = counts;
    }
}