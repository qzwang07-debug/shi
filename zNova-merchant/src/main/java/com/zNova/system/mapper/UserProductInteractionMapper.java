package com.zNova.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.zNova.system.domain.UserProductInteraction;

/**
 * 用户商品交互Mapper接口
 * 
 * @author zNova
 * @date 2025-01-21
 */
public interface UserProductInteractionMapper 
{
    /**
     * 查询用户商品交互
     * 
     * @param id 主键ID
     * @return 用户商品交互
     */
    public UserProductInteraction selectUserProductInteractionById(Long id);

    /**
     * 查询用户商品交互列表
     * 
     * @param userProductInteraction 用户商品交互
     * @return 用户商品交互集合
     */
    public List<UserProductInteraction> selectUserProductInteractionList(UserProductInteraction userProductInteraction);

    /**
     * 新增用户商品交互
     * 
     * @param userProductInteraction 用户商品交互
     * @return 结果
     */
    public int insertUserProductInteraction(UserProductInteraction userProductInteraction);

    /**
     * 批量新增用户商品交互
     * 
     * @param list 用户商品交互列表
     * @return 结果
     */
    public int batchInsertUserProductInteraction(List<UserProductInteraction> list);

    /**
     * 查询用户交互过的商品ID列表
     * 
     * @param userId 用户ID
     * @return 商品ID列表
     */
    public List<Long> selectUserInteractedProductIds(@Param("userId") Long userId);

    /**
     * 查询商品共现次数(两个商品被同一用户交互的次数)
     * 
     * @return 商品对共现次数列表
     */
    public List<Map<String, Object>> selectProductCoOccurrence();

    /**
     * 查询每个商品被多少用户交互过
     * 
     * @return 商品交互用户数列表
     */
    public List<Map<String, Object>> selectProductUserCount();

    /**
     * 查询热门商品(按交互次数排序)
     * 
     * @param limit 限制数量
     * @return 热门商品ID列表
     */
    public List<Long> selectHotProductIds(@Param("limit") int limit);

    /**
     * 删除用户商品交互
     * 
     * @param id 主键ID
     * @return 结果
     */
    public int deleteUserProductInteractionById(Long id);
}
