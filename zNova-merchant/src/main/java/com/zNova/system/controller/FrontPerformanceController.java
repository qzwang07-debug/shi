package com.zNova.system.controller;

import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.common.utils.StringUtils;
import com.zNova.system.domain.HardwareCpu;
import com.zNova.system.domain.HardwareGpu;
import com.zNova.system.domain.HardwareMemory;
import com.zNova.system.domain.SysGame;
import com.zNova.system.service.*;
import com.zNova.system.service.impl.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 前台性能评估Controller
 */
@RestController
@RequestMapping("/front/performance")
public class FrontPerformanceController extends BaseController {

    @Autowired
    private IHardwareCpuService cpuService;
    @Autowired
    private IHardwareGpuService gpuService;
    @Autowired
    private ISysGameService gameService;
    @Autowired
    private PerformanceService performanceService;

    // 硬编码需要展示的四款游戏名称
    private static final List<String> TARGET_GAMES = Arrays.asList("CS2", "PUBG", "三角洲行动", "黑神话：悟空");

    /**
     * 提交配置单进行性能评估
     */
    @PostMapping("/assess")
    public AjaxResult assess(@RequestBody AssessmentRequestDTO request) {
        // 1. 获取硬件详情
        HardwareCpu cpu = findCpu(request.getCpuModel());
        HardwareGpu gpu = findGpu(request.getGpuModel());
        HardwareMemory ram = findMemory(request.getRamType(), request.getRamFrequency());

        if (cpu == null || gpu == null) {
            return AjaxResult.error("无法识别选中的CPU或显卡型号，请确保从下拉框中选择");
        }

        Map<String, Object> result = new HashMap<>();

        // ---------------------------------------------------------------
        // 2. 计算硬件总跑分 (严格按照用户提供的算法)
        // ---------------------------------------------------------------

        // 准备数据，处理空指针，默认为 0
        double cpuMulti = cpu.getMultiCoreScore() != null ? cpu.getMultiCoreScore().doubleValue() : 0;
        double cpuSingle = cpu.getSingleCoreScore() != null ? cpu.getSingleCoreScore().doubleValue() : 0;
        double gpuScore = gpu.getPerformanceScore() != null ? gpu.getPerformanceScore().doubleValue() : 0;
        double ramFreq = ram.getFrequency() != null ? ram.getFrequency().doubleValue() : 0;
        String cpuBrand = StringUtils.isNotEmpty(cpu.getBrand()) ? cpu.getBrand() : "Intel";
        String ramType = StringUtils.isNotEmpty(ram.getType()) ? ram.getType() : "DDR4";

        // A. 计算 CPU 基础分
        double cpuBaseScore = 0;
        if ("AMD".equalsIgnoreCase(cpuBrand)) {
            // AMD: 多核 * 0.5 + 单核
            cpuBaseScore = Math.round((cpuMulti * 0.5) + cpuSingle);
        } else {
            // Intel (或其他): 多核 * 0.3 + 单核
            cpuBaseScore = Math.round((cpuMulti * 0.3) + cpuSingle);
        }

        // B. 计算 内存 分数
        double memoryScore = 0;
        if ("DDR5".equalsIgnoreCase(ramType)) {
            // DDR5: 频率 / 2
            memoryScore = ramFreq / 2;
        } else {
            // DDR4: 频率 (原样)
            memoryScore = ramFreq;
        }

        // C. 总分计算
        double totalScoreVal = cpuBaseScore + memoryScore + gpuScore;
        result.put("totalScore", (int) totalScoreVal);

        // 可选：如果前端需要百分比，这里也可以算，但前端已有逻辑处理进度条
        // double maxScore = 28387;
        // int percentage = (int) Math.min(100, Math.round((totalScoreVal / maxScore) * 100));

        // ---------------------------------------------------------------
        // 3. 计算各游戏帧率 (逻辑保持不变)
        // ---------------------------------------------------------------
        List<Map<String, Object>> gameResults = new ArrayList<>();
        SysGame searchGame = new SysGame();
        List<SysGame> allGames = gameService.selectSysGameList(searchGame);

        for (String targetName : TARGET_GAMES) {
            SysGame game = allGames.stream()
                    .filter(g -> g.getGameName() != null && g.getGameName().contains(targetName))
                    .findFirst()
                    .orElse(null);

            if (game != null) {
                Map<String, Integer> fpsMap = performanceService.calculateFps(game, cpu, gpu, ram);
                Map<String, Object> gameData = new HashMap<>();
                gameData.put("gameName", game.getGameName());
                gameData.put("description", game.getDescription());
                gameData.put("fps", fpsMap);
                gameResults.add(gameData);
            }
        }
        result.put("games", gameResults);

        return AjaxResult.success(result);
    }

    // --- 辅助查找方法 ---
    private HardwareCpu findCpu(String model) {
        HardwareCpu search = new HardwareCpu();
        search.setModel(model);
        List<HardwareCpu> list = cpuService.selectHardwareCpuList(search);
        return list.isEmpty() ? null : list.get(0);
    }

    private HardwareGpu findGpu(String model) {
        HardwareGpu search = new HardwareGpu();
        search.setModel(model);
        List<HardwareGpu> list = gpuService.selectHardwareGpuList(search);
        return list.isEmpty() ? null : list.get(0);
    }

    private HardwareMemory findMemory(String type, Long frequency) {
        HardwareMemory ram = new HardwareMemory();
        ram.setType(type);
        ram.setFrequency(frequency);
        return ram;
    }

    public static class AssessmentRequestDTO {
        private String cpuModel;
        private String gpuModel;
        private String ramType;
        private Long ramFrequency;
        // Getters and Setters
        public String getCpuModel() { return cpuModel; }
        public void setCpuModel(String cpuModel) { this.cpuModel = cpuModel; }
        public String getGpuModel() { return gpuModel; }
        public void setGpuModel(String gpuModel) { this.gpuModel = gpuModel; }
        public String getRamType() { return ramType; }
        public void setRamType(String ramType) { this.ramType = ramType; }
        public Long getRamFrequency() { return ramFrequency; }
        public void setRamFrequency(Long ramFrequency) { this.ramFrequency = ramFrequency; }
    }
}