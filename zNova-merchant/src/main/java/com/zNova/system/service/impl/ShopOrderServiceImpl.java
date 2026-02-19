package com.zNova.system.service.impl;

import java.math.BigDecimal;
import java.util.List;

import com.zNova.common.exception.ServiceException;
import com.zNova.common.utils.DateUtils;
import com.zNova.system.mapper.BizProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.zNova.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.zNova.system.domain.ShopOrderItem;
import com.zNova.system.mapper.ShopOrderMapper;
import com.zNova.system.domain.ShopOrder;
import com.zNova.system.mapper.AppUserMapper;
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

    @Autowired
    private BizProductMapper bizProductMapper;

    @Autowired
    private AppUserMapper appUserMapper;
    // 状态常量定义
    private static final String STATUS_WAIT_PAY = "0";      // 待支付
    private static final String STATUS_PAID = "1";          // 待发货
    private static final String STATUS_SHIPPED = "2";       // 已发货/进行中
    private static final String STATUS_COMPLETED = "3";     // 已完成
    private static final String STATUS_CANCELLED = "4";     // 已取消
    private static final String STATUS_REFUND_AUDIT = "5";  // 退款审核中
    private static final String STATUS_REFUNDED = "6";      // 已退款
    private static final String STATUS_RETURNING = "7";     // 归还中
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
    public int insertShopOrder(ShopOrder shopOrder) {
        shopOrder.setCreateTime(DateUtils.getNowDate());
        shopOrder.setStatus(STATUS_WAIT_PAY); // 初始状态为待支付

        // 1. 扣减库存逻辑
        if (shopOrder.getShopOrderItemList() != null) {
            for (ShopOrderItem item : shopOrder.getShopOrderItemList()) {
                // 使用 SQL 原子更新扣减库存，返回值 > 0 表示扣减成功
                int rows = bizProductMapper.decreaseStock(item.getProductId(), item.getQuantity());
                if (rows == 0) {
                    throw new ServiceException("商品 [" + item.getProductName() + "] 库存不足，无法下单");
                }
            }
        }

        // 2. 保存订单主表
        int rows = shopOrderMapper.insertShopOrder(shopOrder);

        // 3. 保存明细表
        insertShopOrderItem(shopOrder);
        return rows;
    }

    /**
     * 用户取消订单
     */
    @Transactional
    public void userCancelOrder(Long orderId) {
        ShopOrder order = shopOrderMapper.selectShopOrderByOrderId(orderId);
        if (order == null) throw new ServiceException("订单不存在");

        // 1. 校验状态：必须是 "0=待支付"
        if (!"0".equals(order.getStatus())) {
            throw new ServiceException("只有待支付的订单可以直接取消");
        }

        // 2. 更新状态为 "4=已取消"
        order.setStatus(STATUS_CANCELLED);
        order.setUpdateTime(DateUtils.getNowDate());
        shopOrderMapper.updateShopOrder(order);

        // 3. 【关键步骤】直接自动回滚库存，无需人工介入
        restoreInventory(order.getOrderId());
    }

    /**
     * 用户申请退款
     */
    @Transactional
    public void userApplyRefund(Long orderId) {
        ShopOrder order = shopOrderMapper.selectShopOrderByOrderId(orderId);
        // 仅待发货状态可申请退款（未发货前）
        if (!STATUS_PAID.equals(order.getStatus())) {
            throw new ServiceException("只有待发货的订单可以申请退款");
        }

        order.setStatus(STATUS_REFUND_AUDIT);
        order.setUpdateTime(DateUtils.getNowDate());
        shopOrderMapper.updateShopOrder(order);
    }

    /**
     * 管理员处理退款审核
     * @param pass true=同意, false=拒绝
     */
    @Override
    @Transactional
    public void adminAuditRefund(Long orderId, boolean pass) {
        ShopOrder order = shopOrderMapper.selectShopOrderByOrderId(orderId);
        if (!STATUS_REFUND_AUDIT.equals(order.getStatus())) {
            throw new ServiceException("该订单不是退款审核状态");
        }

        if (pass) {
            // 同意退款：状态变更为已退款，并回滚库存
            order.setStatus(STATUS_REFUNDED);
            restoreInventory(orderId);
        } else {
            // 拒绝退款：恢复为已支付（待发货）状态
            // 实际业务中可能需要记录拒绝原因或恢复到申请前的具体状态，这里简化为恢复到待发货
            order.setStatus(STATUS_PAID);
        }
        order.setUpdateTime(DateUtils.getNowDate());
        shopOrderMapper.updateShopOrder(order);
    }

    /**
     * 辅助方法：回滚库存
     */
    private void restoreInventory(Long orderId) {
        // 查询订单明细
        ShopOrder order = shopOrderMapper.selectShopOrderByOrderId(orderId);
        List<ShopOrderItem> items = order.getShopOrderItemList();

        if (items != null) {
            for (ShopOrderItem item : items) {
                // 调用 Mapper 增加库存
                bizProductMapper.increaseStock(item.getProductId(), item.getQuantity());
            }
        }
    }

    /**
     * 商家确认归还（租赁业务）
     * @param orderId 订单ID
     * @param damageDeduct 损坏扣款金额（可选，为0或null时全额返还押金）
     */
    @Transactional
    @Override
    public void confirmReturn(Long orderId, BigDecimal damageDeduct) {
        // 1. 查询订单信息
        ShopOrder order = shopOrderMapper.selectShopOrderByOrderId(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }

        // 2. 校验状态：必须是 "7=归还中"
        if (!"7".equals(order.getStatus())) {
            throw new ServiceException("只有归还中的订单可以确认归还");
        }

        // 3. 恢复库存
        restoreInventory(orderId);

        // 4. 处理押金解冻
        BigDecimal depositAmount = order.getDepositAmount();
        if (depositAmount != null && depositAmount.compareTo(BigDecimal.ZERO) > 0) {
            // 计算实际返还金额 = 剩余押金 - 损坏扣款
            BigDecimal deductAmount = damageDeduct != null ? damageDeduct : BigDecimal.ZERO;
            // 扣款不能超过剩余押金
            if (deductAmount.compareTo(depositAmount) > 0) {
                deductAmount = depositAmount;
            }
            BigDecimal refundAmount = depositAmount.subtract(deductAmount);
            
            // 解冻押金：从用户冻结押金中扣除，将返还金额加到余额
            appUserMapper.unfreezeDeposit(order.getUserId(), depositAmount, refundAmount);
            
            System.out.println("押金处理：订单ID=" + orderId + ", 原押金=" + depositAmount 
                    + ", 损坏扣款=" + deductAmount + ", 返还金额=" + refundAmount);
        }

        // 5. 更新订单状态为 "3=已完成"
        order.setStatus(STATUS_COMPLETED);
        order.setUpdateTime(DateUtils.getNowDate());
        shopOrderMapper.updateShopOrder(order);

        // 6. 判断是否逾期归还，仅正常归还才增加信用分
        boolean isOverdue = "1".equals(order.getIsOverdue());
        if (!isOverdue) {
            // 正常归还，增加50信用分
            appUserMapper.updateCreditScoreDelta(order.getUserId(), 50);
            System.out.println("商家确认归还订单：订单ID=" + orderId + ", 商品数量=" + order.getShopOrderItemList().size() + ", 用户信用分+50（正常归还）");
        } else {
            // 逾期归还，不增加信用分
            System.out.println("商家确认归还订单：订单ID=" + orderId + ", 商品数量=" + order.getShopOrderItemList().size() + ", 用户信用分不增加（逾期归还）");
        }
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
        // 移除这行删除明细的代码！！！
        // shopOrderMapper.deleteShopOrderItemByOrderId(shopOrder.getOrderId());
        // 也移除insertShopOrderItem(shopOrder);——更新订单时不需要重新插入明细
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

    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public com.zNova.common.core.domain.entity.AppUser selectAppUserByUserId(Long userId)
    {
        return appUserMapper.selectAppUserByUserId(userId);
    }

}