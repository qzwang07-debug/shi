package com.zNova.system.service;

import java.util.List;
import com.zNova.system.domain.BizComputerGoods;
import com.zNova.system.domain.vo.PerformanceScoreVO;

/**
 * 电脑商品Service接口
 *
 * @author wyz
 * @date 2025-11-09
 */
public interface IBizComputerGoodsService
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
     * 批量删除电脑商品
     *
     * @param ids 需要删除的电脑商品主键集合
     * @return 结果
     */
    public int deleteBizComputerGoodsByIds(Long[] ids);

    /**
     * 删除电脑商品信息
     *
     * @param id 电脑商品主键
     * @return 结果
     */
    public int deleteBizComputerGoodsById(Long id);

    /**
     * 获取商品性能数据
     */
    BizComputerGoods getPerformanceById(Long id);

    /**
     * 计算商品性能评分
     */
    PerformanceScoreVO calculateScore(BizComputerGoods goods);
}