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
 * * @author RuoYi
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
     *
     * @param game 游戏对象
     * @param cpu  CPU硬件对象
     * @param gpu  显卡硬件对象
     * @param ram  内存硬件对象
     * @return Map<分辨率, 帧率> 例如: {"1080P": 144, "2K": 100, "4K": 60}
     */
    public Map<String, Integer> calculateFps(SysGame game, HardwareCpu cpu, HardwareGpu gpu, HardwareMemory ram) {
        Map<String, Integer> fpsResult = new HashMap<>();

        // 1. 预先计算 CPU 理论帧率 (CPU FPS)，因为 CPU 帧率通常不随分辨率剧烈变化，且算法设定 CPU 不分分辨率
        // 获取 CPU 平台类型 (CPU_INTEL 或 CPU_AMD)
        String cpuPlatform = "CPU_" + (StringUtils.isNotEmpty(cpu.getBrand()) ? cpu.getBrand().toUpperCase() : "INTEL");
        // 获取 CPU 基准系数 (默认查 1080P, 分数区间为 0-999999)
        BigDecimal cpuScore = cpu.getSingleCoreScore() != null ? cpu.getSingleCoreScore() : BigDecimal.ZERO;
        SysGameBenchmark cpuBenchmark = benchmarkMapper.selectBenchmarkByScore(game.getGameId(), cpuPlatform, "1080P", cpuScore);

        int cpuFps = 0;
        if (cpuBenchmark != null && cpuBenchmark.getBaseScore() != null && cpuBenchmark.getBaseScore().compareTo(BigDecimal.ZERO) > 0) {
            // 公式：CPU_FPS = cpu.singleCoreScore / benchmark.baseScore
            cpuFps = cpuScore.divide(cpuBenchmark.getBaseScore(), 0, RoundingMode.HALF_UP).intValue();
        }

        // 2. 应用 X3D 特殊加成
        if (cpuFps > 0 && StringUtils.isNotEmpty(cpu.getModel()) && cpu.getModel().toUpperCase().contains("X3D")) {
            String gameName = game.getGameName() != null ? game.getGameName().toUpperCase() : "";
            if (gameName.contains("CS2")) {
                cpuFps = (int) (cpuFps * 1.2); // CS2 提升 20%
            } else if (gameName.contains("PUBG")) {
                cpuFps = (int) (cpuFps * 1.1); // PUBG 提升 10%
            }
        }

        // 3. 获取内存修正系数 (RAM Buff)
        // SQL逻辑: frequency <= input AND type = input ORDER BY frequency DESC LIMIT 1
        String ramType = StringUtils.isNotEmpty(ram.getType()) ? ram.getType().toUpperCase() : "DDR4";
        Long ramFreq = ram.getFrequency() != null ? ram.getFrequency() : 3200L;
        SysGameRamBenchmark ramBenchmark = ramMapper.selectRamRatio(game.getGameId(), ramType, ramFreq);

        BigDecimal ramRatio = BigDecimal.ONE; // 默认系数 1.0
        if (ramBenchmark != null && ramBenchmark.getRatio() != null) {
            ramRatio = ramBenchmark.getRatio();
        }

        // 4. 遍历三种分辨率，计算 GPU 帧率并结合木桶效应
        // 获取 GPU 平台类型 (GPU_NVIDIA 或 GPU_AMD)
        String gpuPlatform = "GPU_" + (StringUtils.isNotEmpty(gpu.getBrand()) ? gpu.getBrand().toUpperCase() : "NVIDIA");
        BigDecimal gpuScore = gpu.getPerformanceScore() != null ? gpu.getPerformanceScore() : BigDecimal.ZERO;

        for (String resolution : RESOLUTIONS) {
            // 步骤 A: 计算显卡理论帧率 (GPU FPS)
            // 根据 分数区间 获取对应的 base_score
            SysGameBenchmark gpuBenchmark = benchmarkMapper.selectBenchmarkByScore(game.getGameId(), gpuPlatform, resolution, gpuScore);

            int finalFps = 0;
            if (gpuBenchmark != null && gpuBenchmark.getBaseScore() != null && gpuBenchmark.getBaseScore().compareTo(BigDecimal.ZERO) > 0) {
                // 公式：GPU_FPS = gpu.performanceScore / benchmark.baseScore
                int gpuFps = gpuScore.divide(gpuBenchmark.getBaseScore(), 0, RoundingMode.HALF_UP).intValue();

                // 步骤 C: 木桶效应 (Bottleneck)
                // 如果 CPU 还没查到数据(比如库里没配)，就暂时只看 GPU，避免直接为 0
                int rawFps = (cpuFps > 0) ? Math.min(gpuFps, cpuFps) : gpuFps;

                // 步骤 D: 内存修正 (RAM Buff)
                // 公式：Final_FPS = Raw_FPS * ram_ratio
                finalFps = new BigDecimal(rawFps).multiply(ramRatio).setScale(0, RoundingMode.HALF_UP).intValue();
            }

            fpsResult.put(resolution, finalFps);
        }

        return fpsResult;
    }
}