package com.zNova.system.controller;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.common.utils.SecurityUtils;
import com.zNova.common.utils.uuid.IdUtils;
import com.zNova.system.domain.AppAddress;
import com.zNova.system.domain.BizProduct;
import com.zNova.system.domain.ShopOrder;
import com.zNova.system.domain.ShopOrderItem;
import com.zNova.system.service.IAppAddressService;
import com.zNova.system.service.IBizProductService;
import com.zNova.system.service.IShopCartService;
import com.zNova.system.service.IShopOrderService;

/**
 * C端订单管理Controller
 * * @author zNova
 */
@RestController
@RequestMapping("/app/order")
public class AppOrderController extends BaseController
{
    @Autowired
    private IShopOrderService shopOrderService;

    @Autowired
    private IAppAddressService appAddressService;

    @Autowired
    private IBizProductService productService;

    @Autowired
    private IShopCartService shopCartService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult create(@RequestBody OrderCreateBody orderBody)
    {
        Long userId = SecurityUtils.getUserId();

        // 1. 校验地址信息
        AppAddress address = appAddressService.selectAppAddressByAddressId(orderBody.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            return error("收货地址不存在或无效");
        }

        if (orderBody.getItems() == null || orderBody.getItems().isEmpty()) {
            return error("订单商品不能为空");
        }

        // 2. 准备数据：获取商品详情并按商家(DeptId)分组
        // Map<DeptId, List<ShopOrderItem>>
        Map<Long, List<ShopOrderItem>> merchantItemsMap = new HashMap<>();
        // 记录需要删除的购物车ID
        List<Long> cartIdsToDelete = new ArrayList<>();

        for (CartItemRequest itemReq : orderBody.getItems()) {
            BizProduct product = productService.selectBizProductById(itemReq.getProductId());
            if (product == null) {
                throw new RuntimeException("商品已下架: ID " + itemReq.getProductId());
            }

            // 校验库存
            if (itemReq.getQuantity() > product.getStockQuantity()) {
                throw new RuntimeException("商品 [" + product.getProductName() + "] 库存不足");
            }
            // 租赁模式下的额外库存校验
            if ("1".equals(itemReq.getBusinessType()) && itemReq.getQuantity() > product.getAvailableRent()) {
                throw new RuntimeException("商品 [" + product.getProductName() + "] 可租赁数量不足");
            }

            // 构建订单明细对象
            ShopOrderItem orderItem = new ShopOrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getProductName());
            orderItem.setProductImg(product.getImageUrl());
            orderItem.setBusinessType(itemReq.getBusinessType()); // 1租赁 2购买
            orderItem.setQuantity(Long.valueOf(itemReq.getQuantity()));
            orderItem.setDeptId(product.getDeptId()); // 关键：记录该商品的归属商家

            // 计算价格
            BigDecimal unitPrice;
            if ("1".equals(itemReq.getBusinessType())) {
                // 租赁模式
                if (itemReq.getStartDate() == null || itemReq.getEndDate() == null) {
                    throw new RuntimeException("租赁商品必须选择起止时间");
                }
                unitPrice = product.getRentPrice();
                // 计算天数 (向上取整或至少1天)
                long diff = itemReq.getEndDate().getTime() - itemReq.getStartDate().getTime();
                long days = diff / (1000 * 3600 * 24);
                days = days <= 0 ? 1 : days; // 至少算1天

                orderItem.setRentStartTime(itemReq.getStartDate());
                orderItem.setRentEndTime(itemReq.getEndDate());
                orderItem.setRentDays(days);
                orderItem.setPrice(unitPrice); // 记录单价

            } else {
                // 销售模式
                unitPrice = product.getSalePrice();
                orderItem.setPrice(unitPrice);
            }

            // 放入分组 Map
            merchantItemsMap.computeIfAbsent(product.getDeptId(), k -> new ArrayList<>()).add(orderItem);

            // 收集购物车ID以便后续删除
            if (itemReq.getCartId() != null) {
                cartIdsToDelete.add(itemReq.getCartId());
            }
        }

        // 3. 按商家拆单并入库
        List<String> orderNos = new ArrayList<>();

        for (Map.Entry<Long, List<ShopOrderItem>> entry : merchantItemsMap.entrySet()) {
            Long merchantId = entry.getKey();
            List<ShopOrderItem> items = entry.getValue();

            ShopOrder order = new ShopOrder();
            order.setOrderNo(IdUtils.fastSimpleUUID());
            order.setUserId(userId);
            order.setStatus("0"); // 待支付
            order.setDeptId(merchantId); // 关键：订单归属于对应商家，确保数据权限生效

            // 地址快照
            order.setReceiverName(address.getRealName());
            order.setReceiverPhone(address.getPhone());
            order.setReceiverAddress(address.getProvince() + " " + address.getCity() + " " + address.getDistrict() + " " + address.getDetailAddress());
            order.setRemark(orderBody.getRemark());

            // 计算该子订单总金额
            BigDecimal orderTotal = BigDecimal.ZERO;
            for (ShopOrderItem item : items) {
                BigDecimal itemTotal;
                if ("1".equals(item.getBusinessType())) {
                    // 租金 * 数量 * 天数
                    BigDecimal days = new BigDecimal(item.getRentDays());
                    BigDecimal qty = new BigDecimal(item.getQuantity());
                    itemTotal = item.getPrice().multiply(days).multiply(qty);
                } else {
                    // 售价 * 数量
                    BigDecimal qty = new BigDecimal(item.getQuantity());
                    itemTotal = item.getPrice().multiply(qty);
                }
                orderTotal = orderTotal.add(itemTotal);
            }
            order.setTotalAmount(orderTotal);
            order.setShopOrderItemList(items);

            // 保存订单主表（Service中会自动处理关联的Item插入）
            shopOrderService.insertShopOrder(order);
            orderNos.add(order.getOrderNo());
        }

        // 4. 清理购物车
        if (!cartIdsToDelete.isEmpty()) {
            shopCartService.deleteShopCartByIds(cartIdsToDelete.toArray(new Long[0]));
        }

        // --- 修复点：调用 AjaxResult 的静态方法，而不是 BaseController 的实例方法 ---
        return AjaxResult.success("订单创建成功", orderNos);
    }

    // ================= 内部 DTO 类 =================

    /** 订单创建请求体 */
    public static class OrderCreateBody {
        private Long addressId;
        private String remark;
        private List<CartItemRequest> items;

        public Long getAddressId() { return addressId; }
        public void setAddressId(Long addressId) { this.addressId = addressId; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        public List<CartItemRequest> getItems() { return items; }
        public void setItems(List<CartItemRequest> items) { this.items = items; }
    }

    /** 订单商品项请求 */
    public static class CartItemRequest {
        private Long cartId; // 购物车ID（可选，用于删除）
        private Long productId;
        private Integer quantity;
        private String businessType; // 1租赁 2购买
        private Date startDate; // 租赁开始时间
        private Date endDate;   // 租赁结束时间

        public Long getCartId() { return cartId; }
        public void setCartId(Long cartId) { this.cartId = cartId; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getBusinessType() { return businessType; }
        public void setBusinessType(String businessType) { this.businessType = businessType; }
        public Date getStartDate() { return startDate; }
        public void setStartDate(Date startDate) { this.startDate = startDate; }
        public Date getEndDate() { return endDate; }
        public void setEndDate(Date endDate) { this.endDate = endDate; }
    }
}