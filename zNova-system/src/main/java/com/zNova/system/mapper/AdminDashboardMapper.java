package com.zNova.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

/**
 * 管理员首页统计 Mapper
 */
@Mapper
public interface AdminDashboardMapper {

    /**
     * 查询C端用户总数
     *
     * @return C端用户数量
     */
    Long selectTotalConsumerCount();

    /**
     * 查询商家总数（角色为'common'的用户）
     *
     * @return 商家数量
     */
    Long selectTotalMerchantCount();

    /**
     * 查询平台累计交易额
     * 购买类：price×quantity
     * 租赁类：price×rent_days
     * 订单状态为'3'
     *
     * @return 累计交易额
     */
    Double selectTotalTransactionAmount();

    /**
     * 查询平台累计订单数
     *
     * @return 累计订单数
     */
    Long selectTotalOrderCount();

    /**
     * 查询近30天系统访问趋势
     *
     * @return 包含日期和访问人数的Map列表
     */
    List<Map<String, Object>> selectRecent30DaysVisitTrend();
}