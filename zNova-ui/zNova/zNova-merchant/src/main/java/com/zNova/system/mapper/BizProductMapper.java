package com.zNova.system.mapper;

import java.util.List;
import com.zNova.system.domain.BizProduct;

/**
 * 商品Mapper接口
 *
 * @author wyz
 * @date 2025-11-09
 */
public interface BizProductMapper
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
     * 删除商品
     *
     * @param id 商品主键
     * @return 结果
     */
    public int deleteBizProductById(Long id);

    /**
     * 批量删除商品
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizProductByIds(Long[] ids);

    /**
     * 查询商品列表（关联商家名称）
     *
     * @param bizProduct 商品
     * @return 商品集合
     */
    public List<BizProduct> selectBizProductListWithMerchantName(BizProduct bizProduct);
}
