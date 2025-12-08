package com.zNova.system.service;

import java.util.List;
import com.zNova.system.domain.ShopOrder;
import org.springframework.stereotype.Service;

/**
 * 订单主Service接口
 * 
 * @author wyz
 * @date 2025-11-24
 */
public interface IShopOrderService 
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
     * 批量删除订单主
     * 
     * @param orderIds 需要删除的订单主主键集合
     * @return 结果
     */
    public int deleteShopOrderByOrderIds(Long[] orderIds);

    /**
     * 删除订单主信息
     * 
     * @param orderId 订单主主键
     * @return 结果
     */
    public int deleteShopOrderByOrderId(Long orderId);

    /**
     * 用户取消订单
     */
    void userCancelOrder(Long orderId);

    /**
     * 用户申请退款
     */
    void userApplyRefund(Long orderId);

    /**
     * 管理员审核退款
     */
    void adminAuditRefund(Long orderId, boolean pass);
    /**
     * 商家确认归还（租赁业务）
     * @param orderId 订单ID
     */
    void confirmReturn(Long orderId);

    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    com.zNova.common.core.domain.entity.AppUser selectAppUserByUserId(Long userId);
}
