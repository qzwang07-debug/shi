package com.zNova.system.service;

import java.util.List;
import com.zNova.system.domain.ShopComment;

/**
 * 商品评价Service接口
 * 
 * @author wyz
 * @date 2025-11-21
 */
public interface IShopCommentService 
{
    /**
     * 查询商品评价
     * 
     * @param commentId 商品评价主键
     * @return 商品评价
     */
    public ShopComment selectShopCommentByCommentId(Long commentId);

    /**
     * 查询商品评价列表
     * 
     * @param shopComment 商品评价
     * @return 商品评价集合
     */
    public List<ShopComment> selectShopCommentList(ShopComment shopComment);

    /**
     * 新增商品评价
     * 
     * @param shopComment 商品评价
     * @return 结果
     */
    public int insertShopComment(ShopComment shopComment);

    /**
     * 修改商品评价
     * 
     * @param shopComment 商品评价
     * @return 结果
     */
    public int updateShopComment(ShopComment shopComment);

    /**
     * 批量删除商品评价
     * 
     * @param commentIds 需要删除的商品评价主键集合
     * @return 结果
     */
    public int deleteShopCommentByCommentIds(Long[] commentIds);

    /**
     * 删除商品评价信息
     * 
     * @param commentId 商品评价主键
     * @return 结果
     */
    public int deleteShopCommentByCommentId(Long commentId);
}
