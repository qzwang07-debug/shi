package com.zNova.system.service.impl;



import java.util.List;
import com.zNova.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zNova.system.mapper.ShopCartMapper;
import com.zNova.system.domain.ShopCart;
import com.zNova.system.service.IShopCartService;

/**
 * 购物车Service业务层处理
 */
@Service
public class ShopCartServiceImpl implements IShopCartService
{
    @Autowired
    private ShopCartMapper shopCartMapper;

    /**
     * 查询我的购物车
     */
    @Override
    public List<ShopCart> selectCartList(Long userId)
    {
        return shopCartMapper.selectCartList(userId);
    }

    /**
     * 添加购物车
     * 核心逻辑：如果已存在同类型同商品，则叠加数量；否则新增
     */
    @Override
    public int addToCart(ShopCart shopCart)
    {
        // 1. 查询该用户是否已添加过该商品（且业务类型一致）
        ShopCart existingCart = shopCartMapper.selectCartByProduct(
                shopCart.getUserId(),
                shopCart.getProductId(),
                shopCart.getBusinessType()
        );

        if (existingCart != null) {
            // 2. 如果存在，更新数量
            existingCart.setQuantity(existingCart.getQuantity() + shopCart.getQuantity());
            existingCart.setUpdateBy(shopCart.getCreateBy()); // 用创建者作为更新者
            existingCart.setUpdateTime(DateUtils.getNowDate());
            return shopCartMapper.updateShopCart(existingCart);
        } else {
            // 3. 如果不存在，插入新记录
            shopCart.setCreateTime(DateUtils.getNowDate());
            // 默认选中
            shopCart.setIsChecked("1");
            return shopCartMapper.insertShopCart(shopCart);
        }
    }

    /**
     * 修改购物车 (修改数量或勾选状态)
     */
    @Override
    public int updateShopCart(ShopCart shopCart)
    {
        shopCart.setUpdateTime(DateUtils.getNowDate());
        return shopCartMapper.updateShopCart(shopCart);
    }

    /**
     * 批量删除购物车
     */
    @Override
    public int deleteShopCartByIds(Long[] cartIds)
    {
        return shopCartMapper.deleteShopCartByIds(cartIds);
    }
}