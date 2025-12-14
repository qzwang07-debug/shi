package com.zNova.system.service;

import com.zNova.system.domain.HardwareCpu;
import com.zNova.system.domain.HardwareGpu;
import com.zNova.system.domain.HardwareMemory;
import com.zNova.system.mapper.HardwareCpuMapper;
import com.zNova.system.mapper.HardwareGpuMapper;
import com.zNova.system.mapper.HardwareMemoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 性能分数计算组件
 * 用于计算电脑配置的综合性能分数
 * @author wyz
 * @date 2025-12-13
 */
@Component
public class PerformanceCalculator {

    @Autowired
    private HardwareCpuMapper hardwareCpuMapper;

    @Autowired
    private HardwareGpuMapper hardwareGpuMapper;

    @Autowired
    private HardwareMemoryMapper hardwareMemoryMapper;

    /**
     * 计算性能分数
     * @param cpuId CPU ID
     * @param gpuId GPU ID
     * @param ramId 内存 ID
     * @return 综合性能分数
     */
    public Integer calculateScore(Long cpuId, Long gpuId, Long ramId) {
        // 1. 查询硬件详情
        HardwareCpu cpu = hardwareCpuMapper.selectHardwareCpuById(cpuId);
        HardwareGpu gpu = hardwareGpuMapper.selectHardwareGpuById(gpuId);
        HardwareMemory ram = hardwareMemoryMapper.selectHardwareMemoryById(ramId);

        return calculateScoreInternal(cpu, gpu, ram);
    }

    /**
     * 根据硬件型号计算性能分数
     * @param cpuModel CPU型号
     * @param gpuModel GPU型号
     * @param memoryType 内存类型
     * @param memoryFrequency 内存频率
     * @return 综合性能分数
     */
    public Integer calculateScoreByModel(String cpuModel, String gpuModel, String memoryType, Long memoryFrequency) {
        // 1. 根据型号查询硬件详情
        HardwareCpu cpu = hardwareCpuMapper.selectHardwareCpuByModel(cpuModel);
        HardwareGpu gpu = hardwareGpuMapper.selectHardwareGpuByModel(gpuModel);

        // 2. 查询内存详情（根据类型和频率）
        HardwareMemory ram = null;
        if (memoryType != null && memoryFrequency != null) {
            try {
                ram = hardwareMemoryMapper.selectHardwareMemoryByTypeAndFrequency(
                        memoryType,
                        memoryFrequency.intValue()
                );
            } catch (Exception e) {
                // 如果查询失败，创建一个默认的内存对象
                ram = new HardwareMemory();
                ram.setType(memoryType);
                ram.setFrequency(memoryFrequency);
            }
        }

        // 如果内存查询失败，创建一个默认的内存对象
        if (ram == null) {
            ram = new HardwareMemory();
            ram.setType(memoryType != null ? memoryType : "DDR4");
            ram.setFrequency(memoryFrequency != null ? memoryFrequency : 3200L);
        }

        return calculateScoreInternal(cpu, gpu, ram);
    }

    /**
     * 内部计算逻辑
     * @param cpu CPU详情
     * @param gpu GPU详情
     * @param ram 内存详情
     * @return 综合性能分数
     */
    private Integer calculateScoreInternal(HardwareCpu cpu, HardwareGpu gpu, HardwareMemory ram) {
        // 1. 计算CPU分数
        int cpuBaseScore = 0;
        if (cpu != null) {
            BigDecimal multiCore = cpu.getMultiCoreScore() != null ? cpu.getMultiCoreScore() : BigDecimal.ZERO;
            BigDecimal singleCore = cpu.getSingleCoreScore() != null ? cpu.getSingleCoreScore() : BigDecimal.ZERO;

            if ("AMD".equalsIgnoreCase(cpu.getBrand())) {
                cpuBaseScore = multiCore.multiply(new BigDecimal("0.5")).add(singleCore).intValue();
            } else if ("Intel".equalsIgnoreCase(cpu.getBrand())) {
                cpuBaseScore = multiCore.multiply(new BigDecimal("0.3")).add(singleCore).intValue();
            } else {
                // 默认计算方式
                cpuBaseScore = multiCore.multiply(new BigDecimal("0.4")).add(singleCore).intValue();
            }
        }

        // 2. 计算内存分数
        int memoryScore = 0;
        if (ram != null) {
            Long frequency = ram.getFrequency() != null ? ram.getFrequency() : 0L;
            if ("DDR4".equalsIgnoreCase(ram.getType())) {
                memoryScore = frequency.intValue();
            } else if ("DDR5".equalsIgnoreCase(ram.getType())) {
                memoryScore = (int) (frequency / 2);
            } else {
                // 默认DDR4计算方式
                memoryScore = (int) (frequency * 2);
            }
        }

        // 3. 计算GPU分数
        int gpuScore = 0;
        if (gpu != null && gpu.getPerformanceScore() != null) {
            gpuScore = gpu.getPerformanceScore().intValue();
        }

        // 4. 计算总分（简单相加，不是加权平均）
        int totalScore = cpuBaseScore + memoryScore + gpuScore;

        // 5. 确保分数为正数
        return Math.max(0, totalScore);
    }
}