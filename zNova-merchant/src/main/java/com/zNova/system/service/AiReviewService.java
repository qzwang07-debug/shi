package com.zNova.system.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.zNova.common.exception.ServiceException;
import com.zNova.common.utils.DateUtils;
import com.zNova.common.utils.SecurityUtils;
import com.zNova.common.utils.StringUtils;
import com.zNova.framework.manager.AsyncManager;
import com.zNova.system.domain.HardwareDto;
import com.zNova.system.domain.SysAiReviewLog;
import com.zNova.system.mapper.SysAiReviewLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;

@Service
public class AiReviewService
{
    private static final Logger log = LoggerFactory.getLogger(AiReviewService.class);

    private static final String SYSTEM_PROMPT_FUN =
        "你是一位混迹图拉丁吧 10 年的 DIY 老哥，懂硬件、会调侃、说话接地气。"
            + "输出语言必须是中文，语气要有江湖味，允许带点损但不能人身攻击。"
            + "【风格规则】"
            + "1. 配置明显离谱时（例如 i9 配 GT710），直接开启嘲讽模式，反问句和感叹号可以多用。"
            + "2. 配置很强时，用'羡慕嫉妒恨'的口吻夸，像看别人中彩票。"
            + "3. 必须重点检查散热和电源；如果高功耗 CPU 配廉价散热，必须明确喊出'要烧起来了！'。"
            + "4. 严禁固定套话，每次尽量换句式、换比喻、换吐槽角度。"
            + "【输出结构】请使用 Markdown，按以下小节输出："
            + "一句话总评、硬件搭配吐槽/夸奖、游戏帧率点评、散热与电源风险、可落地升级建议。";

    private static final String SYSTEM_PROMPT_EXPERT =
        "## 角色设定\n"
            + "你是一位拥有 20 年经验的计算机硬件系统架构师。你的评估风格客观、严谨、冷峻，只用数据和理论说话。\n"
            + "你的受众是专业人士（设计师、剪辑师、程序员），请摒弃一切网络用语，保持专业形象。\n\n"
            + "## 评估重点\n"
            + "1. **理论性能边界**：分析硬件的 IPC 性能、CUDA 核心数、显存位宽对实际性能的影响。\n"
            + "2. **生产力场景**：必须评估该配置在渲染 (Blender/C4D)、视频剪辑 (Premiere/DaVinci)、代码编译等场景下的表现。\n"
            + "3. **系统瓶颈**：精准指出 PCIe 通道、内存带宽或供电模组可能存在的短板。\n\n"
            + "## 输出格式 (严格遵守 Markdown)\n"
            + "请按以下结构输出：\n"
            + "### 📊 综合评级\n"
            + "(给出 S/A/B/C/D 评级，并简述理由)\n\n"
            + "### 🛠 生产力表现\n"
            + "(列出擅长的专业软件场景)\n\n"
            + "### 📉 瓶颈与短板分析\n"
            + "(从架构角度分析系统的限制因素)\n\n"
            + "### 🔧 优化方案\n"
            + "(为了追求极致稳定性或性能，建议如何调整)";

    private static final String SYSTEM_PROMPT_BEGINNER =
        "## 角色设定\n"
            + "你是一位温柔、耐心且超级懂生活的电脑导购顾问。你的客户是完全不懂参数的'数码小白'。\n"
            + "❌ **绝对禁止**：直接堆砌参数（如'主频 5.0GHz'、'CUDA 核心 3000'），用户看不懂。\n"
            + "✅ **必须做到**：把硬件比喻成生活中的事物（如：硬盘是书包，内存是桌面，CPU 是大脑）。\n\n"
            + "## 评估策略\n"
            + "1. **场景化描述**：告诉用户这台电脑能用来干什么（追剧、做表格、玩像电影一样的游戏）。\n"
            + "2. **耐用性分析**：用大白话解释这台电脑能流畅用几年。\n"
            + "3. **安抚情绪**：如果配置一般，要告诉用户'对于家用完全足够了，不用盲目追求贵的'。\n\n"
            + "## 输出格式 (严格遵守 Markdown)\n"
            + "请按以下结构输出：\n"
            + "### 🏠 这台电脑能帮我做什么？\n"
            + "(列出 3 个最具体的使用场景)\n\n"
            + "### 🐢 会不会卡顿？\n"
            + "(用通俗的比喻解释流畅度)\n\n"
            + "### ⏳ 能用多久？\n"
            + "(预测使用寿命)\n\n"
            + "### 📝 购买建议\n"
            + "(一句话总结是否推荐)";

    @Value("${ai.review.provider:deepseek}")
    private String provider;

    @Value("${ai.review.deepseek.base-url:https://api.deepseek.com/v1/chat/completions}")
    private String deepseekUrl;

    @Value("${ai.review.deepseek.api-key:}")
    private String deepseekApiKey;

    @Value("${ai.review.deepseek.model:deepseek-chat}")
    private String deepseekModel;

    @Value("${ai.review.modelscope.base-url:https://api-inference.modelscope.cn/v1/chat/completions}")
    private String modelscopeUrl;

    @Value("${ai.review.modelscope.api-key:}")
    private String modelscopeApiKey;

    @Value("${ai.review.modelscope.model:moonshotai/Kimi-K2.5}")
    private String modelscopeModel;

    @Value("${ai.review.temperature:0.9}")
    private Double temperature;

    @Autowired
    private SysAiReviewLogMapper sysAiReviewLogMapper;

    public String generateReview(HardwareDto dto)
    {
        if (dto == null)
        {
            throw new ServiceException("Review payload is empty.");
        }

        String style = dto.getStyle();
        String systemPrompt = getSystemPrompt(style);
        String userPrompt = buildUserPrompt(dto, style);
        String aiResponse = callModelApi(systemPrompt, userPrompt);

        Long userId = null;
        try
        {
            userId = SecurityUtils.getUserId();
        }
        catch (Exception ignored)
        {
        }
        saveLogAsync(userId, buildHardwareSummary(dto), aiResponse);
        return aiResponse;
    }

    private String getSystemPrompt(String style)
    {
        if ("expert".equalsIgnoreCase(style))
        {
            return SYSTEM_PROMPT_EXPERT;
        }
        else if ("beginner".equalsIgnoreCase(style))
        {
            return SYSTEM_PROMPT_BEGINNER;
        }
        return SYSTEM_PROMPT_FUN;
    }

    private String callModelApi(String systemPrompt, String userPrompt)
    {
        try
        {
            String apiUrl;
            String apiKey;
            String model;

            if ("modelscope".equalsIgnoreCase(provider))
            {
                apiUrl = modelscopeUrl;
                apiKey = modelscopeApiKey;
                model = modelscopeModel;
            }
            else
            {
                apiUrl = deepseekUrl;
                apiKey = deepseekApiKey;
                model = deepseekModel;
            }

            if (StringUtils.isEmpty(apiKey))
            {
                throw new ServiceException("Please configure ai.review." + provider + ".api-key in application.yml.");
            }

            double finalTemperature = (temperature != null && temperature >= 0.8D && temperature <= 0.9D)
                ? temperature
                : 0.9D;

            JSONObject payload = new JSONObject();
            payload.put("model", model);
            payload.put("temperature", finalTemperature);
            payload.put("top_p", 0.95D);
            payload.put("stream", false);

            JSONArray messages = new JSONArray();
            messages.add(buildMessage("system", systemPrompt));
            messages.add(buildMessage("user", userPrompt));
            payload.put("messages", messages);

            HttpResponse response = HttpRequest.post(apiUrl)
                .timeout(60000)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(payload.toJSONString())
                .execute();

            if (response.getStatus() < 200 || response.getStatus() >= 300)
            {
                log.error("AI call failed, provider={}, status={}, body={}", provider, response.getStatus(), response.body());
                throw new ServiceException("AI call failed, status: " + response.getStatus());
            }

            JSONObject root = JSONObject.parseObject(response.body());
            JSONArray choices = root.getJSONArray("choices");
            if (choices == null || choices.isEmpty())
            {
                throw new ServiceException("AI response choices are empty.");
            }
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            String content = message == null ? null : message.getString("content");
            if (StringUtils.isEmpty(content))
            {
                throw new ServiceException("AI response content is empty.");
            }
            return content.trim();
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            log.error("AI call exception", e);
            throw new ServiceException("AI service is temporarily unavailable.");
        }
    }

    private JSONObject buildMessage(String role, String content)
    {
        JSONObject message = new JSONObject();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private String buildUserPrompt(HardwareDto dto, String style)
    {
        if ("expert".equalsIgnoreCase(style))
        {
            return buildExpertUserPrompt(dto);
        }
        else if ("beginner".equalsIgnoreCase(style))
        {
            return buildBeginnerUserPrompt(dto);
        }
        return buildFunUserPrompt(dto);
    }

    private String buildFunUserPrompt(HardwareDto dto)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Please review this DIY build in real time and respond in Chinese Markdown.\n");
        sb.append("## 1) Hardware details\n");
        sb.append("- Title: ").append(text(dto.getTitle())).append('\n');
        sb.append("- CPU: ").append(text(dto.getCpuModel())).append(" / ").append(num(dto.getCpuPrice())).append(" CNY / TDP ")
            .append(num(dto.getCpuTdp())).append("W\n");
        sb.append("- Motherboard: ").append(text(dto.getMoboBrand())).append(' ')
            .append(text(dto.getMoboModel())).append(' ')
            .append(text(dto.getMoboSeries())).append(" / ").append(num(dto.getMoboPrice())).append(" CNY\n");
        sb.append("- RAM: ").append(text(dto.getRamBrand())).append(' ')
            .append(text(dto.getRamInterface())).append(' ')
            .append(text(dto.getRamFrequency())).append("MHz ")
            .append(text(dto.getRamCapacity())).append(" / ").append(num(dto.getRamPrice())).append(" CNY\n");
        sb.append("- GPU: ").append(text(dto.getGpuBrand())).append(' ')
            .append(text(dto.getGpuModel())).append(' ')
            .append(text(dto.getGpuSeries())).append(" / ").append(num(dto.getGpuPrice())).append(" CNY\n");
        sb.append("- SSD: ").append(text(dto.getSsdFullName())).append(" / ").append(num(dto.getSsdPrice())).append(" CNY\n");
        sb.append("- Cooler: ").append(text(dto.getCoolerFullName())).append(" / ").append(num(dto.getCoolerPrice())).append(" CNY\n");
        sb.append("- PSU: ").append(text(dto.getPsuBrand())).append(' ')
            .append(text(dto.getPsuWattage())).append("W ")
            .append(text(dto.getPsuSeries())).append(" / ").append(num(dto.getPsuPrice())).append(" CNY\n");
        sb.append("- Case: ").append(text(dto.getCaseFullName())).append(" / ").append(num(dto.getCasePrice())).append(" CNY\n");
        sb.append("- Fan/Other: ").append(text(dto.getFanFullName())).append(" / ").append(num(dto.getFanPrice())).append(" CNY\n");
        sb.append("- Total price: ").append(num(dto.getTotalPrice())).append(" CNY\n");

        sb.append("\n## 2) Existing score and analysis\n");
        sb.append("- Total score: ").append(num(dto.getTotalScore())).append('\n');
        sb.append("- Current resolution: ").append(text(dto.getCurrentResolution())).append('\n');
        sb.append("- Cost performance: ").append(text(dto.getCpLevel())).append(", ")
            .append(num(dto.getCpDisplayValue())).append(" FPS/k, ")
            .append(num(dto.getCpPercentage())).append("%\n");
        sb.append("- Bottleneck type/message: ").append(text(dto.getBottleneckType())).append(" / ")
            .append(text(dto.getBottleneckMessage())).append('\n');
        sb.append("- Bottleneck detail: ").append(text(dto.getBottleneckDetail())).append('\n');

        sb.append("\n## 3) Game FPS list\n");
        List<HardwareDto.GameBenchmarkDto> games = dto.getGames();
        if (games == null || games.isEmpty())
        {
            sb.append("- None\n");
        }
        else
        {
            for (HardwareDto.GameBenchmarkDto game : games)
            {
                sb.append("- ").append(text(game.getGameName()))
                    .append(" | Final FPS ").append(formatFps(game.getFps()))
                    .append(" | CPU FPS ").append(formatFps(game.getCpuFps()))
                    .append(" | GPU FPS ").append(formatFps(game.getGpuFps()))
                    .append(" | Note: ").append(text(game.getDescription()))
                    .append('\n');
            }
        }

        sb.append("\n## 4) Monitor recommendation (frontend)\n");
        sb.append("- Best choice: ").append(text(dto.getMonitorBestResolution()))
            .append(" / ").append(text(dto.getMonitorBestRefreshRate()))
            .append(" / ").append(text(dto.getMonitorBestReason())).append('\n');

        List<HardwareDto.MonitorOptionDto> monitorOptions = dto.getMonitorOptions();
        if (monitorOptions != null && !monitorOptions.isEmpty())
        {
            for (HardwareDto.MonitorOptionDto option : monitorOptions)
            {
                sb.append("- Option: ").append(text(option.getResolution()))
                    .append(" / ").append(text(option.getRefreshRate()))
                    .append(" / ").append(text(option.getDesc()))
                    .append(" / Best: ").append(option.getBest() != null && option.getBest() ? "yes" : "no")
                    .append('\n');
            }
        }
        return sb.toString();
    }

    private String buildExpertUserPrompt(HardwareDto dto)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("请对以下硬件配置进行专业性能评估：\n\n");
        sb.append("**硬件参数：**\n");
        sb.append("- CPU: ").append(text(dto.getCpuModel())).append(" (TDP: ").append(num(dto.getCpuTdp())).append("W)\n");
        sb.append("- GPU: ").append(text(dto.getGpuBrand())).append(' ').append(text(dto.getGpuModel())).append('\n');
        sb.append("- RAM: ").append(text(dto.getRamInterface())).append(' ')
            .append(text(dto.getRamFrequency())).append("MHz ")
            .append(text(dto.getRamCapacity())).append('\n');
        sb.append("- Motherboard: ").append(text(dto.getMoboBrand())).append(' ')
            .append(text(dto.getMoboModel())).append('\n');
        sb.append("- SSD: ").append(text(dto.getSsdFullName())).append('\n');
        sb.append("- Cooler: ").append(text(dto.getCoolerFullName())).append('\n');
        sb.append("- PSU: ").append(text(dto.getPsuBrand())).append(' ')
            .append(text(dto.getPsuWattage())).append("W\n");
        sb.append("- Benchmark Score: ").append(num(dto.getTotalScore())).append('\n');
        sb.append("- Total Price: ").append(num(dto.getTotalPrice())).append(" CNY\n");

        sb.append("\n**性能数据：**\n");
        sb.append("- 性价比等级: ").append(text(dto.getCpLevel())).append(" (")
            .append(num(dto.getCpDisplayValue())).append(" FPS/k, ")
            .append(num(dto.getCpPercentage())).append("%)\n");
        sb.append("- 瓶颈分析: ").append(text(dto.getBottleneckType())).append(" - ")
            .append(text(dto.getBottleneckMessage())).append('\n');

        sb.append("\n**游戏帧率数据：**\n");
        List<HardwareDto.GameBenchmarkDto> games = dto.getGames();
        if (games == null || games.isEmpty())
        {
            sb.append("- 暂无数据\n");
        }
        else
        {
            for (HardwareDto.GameBenchmarkDto game : games)
            {
                sb.append("- ").append(text(game.getGameName()))
                    .append(": ").append(formatFps(game.getFps())).append('\n');
            }
        }

        sb.append("\n**显示器推荐：**\n");
        sb.append("- 最佳选择: ").append(text(dto.getMonitorBestResolution())).append(" @ ")
            .append(text(dto.getMonitorBestRefreshRate())).append('\n');

        return sb.toString();
    }

    private String buildBeginnerUserPrompt(HardwareDto dto)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("帮我看看这台电脑好不好用，我不太懂参数：\n\n");
        sb.append("**配置：**\n");
        sb.append("- 处理器: ").append(text(dto.getCpuModel())).append('\n');
        sb.append("- 显卡: ").append(text(dto.getGpuBrand())).append(' ').append(text(dto.getGpuModel())).append('\n');
        sb.append("- 内存: ").append(text(dto.getRamCapacity())).append(' ')
            .append(text(dto.getRamInterface())).append('\n');
        sb.append("- 硬盘: ").append(text(dto.getSsdFullName())).append('\n');
        sb.append("- 总价: ").append(num(dto.getTotalPrice())).append(" 元\n");

        sb.append("\n**性能参考：**\n");
        sb.append("- 综合评分: ").append(num(dto.getTotalScore())).append(" 分\n");
        sb.append("- 性价比: ").append(text(dto.getCpLevel())).append('\n');

        sb.append("\n**游戏表现（帧率）：**\n");
        List<HardwareDto.GameBenchmarkDto> games = dto.getGames();
        if (games == null || games.isEmpty())
        {
            sb.append("- 暂无数据\n");
        }
        else
        {
            for (HardwareDto.GameBenchmarkDto game : games)
            {
                Map<String, Integer> fps = game.getFps();
                int fps1080 = fps != null && fps.get("1080P") != null ? fps.get("1080P") : 0;
                sb.append("- ").append(text(game.getGameName())).append(": ")
                    .append(fps1080 > 0 ? fps1080 + " 帧" : "未知").append('\n');
            }
        }

        sb.append("\n**显示器推荐：**\n");
        sb.append("- 最佳选择: ").append(text(dto.getMonitorBestResolution())).append(" @ ")
            .append(text(dto.getMonitorBestRefreshRate())).append('\n');

        return sb.toString();
    }

    private String buildHardwareSummary(HardwareDto dto)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("CPU=").append(text(dto.getCpuModel()))
            .append(";GPU=").append(text(dto.getGpuModel()))
            .append(";RAM=").append(text(dto.getRamInterface())).append(' ').append(text(dto.getRamCapacity()))
            .append(";SSD=").append(text(dto.getSsdFullName()))
            .append(";Cooler=").append(text(dto.getCoolerFullName()))
            .append(";PSU=").append(text(dto.getPsuBrand())).append(' ').append(text(dto.getPsuWattage())).append("W")
            .append(";Score=").append(num(dto.getTotalScore()))
            .append(";CP=").append(text(dto.getCpLevel())).append(' ').append(num(dto.getCpPercentage())).append("%")
            .append(";BestMonitor=").append(text(dto.getMonitorBestResolution())).append('/').append(text(dto.getMonitorBestRefreshRate()));
        return sb.toString();
    }

    private void saveLogAsync(final Long userId, final String hardwareSummary, final String aiResponse)
    {
        AsyncManager.me().execute(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    SysAiReviewLog logEntity = new SysAiReviewLog();
                    logEntity.setUserId(userId);
                    logEntity.setHardwareSummary(hardwareSummary);
                    logEntity.setAiResponse(aiResponse);
                    logEntity.setCreateTime(DateUtils.getNowDate());
                    sysAiReviewLogMapper.insertSysAiReviewLog(logEntity);
                }
                catch (Exception e)
                {
                    log.error("Failed to save AI review log", e);
                }
            }
        });
    }

    private String formatFps(Map<String, Integer> fpsMap)
    {
        if (fpsMap == null || fpsMap.isEmpty())
        {
            return "-";
        }
        return "1080P:" + num(fpsMap.get("1080P"))
            + ", 2K:" + num(fpsMap.get("2K"))
            + ", 4K:" + num(fpsMap.get("4K"));
    }

    private String text(Object value)
    {
        if (value == null)
        {
            return "-";
        }
        String str = String.valueOf(value).trim();
        return StringUtils.isEmpty(str) ? "-" : str;
    }

    private String num(Object value)
    {
        return value == null ? "-" : String.valueOf(value);
    }
}
