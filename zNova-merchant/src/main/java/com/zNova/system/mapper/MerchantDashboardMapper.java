package com.zNova.system.mapper;

import com.zNova.system.domain.BizProduct;
import com.zNova.system.domain.ShopOrder;
import com.zNova.system.domain.vo.DashboardStatsVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 商家首页统计 Mapper
 */
@Mapper
public interface MerchantDashboardMapper {

    /**
     * 聚合查询订单统计数据
     * 注意：这里不需要 @DataScope，权限控制移至 Service 层
     *
     * @param shopOrder 用于接收数据权限参数的实体
     * @return 映射到 VO 对象
     */
    DashboardStatsVO selectOrderAggregatedStats(ShopOrder shopOrder);

    /**
     * 查询商品总数
     *
     * @param bizProduct 用于接收数据权限参数的实体
     * @return 商品数量
     */
    Long selectProductCount(BizProduct bizProduct);

    /**
     * 查询过去 7 天的趋势数据 (分组统计)
     * @param shopOrder 仅仅为了传递 params.dataScope
     * @return List<Map<String, Object>> 包含 date, saleCount, rentCount
     */
    List<Map<String, Object>> selectTrendData(ShopOrder shopOrder);
}