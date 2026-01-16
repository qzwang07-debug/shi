package com.zNova.system.service.impl;
import com.zNova.common.utils.StringUtils;
import com.zNova.system.domain.*;
import com.zNova.system.mapper.SysGameBenchmarkMapper;
import com.zNova.system.mapper.SysGameRamBenchmarkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 游戏帧率评估核心服务
 * @author wyz
 */
@Service
public class PerformanceService {

    @Autowired
    private SysGameBenchmarkMapper benchmarkMapper;

    @Autowired
    private SysGameRamBenchmarkMapper ramMapper;

    // 分辨率常量
    private static final String[] RESOLUTIONS = {"1080P", "2K", "4K"};

    /**
     * 计算多分辨率下的预估帧率
     */
    public Map<String, Object> calculateFps(SysGame game, HardwareCpu cpu, HardwareGpu gpu, HardwareMemory ram) {
        Map<String, Object> fpsResult = new HashMap<>();

        // 1. 准备硬件基础数据
        String cpuPlatform = "CPU_" + (StringUtils.isNotEmpty(cpu.getBrand()) ? cpu.getBrand().toUpperCase() : "INTEL");
        BigDecimal cpuScore = cpu.getSingleCoreScore() != null ? cpu.getSingleCoreScore() : BigDecimal.ZERO;

        String gpuPlatform = "GPU_" + (StringUtils.isNotEmpty(gpu.getBrand()) ? gpu.getBrand().toUpperCase() : "NVIDIA");
        BigDecimal gpuScore = gpu.getPerformanceScore() != null ? gpu.getPerformanceScore() : BigDecimal.ZERO;

        // 2. 获取内存修正系数 (内存通常不分分辨率，取一次即可)
        String ramType = StringUtils.isNotEmpty(ram.getType()) ? ram.getType().toUpperCase() : "DDR4";
        Long ramFreq = ram.getFrequency() != null ? ram.getFrequency() : 3200L;
        SysGameRamBenchmark ramBenchmark = ramMapper.selectRamRatio(game.getGameId(), ramType, ramFreq);

        BigDecimal ramRatio = BigDecimal.ONE;
        if (ramBenchmark != null && ramBenchmark.getRatio() != null) {
            ramRatio = ramBenchmark.getRatio();
        }

        // 存储最终帧率
        Map<String, Integer> finalFpsMap = new HashMap<>();
        // 存储CPU理论帧率
        Map<String, Integer> cpuFpsMap = new HashMap<>();
        // 存储GPU理论帧率
        Map<String, Integer> gpuFpsMap = new HashMap<>();

        // 3. 遍历分辨率，分别计算 CPU 和 GPU 的理论帧率
        for (String resolution : RESOLUTIONS) {

            // --- 步骤 A: 计算 CPU 理论帧率 (现在也是分分辨率查询) ---
            SysGameBenchmark cpuBenchmark = benchmarkMapper.selectBenchmarkByScore(game.getGameId(), cpuPlatform, resolution, cpuScore);
            int cpuFps = 0;
            if (cpuBenchmark != null && cpuBenchmark.getBaseScore() != null && cpuBenchmark.getBaseScore().compareTo(BigDecimal.ZERO) > 0) {
                cpuFps = cpuScore.divide(cpuBenchmark.getBaseScore(), 0, RoundingMode.HALF_UP).intValue();
            }

            // 应用 X3D 特殊加成 (X3D对低分辨率敏感，高分辨率可能影响较小，这里简单处理为统一加成，也可根据 resolution 判断)
            if (cpuFps > 0 && StringUtils.isNotEmpty(cpu.getModel()) && cpu.getModel().toUpperCase().contains("X3D")) {
                String gameName = game.getGameName() != null ? game.getGameName().toUpperCase() : "";
                if (gameName.contains("CS2")) {
                    cpuFps = (int) (cpuFps * 1.2);
                } else if (gameName.contains("PUBG")) {
                    cpuFps = (int) (cpuFps * 1.1);
                }
            }

            // --- 步骤 B: 计算 GPU 理论帧率 ---
            SysGameBenchmark gpuBenchmark = benchmarkMapper.selectBenchmarkByScore(game.getGameId(), gpuPlatform, resolution, gpuScore);
            int gpuFps = 0;
            if (gpuBenchmark != null && gpuBenchmark.getBaseScore() != null && gpuBenchmark.getBaseScore().compareTo(BigDecimal.ZERO) > 0) {
                gpuFps = gpuScore.divide(gpuBenchmark.getBaseScore(), 0, RoundingMode.HALF_UP).intValue();
            }

            // --- 步骤 C: 木桶效应 & 内存修正 ---
            int finalFps = 0;

            // 如果某一项查不到数据(比如库里只配了GPU数据)，则以查到的那一项为准，避免直接归零
            int rawFps;
            if (cpuFps > 0 && gpuFps > 0) {
                rawFps = Math.min(gpuFps, cpuFps);
            } else {
                rawFps = Math.max(gpuFps, cpuFps); // 取非零的那个
            }

            // 应用内存修正
            finalFps = new BigDecimal(rawFps).multiply(ramRatio).setScale(0, RoundingMode.HALF_UP).intValue();

            // 保存结果
            finalFpsMap.put(resolution, finalFps);
            cpuFpsMap.put(resolution, cpuFps);
            gpuFpsMap.put(resolution, gpuFps);
        }

        // 返回包含最终帧率、CPU理论帧率和GPU理论帧率的结果
        fpsResult.put("finalFps", finalFpsMap);
        fpsResult.put("cpuFps", cpuFpsMap);
        fpsResult.put("gpuFps", gpuFpsMap);

        return fpsResult;
    }
}