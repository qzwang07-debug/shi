package com.zNova.system.mapper;

import java.util.List;
import com.zNova.system.domain.AppAddress;

/**
 * 收货地址Mapper接口
 * 
 * @author wyz
 * @date 2025-11-24
 */
public interface AppAddressMapper 
{
    /**
     * 查询收货地址
     * 
     * @param addressId 收货地址主键
     * @return 收货地址
     */
    public AppAddress selectAppAddressByAddressId(Long addressId);

    /**
     * 查询收货地址列表
     * 
     * @param appAddress 收货地址
     * @return 收货地址集合
     */
    public List<AppAddress> selectAppAddressList(AppAddress appAddress);

    /**
     * 新增收货地址
     * 
     * @param appAddress 收货地址
     * @return 结果
     */
    public int insertAppAddress(AppAddress appAddress);

    /**
     * 修改收货地址
     * 
     * @param appAddress 收货地址
     * @return 结果
     */
    public int updateAppAddress(AppAddress appAddress);

    /**
     * 删除收货地址
     * 
     * @param addressId 收货地址主键
     * @return 结果
     */
    public int deleteAppAddressByAddressId(Long addressId);

    /**
     * 批量删除收货地址
     * 
     * @param addressIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAppAddressByAddressIds(Long[] addressIds);
}
