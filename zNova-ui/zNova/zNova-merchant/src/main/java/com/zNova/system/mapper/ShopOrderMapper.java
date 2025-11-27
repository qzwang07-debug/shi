package com.zNova.system.mapper;

import java.util.List;
import com.zNova.system.domain.ShopOrder;
import com.zNova.system.domain.ShopOrderItem;

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
}
