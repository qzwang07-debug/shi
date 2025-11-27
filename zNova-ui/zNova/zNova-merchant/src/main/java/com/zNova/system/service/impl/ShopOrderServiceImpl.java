package com.zNova.system.service.impl;

import java.util.List;
import com.zNova.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.zNova.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.zNova.system.domain.ShopOrderItem;
import com.zNova.system.mapper.ShopOrderMapper;
import com.zNova.system.domain.ShopOrder;
import com.zNova.system.service.IShopOrderService;

/**
 * 订单主Service业务层处理
 * 
 * @author wyz
 * @date 2025-11-24
 */
@Service
public class ShopOrderServiceImpl implements IShopOrderService 
{
    @Autowired
    private ShopOrderMapper shopOrderMapper;

    /**
     * 查询订单主
     * 
     * @param orderId 订单主主键
     * @return 订单主
     */
    @Override
    public ShopOrder selectShopOrderByOrderId(Long orderId)
    {
        return shopOrderMapper.selectShopOrderByOrderId(orderId);
    }

    /**
     * 查询订单主列表
     * 
     * @param shopOrder 订单主
     * @return 订单主
     */
    @Override
    public List<ShopOrder> selectShopOrderList(ShopOrder shopOrder)
    {
        return shopOrderMapper.selectShopOrderList(shopOrder);
    }

    /**
     * 新增订单主
     * 
     * @param shopOrder 订单主
     * @return 结果
     */
    @Transactional
    @Override
    public int insertShopOrder(ShopOrder shopOrder)
    {
        shopOrder.setCreateTime(DateUtils.getNowDate());
        int rows = shopOrderMapper.insertShopOrder(shopOrder);
        insertShopOrderItem(shopOrder);
        return rows;
    }

    /**
     * 修改订单主
     * 
     * @param shopOrder 订单主
     * @return 结果
     */
    @Transactional
    @Override
    public int updateShopOrder(ShopOrder shopOrder)
    {
        shopOrder.setUpdateTime(DateUtils.getNowDate());
        shopOrderMapper.deleteShopOrderItemByOrderId(shopOrder.getOrderId());
        insertShopOrderItem(shopOrder);
        return shopOrderMapper.updateShopOrder(shopOrder);
    }

    /**
     * 批量删除订单主
     * 
     * @param orderIds 需要删除的订单主主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteShopOrderByOrderIds(Long[] orderIds)
    {
        shopOrderMapper.deleteShopOrderItemByOrderIds(orderIds);
        return shopOrderMapper.deleteShopOrderByOrderIds(orderIds);
    }

    /**
     * 删除订单主信息
     * 
     * @param orderId 订单主主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteShopOrderByOrderId(Long orderId)
    {
        shopOrderMapper.deleteShopOrderItemByOrderId(orderId);
        return shopOrderMapper.deleteShopOrderByOrderId(orderId);
    }

    /**
     * 新增订单明细信息
     * 
     * @param shopOrder 订单主对象
     */
    public void insertShopOrderItem(ShopOrder shopOrder)
    {
        List<ShopOrderItem> shopOrderItemList = shopOrder.getShopOrderItemList();
        Long orderId = shopOrder.getOrderId();
        if (StringUtils.isNotNull(shopOrderItemList))
        {
            List<ShopOrderItem> list = new ArrayList<ShopOrderItem>();
            for (ShopOrderItem shopOrderItem : shopOrderItemList)
            {
                shopOrderItem.setOrderId(orderId);
                list.add(shopOrderItem);
            }
            if (list.size() > 0)
            {
                shopOrderMapper.batchShopOrderItem(list);
            }
        }
    }
}
