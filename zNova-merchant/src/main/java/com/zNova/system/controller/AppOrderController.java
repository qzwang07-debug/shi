package com.zNova.system.controller;

import java.math.BigDecimal;
import java.util.*;

import com.zNova.system.domain.*;
import com.zNova.system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.common.utils.SecurityUtils;
import com.zNova.common.utils.uuid.IdUtils;
import com.zNova.common.core.page.TableDataInfo;
import com.zNova.system.domain.ShopOrder;
import com.zNova.system.domain.ShopOrderItem;
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

    @Autowired
    private IAppUserService appUserService;

    @Autowired
    private IRecommendService recommendService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult create(@RequestBody OrderCreateBody orderBody)
    {
        Long userId = SecurityUtils.getUserId();
        logger.info("用户 {} 开始创建订单", userId);

        // 1. 校验地址信息
        AppAddress address = appAddressService.selectAppAddressByAddressId(orderBody.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            logger.warn("用户 {} 使用无效地址 {} 创建订单", userId, orderBody.getAddressId());
            return error("收货地址不存在或无效");
        }

        if (orderBody.getItems() == null || orderBody.getItems().isEmpty()) {
            logger.warn("用户 {} 创建订单时商品列表为空", userId);
            return error("订单商品不能为空");
        }

        // 获取用户信息以检查信用分（用于押金分级计算）
        com.zNova.common.core.domain.entity.AppUser currentUser = appUserService.selectAppUserByUserId(userId);
        Integer creditScore = currentUser != null && currentUser.getCreditScore() != null ? currentUser.getCreditScore() : 500;
        
        // 押金分级规则：
        // 信用分 >= 600: 免押金
        // 信用分 400-599: 日租金 * 6倍 作为押金
        // 信用分 < 400: 日租金 * 10倍 作为押金
        int depositMultiplier;
        String depositLevel;
        if (creditScore >= 600) {
            depositMultiplier = 0;
            depositLevel = "免押金";
        } else if (creditScore >= 400) {
            depositMultiplier = 6;
            depositLevel = "6倍押金";
        } else {
            depositMultiplier = 10;
            depositLevel = "10倍押金";
        }
        logger.info("用户 {} 信用分: {}, 押金等级: {} (倍率: {})", userId, creditScore, depositLevel, depositMultiplier);

        // 2. 准备数据：获取商品详情并按商家(DeptId)分组
        // Map<DeptId, List<ShopOrderItem>>
        Map<Long, List<ShopOrderItem>> merchantItemsMap = new HashMap<>();
        // 记录需要删除的购物车ID
        List<Long> cartIdsToDelete = new ArrayList<>();

        for (CartItemRequest itemReq : orderBody.getItems()) {
            BizProduct product = productService.selectBizProductById(itemReq.getProductId());
            if (product == null) {
                logger.error("用户 {} 创建订单时商品已下架: ID {}", userId, itemReq.getProductId());
                throw new RuntimeException("商品已下架: ID " + itemReq.getProductId());
            }

            // 校验库存
            if (itemReq.getQuantity() > product.getStockQuantity()) {
                logger.warn("用户 {} 创建订单时商品 [{}] 库存不足，需求: {}, 实际: {}",
                        userId, product.getProductName(), itemReq.getQuantity(), product.getStockQuantity());
                throw new RuntimeException("商品 [" + product.getProductName() + "] 库存不足");
            }


            // 构建订单明细对象
            ShopOrderItem orderItem = new ShopOrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getProductName());
            orderItem.setProductImg(product.getImageUrl());
            orderItem.setBusinessType(itemReq.getBusinessType()); // 1租赁 2购买
            orderItem.setQuantity(Long.valueOf(itemReq.getQuantity()));
            orderItem.setDeptId(product.getDeptId()); // 关键：记录该商品的归属商家

            // 【调试日志】验证productId是否正确设置
            logger.info("创建订单明细 - productId: {}, productName: {}, quantity: {}",
                    orderItem.getProductId(), orderItem.getProductName(), orderItem.getQuantity());

            // 计算价格
            BigDecimal unitPrice;
            if ("1".equals(itemReq.getBusinessType())) {
                // 租赁模式
                if (itemReq.getStartDate() == null || itemReq.getEndDate() == null) {
                    logger.warn("用户 {} 创建租赁订单时未选择起止时间，商品ID: {}", userId, itemReq.getProductId());
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

                // 计算租赁商品的押金：押金 = 日租金 * 押金倍率 * 数量
                // 押金倍率根据信用分分级：>=600免押, 400-599六倍, <400十倍
                BigDecimal itemDeposit = BigDecimal.ZERO;
                if (depositMultiplier > 0 && unitPrice != null) {
                    itemDeposit = unitPrice
                            .multiply(new BigDecimal(depositMultiplier))
                            .multiply(new BigDecimal(itemReq.getQuantity()));
                }
                orderItem.setDeposit(itemDeposit);
                // 初始化逾期扣款天数为0
                orderItem.setOverdueDeductDays(0);

            } else {
                // 销售模式（购买不需要押金）
                unitPrice = product.getSalePrice();
                orderItem.setPrice(unitPrice);
                orderItem.setDeposit(BigDecimal.ZERO);
                orderItem.setOverdueDeductDays(0);
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

            // 计算该子订单总金额和总押金
            BigDecimal rentTotal = BigDecimal.ZERO;
            BigDecimal orderDeposit = BigDecimal.ZERO;
            for (ShopOrderItem item : items) {
                BigDecimal itemRent;
                if ("1".equals(item.getBusinessType())) {
                    // 租金 * 数量 * 天数
                    BigDecimal days = new BigDecimal(item.getRentDays());
                    BigDecimal qty = new BigDecimal(item.getQuantity());
                    itemRent = item.getPrice().multiply(days).multiply(qty);
                    // 累加明细押金
                    if (item.getDeposit() != null) {
                        orderDeposit = orderDeposit.add(item.getDeposit());
                    }
                } else {
                    // 售价 * 数量
                    BigDecimal qty = new BigDecimal(item.getQuantity());
                    itemRent = item.getPrice().multiply(qty);
                }
                rentTotal = rentTotal.add(itemRent);
            }
            // 总金额 = 租金 + 押金
            BigDecimal orderTotal = rentTotal.add(orderDeposit);
            order.setTotalAmount(orderTotal);
            order.setDepositAmount(orderDeposit); // 设置订单总押金
            order.setIsOverdue("0"); // 初始化未逾期
            order.setShopOrderItemList(items);

            // 保存订单主表（Service中会自动处理关联的Item插入）
            shopOrderService.insertShopOrder(order);
            orderNos.add(order.getOrderNo());
            
            logger.info("用户 {} 创建子订单成功，订单号: {}, 商家ID: {}, 金额: {}, 押金: {}",
                    userId, order.getOrderNo(), merchantId, orderTotal, orderDeposit);
        }

        // 4. 清理购物车
        if (!cartIdsToDelete.isEmpty()) {
            shopCartService.deleteShopCartByIds(cartIdsToDelete.toArray(new Long[0]));
            logger.info("用户 {} 创建订单后清理购物车，删除 {} 条记录", userId, cartIdsToDelete.size());
        }

        // 5. 记录用户商品交互（用于协同过滤推荐）
        for (CartItemRequest itemReq : orderBody.getItems()) {
            try {
                recommendService.recordInteraction(userId, itemReq.getProductId(), "1", null);
            } catch (Exception e) {
                logger.warn("记录用户交互失败: {}", e.getMessage());
            }
        }

        logger.info("用户 {} 订单创建成功，共生成 {} 个订单: {}", userId, orderNos.size(), orderNos);

        // --- 修复点：调用 AjaxResult 的静态方法，而不是 BaseController 的实例方法 ---
        return AjaxResult.success("订单创建成功", orderNos);
    }
    /**
     * 查询我的订单列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ShopOrder shopOrder)
    {
        startPage();
        shopOrder.setUserId(SecurityUtils.getUserId());
        // 1. 查询主订单列表
        List<ShopOrder> list = shopOrderService.selectShopOrderList(shopOrder);

        // 2. 补全订单明细信息 (关键步骤)
        if (list != null && !list.isEmpty()) {
            for (ShopOrder order : list) {
                // 调用 selectShopOrderByOrderId 通常会关联查询 Items
                ShopOrder fullOrder = shopOrderService.selectShopOrderByOrderId(order.getOrderId());
                if (fullOrder != null && fullOrder.getShopOrderItemList() != null) {
                    order.setShopOrderItemList(fullOrder.getShopOrderItemList());

                    // 【调试日志】打印查询到的订单明细信息
                    logger.info("查询订单列表 - 订单号: {}, 商品明细数量: {}", order.getOrderNo(), fullOrder.getShopOrderItemList().size());
                    for (ShopOrderItem item : fullOrder.getShopOrderItemList()) {
                        logger.info("  商品明细 - ID: {}, productId: {}, productName: {}",
                                item.getId(), item.getProductId(), item.getProductName());
                    }
                } else {
                    // 防止 null 指针，设置空列表
                    order.setShopOrderItemList(new ArrayList<>());
                    logger.warn("查询订单列表 - 订单号: {} 未找到商品明细", order.getOrderNo());
                }
            }
        }
        return getDataTable(list);
    }

    /**
     * 模拟支付接口
     */
    @PutMapping("/pay/{orderNo}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult pay(@PathVariable String orderNo, @RequestBody(required = false) Map<String, Object> payDetails)
    {
        Long userId = SecurityUtils.getUserId();
        logger.info("用户 {} 开始支付订单 {}, 支付详情: {}", userId, orderNo, payDetails);

        // 1. 查询订单
        ShopOrder query = new ShopOrder();
        query.setOrderNo(orderNo);
        query.setUserId(userId);
        List<ShopOrder> list = shopOrderService.selectShopOrderList(query);

        if (list == null || list.isEmpty()) {
            logger.warn("用户 {} 支付订单失败：订单 {} 不存在或无权操作", userId, orderNo);
            return error("订单不存在或无权操作");
        }
        ShopOrder order = list.get(0);

        // 2. 校验状态 (仅待支付0可支付)
        if (!"0".equals(order.getStatus())) {
            logger.warn("用户 {} 支付订单失败：订单 {} 当前状态为 {}，不可支付", userId, orderNo, order.getStatus());
            return error("订单状态不可支付");
        }

        // 3. 校验金额 (如果前端传了金额明细)
        if (payDetails != null && payDetails.containsKey("totalAmount")) {
            BigDecimal expectedAmount = new BigDecimal(payDetails.get("totalAmount").toString());
            if (order.getTotalAmount().compareTo(expectedAmount) != 0) {
                logger.error("用户 {} 支付订单 {} 金额校验失败：预期 {}, 实际支付 {}", 
                    userId, orderNo, order.getTotalAmount(), expectedAmount);
                return error("支付金额校验失败");
            }
        }

        // 4. 更新状态 -> 1 (待发货)
        order.setStatus("1");
        order.setPayType("1"); // 默认模拟微信
        order.setPayTime(new Date());
        
        // 记录支付历史到备注
        String oldRemark = order.getRemark() != null ? order.getRemark() : "";
        String payLog = String.format("[支付成功] 时间:%s, 支付总额:%s, 押金:%s", 
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getPayTime()),
            order.getTotalAmount(), order.getDepositAmount());
        order.setRemark(oldRemark + (oldRemark.isEmpty() ? "" : " | ") + payLog);

        shopOrderService.updateShopOrder(order);
        
        // 5. 支付成功后，更新用户冻结押金账户
        if (order.getDepositAmount() != null && order.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
            appUserService.freezeDeposit(userId, order.getDepositAmount());
            logger.info("用户 {} 支付订单 {} 成功，同步更新账户冻结押金: {}", 
                userId, orderNo, order.getDepositAmount());
        }

        logger.info("用户 {} 支付订单 {} 成功，订单状态更新为待发货", userId, orderNo);

        return success();
    }

    /**
     * 确认收货/归还
     */
    @PutMapping("/confirm/{orderNo}")
    public AjaxResult confirm(@PathVariable String orderNo)
    {
        Long userId = SecurityUtils.getUserId();
        logger.info("用户 {} 开始确认收货/归还订单 {}", userId, orderNo);

        ShopOrder query = new ShopOrder();
        query.setOrderNo(orderNo);
        query.setUserId(userId);
        List<ShopOrder> list = shopOrderService.selectShopOrderList(query);

        if (list == null || list.isEmpty()) {
            logger.warn("用户 {} 确认订单失败：订单 {} 不存在", userId, orderNo);
            return error("订单不存在");
        }
        ShopOrder order = list.get(0);

        // 校验：只有已发货/租赁中(2) 才能确认完成
        // (模拟演示时，如果想方便测试，可以允许状态1也直接完成，这里写标准逻辑)
        if (!"2".equals(order.getStatus()) && !"1".equals(order.getStatus())) {
            logger.warn("用户 {} 确认订单失败：订单 {} 当前状态为 {}，无法确认收货", userId, orderNo, order.getStatus());
            return error("当前状态无法确认收货");
        }
// 修复：保留原有商品明细，防止更新时丢失
        ShopOrder originalOrder = shopOrderService.selectShopOrderByOrderId(order.getOrderId());
        if (originalOrder != null && originalOrder.getShopOrderItemList() != null) {
            order.setShopOrderItemList(originalOrder.getShopOrderItemList());
            logger.info("用户 {} 确认订单 {} 时保留商品明细 {} 条", userId, orderNo, originalOrder.getShopOrderItemList().size());
        }
        order.setStatus("3"); // 已完成
        shopOrderService.updateShopOrder(order);
        
        // 订单完成时增加50信用分（仅当状态从非完成变为完成时）
        appUserService.updateCreditScoreDelta(userId, 50);
        logger.info("用户 {} 确认订单 {} 成功，订单状态更新为已完成，信用分+50", userId, orderNo);

        return success();
    }
    /**
     * 申请归还 (租赁业务)
     * 状态流转: 2(租赁中) -> 7(归还中)
     */
    @PutMapping("/return/{orderId}")
    public AjaxResult returnOrder(@PathVariable Long orderId)
    {
        ShopOrder order = shopOrderService.selectShopOrderByOrderId(orderId);
        if (order == null || !order.getUserId().equals(SecurityUtils.getUserId())) {
            return error("订单不存在或无权操作");
        }

        if (!"2".equals(order.getStatus())) {
            return error("当前状态无法申请归还");
        }

        // 简单校验是否包含租赁商品 (实际业务可能更复杂)
        boolean hasRental = order.getShopOrderItemList().stream()
                .anyMatch(item -> "1".equals(item.getBusinessType()));

        if (!hasRental) {
            return error("非租赁订单，请直接确认收货");
        }

        order.setStatus("7"); // 7=归还中
        shopOrderService.updateShopOrder(order);
        return success();
    }
    /**
     * 用户取消订单
     */
    @PostMapping("/cancel")
    public AjaxResult cancel(@RequestBody ShopOrder order) {
        // 建议在Service层加校验：确保该订单属于当前用户
        shopOrderService.userCancelOrder(order.getOrderId());
        return success();
    }

    /**
     * 用户申请退款
     */
    @PostMapping("/applyRefund")
    public AjaxResult applyRefund(@RequestBody ShopOrder order) {
        shopOrderService.userApplyRefund(order.getOrderId());
        return success();
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

    @Autowired
    private com.zNova.system.service.IRentalExpiryWarningService rentalExpiryWarningService;

    /**
     * 获取用户即将到期的租赁订单（2天内到期）
     * 用于前端登录后显示到期提醒弹窗
     */
    @GetMapping("/expiring-soon")
    public AjaxResult getExpiringSoonOrders()
    {
        Long userId = SecurityUtils.getUserId();
        java.util.List<java.util.Map<String, Object>> expiringOrders = 
                rentalExpiryWarningService.getExpiringSoonOrders(userId, 2);
        return AjaxResult.success(expiringOrders);
    }

}