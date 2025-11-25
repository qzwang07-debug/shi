package com.zNova.system.service.impl;

import java.util.List;
import com.zNova.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zNova.system.mapper.AppAddressMapper;
import com.zNova.system.domain.AppAddress;
import com.zNova.system.service.IAppAddressService;

/**
 * 收货地址Service业务层处理
 * 
 * @author wyz
 * @date 2025-11-24
 */
@Service
public class AppAddressServiceImpl implements IAppAddressService 
{
    @Autowired
    private AppAddressMapper appAddressMapper;

    /**
     * 查询收货地址
     * 
     * @param addressId 收货地址主键
     * @return 收货地址
     */
    @Override
    public AppAddress selectAppAddressByAddressId(Long addressId)
    {
        return appAddressMapper.selectAppAddressByAddressId(addressId);
    }

    /**
     * 查询收货地址列表
     * 
     * @param appAddress 收货地址
     * @return 收货地址
     */
    @Override
    public List<AppAddress> selectAppAddressList(AppAddress appAddress)
    {
        return appAddressMapper.selectAppAddressList(appAddress);
    }

    /**
     * 新增收货地址
     * 
     * @param appAddress 收货地址
     * @return 结果
     */
    @Override
    public int insertAppAddress(AppAddress appAddress)
    {
        appAddress.setCreateTime(DateUtils.getNowDate());
        return appAddressMapper.insertAppAddress(appAddress);
    }

    /**
     * 修改收货地址
     * 
     * @param appAddress 收货地址
     * @return 结果
     */
    @Override
    public int updateAppAddress(AppAddress appAddress)
    {
        return appAddressMapper.updateAppAddress(appAddress);
    }

    /**
     * 批量删除收货地址
     * 
     * @param addressIds 需要删除的收货地址主键
     * @return 结果
     */
    @Override
    public int deleteAppAddressByAddressIds(Long[] addressIds)
    {
        return appAddressMapper.deleteAppAddressByAddressIds(addressIds);
    }

    /**
     * 删除收货地址信息
     * 
     * @param addressId 收货地址主键
     * @return 结果
     */
    @Override
    public int deleteAppAddressByAddressId(Long addressId)
    {
        return appAddressMapper.deleteAppAddressByAddressId(addressId);
    }
}
