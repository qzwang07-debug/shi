package com.zNova.system.service.impl;

import java.util.List;
import com.zNova.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zNova.system.mapper.ShopCommentMapper;
import com.zNova.system.domain.ShopComment;
import com.zNova.system.service.IShopCommentService;

/**
 * 商品评价Service业务层处理
 * 
 * @author wyz
 * @date 2025-11-21
 */
@Service
public class ShopCommentServiceImpl implements IShopCommentService 
{
    @Autowired
    private ShopCommentMapper shopCommentMapper;

    /**
     * 查询商品评价
     * 
     * @param commentId 商品评价主键
     * @return 商品评价
     */
    @Override
    public ShopComment selectShopCommentByCommentId(Long commentId)
    {
        return shopCommentMapper.selectShopCommentByCommentId(commentId);
    }

    /**
     * 查询商品评价列表
     * 
     * @param shopComment 商品评价
     * @return 商品评价
     */
    @Override
    public List<ShopComment> selectShopCommentList(ShopComment shopComment)
    {
        return shopCommentMapper.selectShopCommentList(shopComment);
    }

    /**
     * 新增商品评价
     * 
     * @param shopComment 商品评价
     * @return 结果
     */
    @Override
    public int insertShopComment(ShopComment shopComment)
    {
        shopComment.setCreateTime(DateUtils.getNowDate());
        return shopCommentMapper.insertShopComment(shopComment);
    }

    /**
     * 修改商品评价
     * 
     * @param shopComment 商品评价
     * @return 结果
     */
    @Override
    public int updateShopComment(ShopComment shopComment)
    {
        return shopCommentMapper.updateShopComment(shopComment);
    }

    /**
     * 批量删除商品评价
     * 
     * @param commentIds 需要删除的商品评价主键
     * @return 结果
     */
    @Override
    public int deleteShopCommentByCommentIds(Long[] commentIds)
    {
        return shopCommentMapper.deleteShopCommentByCommentIds(commentIds);
    }

    /**
     * 删除商品评价信息
     * 
     * @param commentId 商品评价主键
     * @return 结果
     */
    @Override
    public int deleteShopCommentByCommentId(Long commentId)
    {
        return shopCommentMapper.deleteShopCommentByCommentId(commentId);
    }
}
