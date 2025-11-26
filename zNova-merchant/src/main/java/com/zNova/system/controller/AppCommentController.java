package com.zNova.system.controller;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zNova.common.annotation.Anonymous;
import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.common.core.page.TableDataInfo;
import com.zNova.common.utils.SecurityUtils;
import com.zNova.common.utils.StringUtils;
import com.zNova.system.domain.ShopComment;
import com.zNova.system.domain.ShopOrder;
import com.zNova.system.domain.ShopOrderItem;
import com.zNova.system.mapper.ShopOrderMapper;
import com.zNova.system.service.IShopCommentService;

/**
 * C端商品评价接口
 * * @author zNova
 */
@RestController
@RequestMapping("/app/comment")
public class AppCommentController extends BaseController
{
    @Autowired
    private IShopCommentService shopCommentService;

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    /**
     * 获取商品评论列表
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(ShopComment shopComment)
    {
        if (shopComment.getProductId() == null) {
            return new TableDataInfo();
        }
        startPage();
        List<ShopComment> list = shopCommentService.selectShopCommentList(shopComment);
        return getDataTable(list);
    }

    /**
     * 发表评论
     */
    @PostMapping("/add")
    public AjaxResult add(@RequestBody ShopComment comment)
    {
        // 1. 获取当前登录用户 ID
        Long userId = SecurityUtils.getUserId();

        // 【调试日志】打印接收到的评论数据
        logger.info("【评论接口】接收到评论请求 - 用户ID: {}, 订单ID: {}, 商品ID: {}, 评分: {}, 内容: {}",
                userId, comment.getOrderId(), comment.getProductId(), comment.getStar(), comment.getContent());

        // 2. 参数基础校验
        if (comment.getProductId() == null || comment.getOrderId() == null) {
            logger.error("【评论接口】参数校验失败 - 商品ID: {}, 订单ID: {}",
                    comment.getProductId(), comment.getOrderId());
            return error("参数缺失：商品ID或订单ID不能为空");
        }
        if (StringUtils.isEmpty(comment.getContent())) {
            return error("请输入评价内容");
        }
        if (comment.getStar() == null) {
            comment.setStar(5); // 默认5星
        }

        // 3. 核心业务校验
        ShopOrder order = shopOrderMapper.selectShopOrderByOrderId(comment.getOrderId());

        // 3.1 校验订单是否存在
        if (order == null) {
            return error("订单不存在");
        }

        // 3.2 校验订单归属
        if (!order.getUserId().equals(userId)) {
            return error("无权评价非本人的订单");
        }

        // 3.3 校验订单状态（必须是 "3" 已完成状态）
        if (!"3".equals(order.getStatus())) {
            return error("订单尚未完成，无法进行评价");
        }

        // 3.4 校验该订单是否真的包含该商品
        // 修改点：如果 productId 为 0 (整单评价) 或者商品ID存在于明细中，则允许通过
        if (comment.getProductId() != 0L) {
            boolean hasProduct = false;
            List<ShopOrderItem> items = order.getShopOrderItemList();

            if (items != null && !items.isEmpty()) {
                for (ShopOrderItem item : items) {
                    if (item.getProductId().equals(comment.getProductId())) {
                        hasProduct = true;
                        break;
                    }
                }
            }

            if (!hasProduct) {
                // 如果是为了测试旧数据（没有明细），这里可以暂时注释掉 return error
                return error("您未在该订单中购买此商品，无法评价");
            }
        }

        // 4. 封装评价数据并保存
        comment.setUserId(userId);
        comment.setCreateTime(new Date());
        comment.setNickname(SecurityUtils.getUsername());

        shopCommentService.insertShopComment(comment);

        return success("评价成功");
    }
}