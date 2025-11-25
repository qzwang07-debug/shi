package com.zNova.system.service;


import java.util.List;
import com.zNova.system.domain.ShopCart;

/**
 * 购物车Service接口
 *
 * @author zNova
 * @date 2024-05-20
 */
public interface IShopCartService
{
    /**
     * 查询我的购物车列表
     *
     * @param userId 用户ID
     * @return 购物车集合
     */
    public List<ShopCart> selectCartList(Long userId);

    /**
     * 添加商品到购物车
     *
     * @param shopCart 购物车信息
     * @return 结果
     */
    public int addToCart(ShopCart shopCart);

    /**
     * 修改购物车 (修改数量或勾选状态)
     *
     * @param shopCart 购物车信息
     * @return 结果
     */
    public int updateShopCart(ShopCart shopCart);

    /**
     * 批量删除购物车信息
     *
     * @param cartIds 需要删除的购物车ID
     * @return 结果
     */
    public int deleteShopCartByIds(Long[] cartIds);
}