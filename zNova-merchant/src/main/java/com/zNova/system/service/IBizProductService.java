package com.zNova.system.service;

import java.util.List;
import com.zNova.system.domain.BizProduct;

/**
 * 商品Service接口
 *
 * @author wyz
 * @date 2025-11-24
 */
public interface IBizProductService
{
    /**
     * 查询商品
     *
     * @param id 商品主键
     * @return 商品
     */
    public BizProduct selectBizProductById(Long id);

    /**
     * 查询商品列表
     *
     * @param bizProduct 商品
     * @return 商品集合
     */
    public List<BizProduct> selectBizProductList(BizProduct bizProduct);

    /**
     * 新增商品
     *
     * @param bizProduct 商品
     * @return 结果
     */
    public int insertBizProduct(BizProduct bizProduct);

    /**
     * 修改商品
     *
     * @param bizProduct 商品
     * @return 结果
     */
    public int updateBizProduct(BizProduct bizProduct);

    /**
     * 批量删除商品
     *
     * @param ids 需要删除的商品主键集合
     * @return 结果
     */
    public int deleteBizProductByIds(Long[] ids);

    /**
     * 删除商品信息
     *
     * @param id 商品主键
     * @return 结果
     */
    public int deleteBizProductById(Long id);

    /**
     * 前台查询商品列表（不应用数据权限过滤）
     *
     * @param bizProduct 商品
     * @return 商品集合
     */
    public List<BizProduct> selectFrontProductList(BizProduct bizProduct);

    /**
     * 根据性能分数查询最接近的商品
     *
     * @param targetScore 目标性能分数
     * @return 商品集合
     */
    public List<BizProduct> selectProductListByScoreNear(Integer targetScore);
}
