package com.zNova.system.mapper;



import java.util.List;
import com.zNova.system.domain.ShopCart;
import org.apache.ibatis.annotations.Param;

/**
 * 购物车Mapper接口
 */
public interface ShopCartMapper
{
    /**
     * 查询购物车列表
     */
    List<ShopCart> selectCartList(Long userId);

    /**
     * 查询单条购物车记录（用于检查是否存在）
     */
    ShopCart selectCartByProduct(@Param("userId") Long userId,
                                 @Param("productId") Long productId,
                                 @Param("businessType") String businessType);

    /**
     * 新增购物车
     */
    int insertShopCart(ShopCart shopCart);

    /**
     * 修改购物车
     */
    int updateShopCart(ShopCart shopCart);

    /**
     * 批量删除购物车
     */
    int deleteShopCartByIds(Long[] cartIds);
}