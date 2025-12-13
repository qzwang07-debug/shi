package com.zNova.system.service.impl;

import com.zNova.system.domain.vo.AdminDashboardStatsVO;
import com.zNova.system.domain.vo.VisitTrendVO;
import com.zNova.system.mapper.AdminDashboardMapper;
import com.zNova.system.service.IAdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员面板统计 Service 实现类
 */
@Service
public class AdminDashboardServiceImpl implements IAdminDashboardService {

    @Autowired
    private AdminDashboardMapper adminDashboardMapper;

    @Override
    public AdminDashboardStatsVO getAdminStats() {
        AdminDashboardStatsVO stats = new AdminDashboardStatsVO();
        stats.setTotalConsumerCount(adminDashboardMapper.selectTotalConsumerCount());
        stats.setTotalMerchantCount(adminDashboardMapper.selectTotalMerchantCount());
        stats.setTotalTransactionAmount(adminDashboardMapper.selectTotalTransactionAmount());
        stats.setTotalOrderCount(adminDashboardMapper.selectTotalOrderCount());
        return stats;
    }

    @Override
    public VisitTrendVO getRecent30DaysVisitTrend() {
        // 1. 从数据库查询近30天的访问数据
        List<Map<String, Object>> dbResults = adminDashboardMapper.selectRecent30DaysVisitTrend();

        // 2. 生成过去30天的完整日期列表
        List<String> last30Days = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 29; i >= 0; i--) {
            last30Days.add(today.minusDays(i).format(formatter));
        }

        // 3. 将数据库结果转为 Map<日期, 用户数> 格式，方便查找
        Map<String, Long> userCountMap = dbResults.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("dateStr"),
                        m -> Long.parseLong(String.valueOf(m.get("userCount")))
                ));

        // 4. 组装最终结果：遍历完整日期列表，从 Map 中获取对应数据，缺失的补0
        List<Long> userCounts = new ArrayList<>();
        for (String day : last30Days) {
            userCounts.add(userCountMap.getOrDefault(day, 0L));
        }

        // 5. 构造并返回 VO 对象
        return new VisitTrendVO(last30Days, userCounts);
    }
}