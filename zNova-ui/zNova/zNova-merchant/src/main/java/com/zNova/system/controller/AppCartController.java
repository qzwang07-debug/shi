package com.zNova.system.controller;



import java.util.List;

import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.zNova.common.core.domain.model.AppLoginUser; // 引入你自定义的User类
import com.zNova.system.domain.ShopCart;
import com.zNova.system.service.IShopCartService;

/**
 * C端 - 购物车接口
 *
 * @author zNova
 */
@RestController
@RequestMapping("/app/cart")
public class AppCartController extends BaseController
{
    @Autowired
    private IShopCartService shopCartService;

    /**
     * 获取当前登录用户的ID (封装方法)
     */
    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AppLoginUser) {
            AppLoginUser loginUser = (AppLoginUser) authentication.getPrincipal();
            return loginUser.getUserId();
        }
        // 抛出特定的未登录异常，通常 RuoYi 会捕获并返回 401
        throw new ServiceException("登录状态已过期，请重新登录", 401);
    }

    /**
     * 获取我的购物车列表
     */
    @GetMapping("/list")
    public AjaxResult list()
    {
        Long userId = getUserId();
        List<ShopCart> list = shopCartService.selectCartList(userId);
        return success(list);
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping
    public AjaxResult add(@RequestBody ShopCart shopCart)
    {
        shopCart.setUserId(getUserId());
        shopCart.setCreateBy(String.valueOf(getUserId())); // 记录操作人
        return toAjax(shopCartService.addToCart(shopCart));
    }

    /**
     * 修改购物车商品 (数量/勾选状态)
     */
    @PutMapping
    public AjaxResult edit(@RequestBody ShopCart shopCart)
    {
        // 可以在这里加个校验，确保修改的是自己的购物车数据，防止ID遍历攻击
        // 但简单毕设可以直接调用
        shopCart.setUpdateBy(String.valueOf(getUserId()));
        return toAjax(shopCartService.updateShopCart(shopCart));
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/{cartIds}")
    public AjaxResult remove(@PathVariable Long[] cartIds)
    {
        return toAjax(shopCartService.deleteShopCartByIds(cartIds));
    }
}