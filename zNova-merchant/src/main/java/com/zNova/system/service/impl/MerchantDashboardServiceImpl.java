package com.zNova.system.service.impl;

import com.zNova.common.annotation.DataScope;
import com.zNova.system.domain.BizProduct;
import com.zNova.system.domain.ShopOrder;
import com.zNova.system.domain.vo.DashboardStatsVO;
import com.zNova.system.domain.vo.DashboardTrendVO;
import com.zNova.system.mapper.MerchantDashboardMapper;
import com.zNova.system.service.IMerchantDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import java.util.Map;

/**
 * 商家面板统计 Service 实现类
 */
@Service
public class MerchantDashboardServiceImpl implements IMerchantDashboardService {

    @Autowired
    private MerchantDashboardMapper merchantDashboardMapper;

    /**
     * 注入自身代理对象，解决同类内部方法调用 AOP 失效的问题
     * 加 @Lazy 防止循环依赖
     */
    @Resource
    @Lazy
    private MerchantDashboardServiceImpl self;

    @Override
    public DashboardStatsVO getDashboardStats() {
        DashboardStatsVO vo = new DashboardStatsVO();

        // 1. 准备查询实体（必须作为参数传递，否则 @DataScope 无法注入 SQL）
        ShopOrder orderQuery = new ShopOrder();
        BizProduct productQuery = new BizProduct();

        // 2. 调用带 @DataScope 注解的代理方法
        // 注意：必须通过 self. 调用，不能直接 this. 调用，否则 AOP 不生效
        DashboardStatsVO orderStats = self.selectOrderStatsWithScope(orderQuery);
        Long productCount = self.selectProductCountWithScope(productQuery);

        // 3. 组装数据
        if (orderStats != null) {
            vo.setTodayOrderCount(orderStats.getTodayOrderCount());
            vo.setPendingShipmentCount(orderStats.getPendingShipmentCount());
            vo.setRentingCount(orderStats.getRentingCount());
        }
        vo.setTotalProductCount(productCount != null ? productCount : 0L);

        return vo;
    }
    @Override
    public DashboardTrendVO getDashboardTrend() {
        // 1. 也是通过自我注入的方法调用，确保权限生效
        ShopOrder query = new ShopOrder();
        List<Map<String, Object>> dbResults = self.selectTrendDataWithScope(query);

        // 2. 构造过去 7 天的完整日期列表
        List<String> last7Days = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 6; i >= 0; i--) {
            last7Days.add(today.minusDays(i).format(formatter));
        }

        // 3. 将数据库结果转为 Map<日期, 数据> 方便查找
        Map<String, Map<String, Object>> dataMap = dbResults.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("dateStr"),
                        m -> m
                ));

        // 4. 组装最终结果 (对齐日期，缺少的补 0)
        List<Long> salesList = new ArrayList<>();
        List<Long> rentalList = new ArrayList<>();

        for (String day : last7Days) {
            Map<String, Object> dayData = dataMap.get(day);
            if (dayData != null) {
                // 注意处理 Long/BigDecimal 类型转换，安全起见转 String 再转 Long
                salesList.add(Long.parseLong(String.valueOf(dayData.getOrDefault("saleCount", 0))));
                rentalList.add(Long.parseLong(String.valueOf(dayData.getOrDefault("rentCount", 0))));
            } else {
                salesList.add(0L);
                rentalList.add(0L);
            }
        }

        DashboardTrendVO vo = new DashboardTrendVO();
        vo.setCategories(last7Days);
        vo.setSalesData(salesList);
        vo.setRentalData(rentalList);

        return vo;
    }

    // --- AOP 辅助方法 ---
    @DataScope(deptAlias = "so", userAlias = "so")
    public List<Map<String, Object>> selectTrendDataWithScope(ShopOrder shopOrder) {
        return merchantDashboardMapper.selectTrendData(shopOrder);
    }
    /**
     * 辅助方法：查询订单统计
     * 必须是 public，必须有参数，@DataScope 才能生效
     */
    @DataScope(deptAlias = "so", userAlias = "so")
    public DashboardStatsVO selectOrderStatsWithScope(ShopOrder shopOrder) {
        return merchantDashboardMapper.selectOrderAggregatedStats(shopOrder);
    }

    /**
     * 辅助方法：查询商品总数
     * 必须是 public，必须有参数，@DataScope 才能生效
     */
    @DataScope(deptAlias = "bp", userAlias = "bp")
    public Long selectProductCountWithScope(BizProduct bizProduct) {
        return merchantDashboardMapper.selectProductCount(bizProduct);
    }
}