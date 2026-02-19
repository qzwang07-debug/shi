package com.zNova.system.mapper;

import java.util.List;
import com.zNova.system.domain.BizProduct;
import org.apache.ibatis.annotations.Param;

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
    public List<BizProduct>selectBizProductListWithDeptName (BizProduct bizProduct);

    /**
     * 根据性能分数查询最接近的商品
     *
     * @param targetScore 目标性能分数
     * @return 商品集合
     */
    public List<BizProduct> selectProductListByScoreNear(@Param("targetScore") Integer targetScore);
// selectBizProductListWithMerchantName
    /**
     * 扣减库存（原子操作，防止超卖）
     * @param id 商品ID
     * @param quantity 扣减数量
     * @return 更新行数
     */
    public int decreaseStock(@Param("id") Long id, @Param("quantity") Long quantity);

    /**
     * 增加库存（回滚库存）
     * @param id 商品ID
     * @param quantity 增加数量
     * @return 更新行数
     */
    public int increaseStock(@Param("id") Long id, @Param("quantity") Long quantity);

    /**
     * 查询低库存商品（库存<=预警阈值）
     * @param deptId 商家ID（可选，传null查询所有）
     * @return 低库存商品列表
     */
    public List<BizProduct> selectLowStockProducts(@Param("deptId") Long deptId);
}
