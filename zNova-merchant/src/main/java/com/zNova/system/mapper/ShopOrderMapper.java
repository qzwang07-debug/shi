package com.zNova.system.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.zNova.system.domain.ShopOrder;
import com.zNova.system.domain.ShopOrderItem;
import org.apache.ibatis.annotations.Param;

/**
 * 订单主Mapper接口
 * 
 * @author wyz
 * @date 2025-11-24
 */
public interface ShopOrderMapper 
{
    /**
     * 查询订单主
     * 
     * @param orderId 订单主主键
     * @return 订单主
     */
    public ShopOrder selectShopOrderByOrderId(Long orderId);

    /**
     * 查询订单主列表
     * 
     * @param shopOrder 订单主
     * @return 订单主集合
     */
    public List<ShopOrder> selectShopOrderList(ShopOrder shopOrder);

    /**
     * 新增订单主
     * 
     * @param shopOrder 订单主
     * @return 结果
     */
    public int insertShopOrder(ShopOrder shopOrder);

    /**
     * 修改订单主
     * 
     * @param shopOrder 订单主
     * @return 结果
     */
    public int updateShopOrder(ShopOrder shopOrder);

    /**
     * 删除订单主
     * 
     * @param orderId 订单主主键
     * @return 结果
     */
    public int deleteShopOrderByOrderId(Long orderId);

    /**
     * 批量删除订单主
     * 
     * @param orderIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteShopOrderByOrderIds(Long[] orderIds);

    /**
     * 批量删除订单明细
     * 
     * @param orderIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteShopOrderItemByOrderIds(Long[] orderIds);
    
    /**
     * 批量新增订单明细
     * 
     * @param shopOrderItemList 订单明细列表
     * @return 结果
     */
    public int batchShopOrderItem(List<ShopOrderItem> shopOrderItemList);
    

    /**
     * 通过订单主主键删除订单明细信息
     * 
     * @param orderId 订单主ID
     * @return 结果
     */
    public int deleteShopOrderItemByOrderId(Long orderId);

    /**
     * 查询已逾期的租赁订单明细（租赁结束时间 < 当前日期，且订单状态为租赁中）
     * @return 逾期订单明细列表
     */
    public List<ShopOrderItem> selectOverdueRentalItems();

    /**
     * 更新订单明细的已扣逾期天数
     * @param itemId 明细ID
     * @param overdueDeductDays 已扣天数
     * @return 结果
     */
    public int updateOverdueDeductDays(@Param("itemId") Long itemId, @Param("overdueDeductDays") Integer overdueDeductDays);

    /**
     * 扣减订单押金
     * @param orderId 订单ID
     * @param deductAmount 扣减金额
     * @return 结果
     */
    public int deductDepositAmount(@Param("orderId") Long orderId, @Param("deductAmount") BigDecimal deductAmount);

    /**
     * 标记订单为已逾期
     * @param orderId 订单ID
     * @return 结果
     */
    public int markOrderOverdue(@Param("orderId") Long orderId);

    /**
     * 查询即将到期的租赁订单明细（用于定时任务）
     * @param daysBeforeExpiry 到期前天数
     * @return 即将到期的订单明细列表
     */
    public List<ShopOrderItem> selectExpiringSoonItems(@Param("daysBeforeExpiry") int daysBeforeExpiry);

    /**
     * 查询指定用户即将到期的租赁订单明细（用于前端提醒）
     * @param userId 用户ID
     * @param daysBeforeExpiry 到期前天数
     * @return 即将到期的订单明细列表
     */
    public List<ShopOrderItem> selectExpiringSoonItemsByUserId(@Param("userId") Long userId, @Param("daysBeforeExpiry") int daysBeforeExpiry);
}
