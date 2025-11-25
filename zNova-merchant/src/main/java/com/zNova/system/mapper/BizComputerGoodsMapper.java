package com.zNova.system.mapper;

import java.util.List;
import com.zNova.system.domain.BizComputerGoods;

/**
 * 电脑商品Mapper接口
 *
 * @author wyz
 * @date 2025-11-09
 */
public interface BizComputerGoodsMapper
{
    /**
     * 查询电脑商品
     *
     * @param id 电脑商品主键
     * @return 电脑商品
     */
    public BizComputerGoods selectBizComputerGoodsById(Long id);

    /**
     * 查询电脑商品列表
     *
     * @param bizComputerGoods 电脑商品
     * @return 电脑商品集合
     */
    public List<BizComputerGoods> selectBizComputerGoodsList(BizComputerGoods bizComputerGoods);

    /**
     * 新增电脑商品
     *
     * @param bizComputerGoods 电脑商品
     * @return 结果
     */
    public int insertBizComputerGoods(BizComputerGoods bizComputerGoods);

    /**
     * 修改电脑商品
     *
     * @param bizComputerGoods 电脑商品
     * @return 结果
     */
    public int updateBizComputerGoods(BizComputerGoods bizComputerGoods);

    /**
     * 删除电脑商品
     *
     * @param id 电脑商品主键
     * @return 结果
     */
    public int deleteBizComputerGoodsById(Long id);

    /**
     * 批量删除电脑商品
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizComputerGoodsByIds(Long[] ids);

    /**
     * 根据商品ID查询性能相关数据
     */
    BizComputerGoods selectPerformanceById(Long id);
}