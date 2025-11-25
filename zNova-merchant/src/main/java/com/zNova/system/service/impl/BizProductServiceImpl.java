package com.zNova.system.service.impl;

import java.util.List;

import com.zNova.common.annotation.DataScope;
import com.zNova.common.utils.DateUtils;
import com.zNova.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zNova.system.mapper.BizProductMapper;
import com.zNova.system.domain.BizProduct;
import com.zNova.system.service.IBizProductService;
import com.zNova.common.core.domain.entity.SysUser;

/**
 * 商品Service业务层处理
 *
 * @author wyz
 * @date 2025-11-24
 */
@Service
public class BizProductServiceImpl implements IBizProductService
{
    @Autowired
    private BizProductMapper bizProductMapper;

    /**
     * 查询商品
     *
     * @param id 商品主键
     * @return 商品
     */
    @Override
    public BizProduct selectBizProductById(Long id)
    {
        return bizProductMapper.selectBizProductById(id);
    }

    /**
     * 查询商品列表
     *
     * @param bizProduct 商品
     * @return 商品
     */
    @Override
    // 2. 添加注解
    // deptAlias = "p" 对应 mapper.xml 中 biz_product 表的别名
    @DataScope(deptAlias = "p")
    public List<BizProduct> selectBizProductList(BizProduct bizProduct)
    {
        return bizProductMapper.selectBizProductList(bizProduct);
    }

    /**
     * 新增商品
     *
     * @param bizProduct 商品
     * @return 结果
     */
    @Override
    public int insertBizProduct(BizProduct bizProduct)
    {
        // 自动设置创建人和创建时间
        bizProduct.setCreateBy(SecurityUtils.getUsername());
        bizProduct.setCreateTime(DateUtils.getNowDate());

        // 获取当前登录用户
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();

        // 核心修复逻辑：
        // 1. 判断是否为管理员（ID为1通常是超级管理员，或者根据你的业务角色判断）
        boolean isAdmin = SysUser.isAdmin(currentUser.getUserId());

        if (isAdmin) {
            // === 管理员逻辑 ===
            // 管理员在前端选择了部门，提交了 deptId，我们予以保留。
            // 如果管理员没选（deptId为空），为了防止报错，兜底设为管理员自己的部门
            if (bizProduct.getDeptId() == null) {
                bizProduct.setDeptId(currentUser.getDeptId());
            }
        } else {
            // === 商家/普通用户逻辑 ===
            // 强制设置为当前用户的部门ID，无视前端传来的任何值（安全保障）
            if (currentUser != null && currentUser.getDeptId() != null) {
                bizProduct.setDeptId(currentUser.getDeptId());
            }
        }

        return bizProductMapper.insertBizProduct(bizProduct);
    }

    /**
     * 修改商品
     *
     * @param bizProduct 商品
     * @return 结果
     */
    @Override
    public int updateBizProduct(BizProduct bizProduct)
    {
        bizProduct.setUpdateBy(SecurityUtils.getUsername());
        bizProduct.setUpdateTime(DateUtils.getNowDate());

        // 注意：这里不要写任何 setDeptId 的逻辑
        // 部门的修改完全依赖前端传参（管理员传，商家不传）

        return bizProductMapper.updateBizProduct(bizProduct);
    }

    /**
     * 批量删除商品
     *
     * @param ids 需要删除的商品主键
     * @return 结果
     */
    @Override
    public int deleteBizProductByIds(Long[] ids)
    {
        return bizProductMapper.deleteBizProductByIds(ids);
    }

    /**
     * 删除商品信息
     *
     * @param id 商品主键
     * @return 结果
     */
    @Override
    public int deleteBizProductById(Long id)
    {
        return bizProductMapper.deleteBizProductById(id);
    }

    /**
     * 前台查询商品列表（不应用数据权限过滤）
     *
     * @param bizProduct 商品
     * @return 商品集合
     */
    @Override
    public List<BizProduct> selectFrontProductList(BizProduct bizProduct)
    {
        return bizProductMapper.selectBizProductList(bizProduct);
    }

}
