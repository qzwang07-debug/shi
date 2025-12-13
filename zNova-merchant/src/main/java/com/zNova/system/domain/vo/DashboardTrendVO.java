package com.zNova.system.domain.vo;


import java.util.List;

/**
 * 首页图表趋势数据 VO
 */
public class DashboardTrendVO {
    // X 轴：日期列表 (e.g. ["2023-10-01", "2023-10-02"])
    private List<String> categories;

    // Y 轴：销售数据
    private List<Long> salesData;

    // Y 轴：租赁数据
    private List<Long> rentalData;

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<Long> getSalesData() {
        return salesData;
    }

    public void setSalesData(List<Long> salesData) {
        this.salesData = salesData;
    }

    public List<Long> getRentalData() {
        return rentalData;
    }

    public void setRentalData(List<Long> rentalData) {
        this.rentalData = rentalData;
    }
}
