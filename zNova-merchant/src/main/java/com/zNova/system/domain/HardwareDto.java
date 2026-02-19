package com.zNova.system.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class HardwareDto implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String title;
    private String cpuModel;
    private BigDecimal cpuPrice;
    private Long cpuTdp;
    private String moboBrand;
    private String moboModel;
    private String moboSeries;
    private BigDecimal moboPrice;
    private String ramBrand;
    private String ramInterface;
    private String ramFrequency;
    private String ramCapacity;
    private BigDecimal ramPrice;
    private String gpuBrand;
    private String gpuModel;
    private String gpuSeries;
    private BigDecimal gpuPrice;
    private String ssdFullName;
    private BigDecimal ssdPrice;
    private String coolerFullName;
    private BigDecimal coolerPrice;
    private String psuBrand;
    private String psuWattage;
    private String psuSeries;
    private BigDecimal psuPrice;
    private String caseFullName;
    private BigDecimal casePrice;
    private String fanFullName;
    private BigDecimal fanPrice;
    private BigDecimal totalPrice;
    private Integer totalScore;
    private String currentResolution;
    private String bottleneckType;
    private String bottleneckMessage;
    private String bottleneckDetail;
    private String cpLevel;
    private Double cpDisplayValue;
    private Double cpPercentage;
    private String monitorBestResolution;
    private String monitorBestRefreshRate;
    private String monitorBestReason;
    private List<MonitorOptionDto> monitorOptions;
    private List<GameBenchmarkDto> games;
    private String style;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getCpuModel()
    {
        return cpuModel;
    }

    public void setCpuModel(String cpuModel)
    {
        this.cpuModel = cpuModel;
    }

    public BigDecimal getCpuPrice()
    {
        return cpuPrice;
    }

    public void setCpuPrice(BigDecimal cpuPrice)
    {
        this.cpuPrice = cpuPrice;
    }

    public Long getCpuTdp()
    {
        return cpuTdp;
    }

    public void setCpuTdp(Long cpuTdp)
    {
        this.cpuTdp = cpuTdp;
    }

    public String getMoboBrand()
    {
        return moboBrand;
    }

    public void setMoboBrand(String moboBrand)
    {
        this.moboBrand = moboBrand;
    }

    public String getMoboModel()
    {
        return moboModel;
    }

    public void setMoboModel(String moboModel)
    {
        this.moboModel = moboModel;
    }

    public String getMoboSeries()
    {
        return moboSeries;
    }

    public void setMoboSeries(String moboSeries)
    {
        this.moboSeries = moboSeries;
    }

    public BigDecimal getMoboPrice()
    {
        return moboPrice;
    }

    public void setMoboPrice(BigDecimal moboPrice)
    {
        this.moboPrice = moboPrice;
    }

    public String getRamBrand()
    {
        return ramBrand;
    }

    public void setRamBrand(String ramBrand)
    {
        this.ramBrand = ramBrand;
    }

    public String getRamInterface()
    {
        return ramInterface;
    }

    public void setRamInterface(String ramInterface)
    {
        this.ramInterface = ramInterface;
    }

    public String getRamFrequency()
    {
        return ramFrequency;
    }

    public void setRamFrequency(String ramFrequency)
    {
        this.ramFrequency = ramFrequency;
    }

    public String getRamCapacity()
    {
        return ramCapacity;
    }

    public void setRamCapacity(String ramCapacity)
    {
        this.ramCapacity = ramCapacity;
    }

    public BigDecimal getRamPrice()
    {
        return ramPrice;
    }

    public void setRamPrice(BigDecimal ramPrice)
    {
        this.ramPrice = ramPrice;
    }

    public String getGpuBrand()
    {
        return gpuBrand;
    }

    public void setGpuBrand(String gpuBrand)
    {
        this.gpuBrand = gpuBrand;
    }

    public String getGpuModel()
    {
        return gpuModel;
    }

    public void setGpuModel(String gpuModel)
    {
        this.gpuModel = gpuModel;
    }

    public String getGpuSeries()
    {
        return gpuSeries;
    }

    public void setGpuSeries(String gpuSeries)
    {
        this.gpuSeries = gpuSeries;
    }

    public BigDecimal getGpuPrice()
    {
        return gpuPrice;
    }

    public void setGpuPrice(BigDecimal gpuPrice)
    {
        this.gpuPrice = gpuPrice;
    }

    public String getSsdFullName()
    {
        return ssdFullName;
    }

    public void setSsdFullName(String ssdFullName)
    {
        this.ssdFullName = ssdFullName;
    }

    public BigDecimal getSsdPrice()
    {
        return ssdPrice;
    }

    public void setSsdPrice(BigDecimal ssdPrice)
    {
        this.ssdPrice = ssdPrice;
    }

    public String getCoolerFullName()
    {
        return coolerFullName;
    }

    public void setCoolerFullName(String coolerFullName)
    {
        this.coolerFullName = coolerFullName;
    }

    public BigDecimal getCoolerPrice()
    {
        return coolerPrice;
    }

    public void setCoolerPrice(BigDecimal coolerPrice)
    {
        this.coolerPrice = coolerPrice;
    }

    public String getPsuBrand()
    {
        return psuBrand;
    }

    public void setPsuBrand(String psuBrand)
    {
        this.psuBrand = psuBrand;
    }

    public String getPsuWattage()
    {
        return psuWattage;
    }

    public void setPsuWattage(String psuWattage)
    {
        this.psuWattage = psuWattage;
    }

    public String getPsuSeries()
    {
        return psuSeries;
    }

    public void setPsuSeries(String psuSeries)
    {
        this.psuSeries = psuSeries;
    }

    public BigDecimal getPsuPrice()
    {
        return psuPrice;
    }

    public void setPsuPrice(BigDecimal psuPrice)
    {
        this.psuPrice = psuPrice;
    }

    public String getCaseFullName()
    {
        return caseFullName;
    }

    public void setCaseFullName(String caseFullName)
    {
        this.caseFullName = caseFullName;
    }

    public BigDecimal getCasePrice()
    {
        return casePrice;
    }

    public void setCasePrice(BigDecimal casePrice)
    {
        this.casePrice = casePrice;
    }

    public String getFanFullName()
    {
        return fanFullName;
    }

    public void setFanFullName(String fanFullName)
    {
        this.fanFullName = fanFullName;
    }

    public BigDecimal getFanPrice()
    {
        return fanPrice;
    }

    public void setFanPrice(BigDecimal fanPrice)
    {
        this.fanPrice = fanPrice;
    }

    public BigDecimal getTotalPrice()
    {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice)
    {
        this.totalPrice = totalPrice;
    }

    public Integer getTotalScore()
    {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore)
    {
        this.totalScore = totalScore;
    }

    public String getCurrentResolution()
    {
        return currentResolution;
    }

    public void setCurrentResolution(String currentResolution)
    {
        this.currentResolution = currentResolution;
    }

    public String getBottleneckType()
    {
        return bottleneckType;
    }

    public void setBottleneckType(String bottleneckType)
    {
        this.bottleneckType = bottleneckType;
    }

    public String getBottleneckMessage()
    {
        return bottleneckMessage;
    }

    public void setBottleneckMessage(String bottleneckMessage)
    {
        this.bottleneckMessage = bottleneckMessage;
    }

    public String getBottleneckDetail()
    {
        return bottleneckDetail;
    }

    public void setBottleneckDetail(String bottleneckDetail)
    {
        this.bottleneckDetail = bottleneckDetail;
    }

    public String getCpLevel()
    {
        return cpLevel;
    }

    public void setCpLevel(String cpLevel)
    {
        this.cpLevel = cpLevel;
    }

    public Double getCpDisplayValue()
    {
        return cpDisplayValue;
    }

    public void setCpDisplayValue(Double cpDisplayValue)
    {
        this.cpDisplayValue = cpDisplayValue;
    }

    public Double getCpPercentage()
    {
        return cpPercentage;
    }

    public void setCpPercentage(Double cpPercentage)
    {
        this.cpPercentage = cpPercentage;
    }

    public String getMonitorBestResolution()
    {
        return monitorBestResolution;
    }

    public void setMonitorBestResolution(String monitorBestResolution)
    {
        this.monitorBestResolution = monitorBestResolution;
    }

    public String getMonitorBestRefreshRate()
    {
        return monitorBestRefreshRate;
    }

    public void setMonitorBestRefreshRate(String monitorBestRefreshRate)
    {
        this.monitorBestRefreshRate = monitorBestRefreshRate;
    }

    public String getMonitorBestReason()
    {
        return monitorBestReason;
    }

    public void setMonitorBestReason(String monitorBestReason)
    {
        this.monitorBestReason = monitorBestReason;
    }

    public List<MonitorOptionDto> getMonitorOptions()
    {
        return monitorOptions;
    }

    public void setMonitorOptions(List<MonitorOptionDto> monitorOptions)
    {
        this.monitorOptions = monitorOptions;
    }

    public List<GameBenchmarkDto> getGames()
    {
        return games;
    }

    public void setGames(List<GameBenchmarkDto> games)
    {
        this.games = games;
    }

    public String getStyle()
    {
        return style;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public static class MonitorOptionDto implements Serializable
    {
        private static final long serialVersionUID = 1L;

        private String resolution;
        private String refreshRate;
        private String desc;
        private Boolean best;

        public String getResolution()
        {
            return resolution;
        }

        public void setResolution(String resolution)
        {
            this.resolution = resolution;
        }

        public String getRefreshRate()
        {
            return refreshRate;
        }

        public void setRefreshRate(String refreshRate)
        {
            this.refreshRate = refreshRate;
        }

        public String getDesc()
        {
            return desc;
        }

        public void setDesc(String desc)
        {
            this.desc = desc;
        }

        public Boolean getBest()
        {
            return best;
        }

        public void setBest(Boolean best)
        {
            this.best = best;
        }
    }

    public static class GameBenchmarkDto implements Serializable
    {
        private static final long serialVersionUID = 1L;

        private String gameName;
        private String description;
        private Map<String, Integer> fps;
        private Map<String, Integer> cpuFps;
        private Map<String, Integer> gpuFps;

        public String getGameName()
        {
            return gameName;
        }

        public void setGameName(String gameName)
        {
            this.gameName = gameName;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public Map<String, Integer> getFps()
        {
            return fps;
        }

        public void setFps(Map<String, Integer> fps)
        {
            this.fps = fps;
        }

        public Map<String, Integer> getCpuFps()
        {
            return cpuFps;
        }

        public void setCpuFps(Map<String, Integer> cpuFps)
        {
            this.cpuFps = cpuFps;
        }

        public Map<String, Integer> getGpuFps()
        {
            return gpuFps;
        }

        public void setGpuFps(Map<String, Integer> gpuFps)
        {
            this.gpuFps = gpuFps;
        }
    }
}
