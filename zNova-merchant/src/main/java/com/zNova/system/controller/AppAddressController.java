package com.zNova.system.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize; // App端通常不需要此注解，视框架配置而定，此处保留或建议移除如果App端不走RBAC
import org.springframework.web.bind.annotation.*;
import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.common.utils.SecurityUtils;
import com.zNova.common.utils.StringUtils;
import com.zNova.system.domain.AppAddress;
import com.zNova.system.service.IAppAddressService;

/**
 * C端收货地址Controller
 * * @author zNova
 */
@RestController
@RequestMapping("/app/address")
public class AppAddressController extends BaseController
{
    @Autowired
    private IAppAddressService appAddressService;

    /**
     * 查询我的收货地址列表
     */
    @GetMapping("/list")
    public AjaxResult list()
    {
        AppAddress query = new AppAddress();
        query.setUserId(SecurityUtils.getUserId());
        List<AppAddress> list = appAddressService.selectAppAddressList(query);
        return success(list);
    }

    /**
     * 新增收货地址
     */
    @PostMapping("/add")
    public AjaxResult add(@RequestBody AppAddress appAddress)
    {
        Long userId = SecurityUtils.getUserId();
        appAddress.setUserId(userId);

        // 如果设置为默认地址，先重置其他地址
        if ("1".equals(appAddress.getIsDefault())) {
            resetDefaultAddress(userId);
        }

        return toAjax(appAddressService.insertAppAddress(appAddress));
    }

    /**
     * 修改收货地址
     */
    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody AppAddress appAddress)
    {
        Long userId = SecurityUtils.getUserId();

        // 安全检查：确保修改的是自己的地址
        AppAddress existing = appAddressService.selectAppAddressByAddressId(appAddress.getAddressId());
        if (existing == null || !existing.getUserId().equals(userId)) {
            return error("无权访问该地址数据");
        }

        appAddress.setUserId(userId);

        // 如果设置为默认地址，先重置其他地址
        if ("1".equals(appAddress.getIsDefault())) {
            resetDefaultAddress(userId);
        }

        return toAjax(appAddressService.updateAppAddress(appAddress));
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("/del/{addressId}")
    public AjaxResult remove(@PathVariable Long addressId)
    {
        Long userId = SecurityUtils.getUserId();

        // 安全检查
        AppAddress existing = appAddressService.selectAppAddressByAddressId(addressId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return error("无权删除该地址");
        }

        return toAjax(appAddressService.deleteAppAddressByAddressId(addressId));
    }

    /**
     * 获取地址详情
     */
    @GetMapping("/{addressId}")
    public AjaxResult getInfo(@PathVariable Long addressId)
    {
        Long userId = SecurityUtils.getUserId();
        AppAddress address = appAddressService.selectAppAddressByAddressId(addressId);
        if (address != null && !address.getUserId().equals(userId)) {
            return error("无权访问该地址");
        }
        return success(address);
    }

    /**
     * 重置该用户的所有地址为非默认
     */
    private void resetDefaultAddress(Long userId) {
        AppAddress query = new AppAddress();
        query.setUserId(userId);
        query.setIsDefault("1");
        List<AppAddress> defaults = appAddressService.selectAppAddressList(query);
        for (AppAddress addr : defaults) {
            addr.setIsDefault("0");
            appAddressService.updateAppAddress(addr);
        }
    }
}