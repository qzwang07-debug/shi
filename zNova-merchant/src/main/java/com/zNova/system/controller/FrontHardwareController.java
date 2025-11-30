package com.zNova.system.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.page.TableDataInfo;
import com.zNova.system.domain.HardwareCpu;
import com.zNova.system.domain.HardwareGpu;
import com.zNova.system.domain.HardwareMemory;
import com.zNova.system.domain.HardwareMotherboard;
import com.zNova.system.domain.HardwarePowerSupply;
import com.zNova.system.service.IHardwareCpuService;
import com.zNova.system.service.IHardwareGpuService;
import com.zNova.system.service.IHardwareMemoryService;
import com.zNova.system.service.IHardwareMotherboardService;
import com.zNova.system.service.IHardwarePowerSupplyService;

/**
 * 前台硬件Controller
 *
 * @author wyz
 * @date 2025-11-20
 */
@RestController
@RequestMapping("/front/hardware")
public class FrontHardwareController extends BaseController
{
    @Autowired
    private IHardwareCpuService hardwareCpuService;

    @Autowired
    private IHardwareGpuService hardwareGpuService;

    @Autowired
    private IHardwareMemoryService hardwareMemoryService;

    @Autowired
    private IHardwareMotherboardService hardwareMotherboardService;

    @Autowired
    private IHardwarePowerSupplyService hardwarePowerSupplyService;

    /**
     * 查询CPU硬件列表（前台专用）
     */
    @GetMapping("/cpu/list")
    public TableDataInfo listCpu(HardwareCpu hardwareCpu)
    {
        startPage();
        List<HardwareCpu> list = hardwareCpuService.selectHardwareCpuList(hardwareCpu);
        return getDataTable(list);
    }

    /**
     * 查询GPU硬件列表（前台专用）
     */
    @GetMapping("/gpu/list")
    public TableDataInfo listGpu(HardwareGpu hardwareGpu)
    {
        startPage();
        List<HardwareGpu> list = hardwareGpuService.selectHardwareGpuList(hardwareGpu);
        return getDataTable(list);
    }

    /**
     * 查询内存硬件列表（前台专用）
     */
    @GetMapping("/memory/list")
    public TableDataInfo listMemory(HardwareMemory hardwareMemory)
    {
        startPage();
        List<HardwareMemory> list = hardwareMemoryService.selectHardwareMemoryList(hardwareMemory);
        return getDataTable(list);
    }

    /**
     * 查询主板硬件列表（前台专用）
     */
    @GetMapping("/motherboard/list")
    public TableDataInfo listMotherboard(HardwareMotherboard hardwareMotherboard)
    {
        startPage();
        List<HardwareMotherboard> list = hardwareMotherboardService.selectHardwareMotherboardList(hardwareMotherboard);
        return getDataTable(list);
    }

    /**
     * 查询电源硬件列表（前台专用）
     */
    @GetMapping("/power/list")
    public TableDataInfo listPower(HardwarePowerSupply hardwarePowerSupply)
    {
        startPage();
        List<HardwarePowerSupply> list = hardwarePowerSupplyService.selectHardwarePowerSupplyList(hardwarePowerSupply);
        return getDataTable(list);
    }
}