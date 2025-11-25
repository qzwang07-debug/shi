package com.zNova.system.service.impl;

import java.math.BigDecimal;
import java.util.List;
import com.zNova.common.utils.DateUtils;
import com.zNova.system.domain.HardwareCpu;
import com.zNova.system.domain.HardwareGpu;
import com.zNova.system.domain.HardwareMemory;
import com.zNova.system.domain.vo.PerformanceScoreVO;
import com.zNova.system.mapper.HardwareCpuMapper;
import com.zNova.system.mapper.HardwareGpuMapper;
import com.zNova.system.mapper.HardwareMemoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zNova.system.mapper.BizComputerGoodsMapper;
import com.zNova.system.domain.BizComputerGoods;
import com.zNova.system.service.IBizComputerGoodsService;

/**
 * 电脑商品Service业务层处理
 *
 * @author wyz
 * @date 2025-11-09
 */
@Service
public class BizComputerGoodsServiceImpl implements IBizComputerGoodsService {
    @Autowired
    private BizComputerGoodsMapper bizComputerGoodsMapper;

    /**
     * 查询电脑商品
     *
     * @param id 电脑商品主键
     * @return 电脑商品
     */
    @Override
    public BizComputerGoods selectBizComputerGoodsById(Long id) {
        return bizComputerGoodsMapper.selectBizComputerGoodsById(id);
    }

    /**
     * 查询电脑商品列表
     *
     * @param bizComputerGoods 电脑商品
     * @return 电脑商品
     */
    @Override
    public List<BizComputerGoods> selectBizComputerGoodsList(BizComputerGoods bizComputerGoods) {
        return bizComputerGoodsMapper.selectBizComputerGoodsList(bizComputerGoods);
    }

    /**
     * 新增电脑商品
     *
     * @param bizComputerGoods 电脑商品
     * @return 结果
     */
    @Override
    public int insertBizComputerGoods(BizComputerGoods bizComputerGoods) {
        bizComputerGoods.setCreateTime(DateUtils.getNowDate());
        return bizComputerGoodsMapper.insertBizComputerGoods(bizComputerGoods);
    }

    /**
     * 修改电脑商品
     *
     * @param bizComputerGoods 电脑商品
     * @return 结果
     */
    @Override
    public int updateBizComputerGoods(BizComputerGoods bizComputerGoods) {
        bizComputerGoods.setUpdateTime(DateUtils.getNowDate());
        return bizComputerGoodsMapper.updateBizComputerGoods(bizComputerGoods);
    }

    /**
     * 批量删除电脑商品
     *
     * @param ids 需要删除的电脑商品主键
     * @return 结果
     */
    @Override
    public int deleteBizComputerGoodsByIds(Long[] ids) {
        return bizComputerGoodsMapper.deleteBizComputerGoodsByIds(ids);
    }

    /**
     * 删除电脑商品信息
     *
     * @param id 电脑商品主键
     * @return 结果
     */
    @Override
    public int deleteBizComputerGoodsById(Long id) {
        return bizComputerGoodsMapper.deleteBizComputerGoodsById(id);
    }

    @Autowired
    private BizComputerGoodsMapper computerGoodsMapper;



    // 注入硬件相关的Mapper
    @Autowired
    private HardwareCpuMapper hardwareCpuMapper;

    @Autowired
    private HardwareGpuMapper hardwareGpuMapper;

    @Autowired
    private HardwareMemoryMapper hardwareMemoryMapper;

    // 原有CRUD方法实现保持不变...

    @Override
    public BizComputerGoods getPerformanceById(Long id) {
        return computerGoodsMapper.selectPerformanceById(id);
    }

    @Override
    public PerformanceScoreVO calculateScore(BizComputerGoods goods) {
        PerformanceScoreVO scoreVO = new PerformanceScoreVO();
        if (goods == null) {
            return scoreVO;
        }

        // 1. 通过CPU型号（model）查询CPU信息
        HardwareCpu cpu = hardwareCpuMapper.selectHardwareCpuByModel(goods.getCpu());
        int cpuBaseScore = 0;
        if (cpu != null) {
            BigDecimal multiCore = cpu.getMultiCoreScore() != null ? cpu.getMultiCoreScore() : BigDecimal.ZERO;
            BigDecimal singleCore = cpu.getSingleCoreScore() != null ? cpu.getSingleCoreScore() : BigDecimal.ZERO;

            if ("AMD".equalsIgnoreCase(cpu.getBrand())) {
                cpuBaseScore = multiCore.multiply(new BigDecimal("0.5")).add(singleCore).intValue();
            } else if ("Intel".equalsIgnoreCase(cpu.getBrand())) {
                cpuBaseScore = multiCore.multiply(new BigDecimal("0.3")).add(singleCore).intValue();
            }
        }

        // 2. 通过内存类型和频率查询内存信息
        HardwareMemory memory = hardwareMemoryMapper.selectHardwareMemoryByTypeAndFrequency(
                goods.getMemoryType(),
                goods.getMemoryFrequency()
        );
        int memoryScore = 0;
        if (memory != null) {
            Long frequency = memory.getFrequency() != null ? memory.getFrequency() : 0L;
            if ("DDR4".equalsIgnoreCase(memory.getType())) {
                memoryScore = (int) (frequency * 2);
            } else if ("DDR5".equalsIgnoreCase(memory.getType())) {
                memoryScore = frequency.intValue();
            }
        }

        // 3. 通过GPU型号（model）查询GPU信息
        HardwareGpu gpu = hardwareGpuMapper.selectHardwareGpuByModel(goods.getGpuModel());
        int gpuScore = 0;
        if (gpu != null && gpu.getPerformanceScore() != null) {
            gpuScore = gpu.getPerformanceScore().intValue();
        }

        // 4. 计算总分和百分比（逻辑不变）
        int totalScore = cpuBaseScore + memoryScore + gpuScore;
        int percentage = Math.min(100, (totalScore * 100) / 10000);

        scoreVO.setTotalScore(totalScore);
        scoreVO.setCpuScore(cpuBaseScore);
        scoreVO.setMemoryScore(memoryScore);
        scoreVO.setGpuScore(gpuScore);
        scoreVO.setPercentage(percentage);

        return scoreVO;
    }
}