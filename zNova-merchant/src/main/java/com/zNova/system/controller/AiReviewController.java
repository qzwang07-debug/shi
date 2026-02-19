package com.zNova.system.controller;

import com.zNova.common.annotation.Anonymous;
import com.zNova.common.core.controller.BaseController;
import com.zNova.common.core.domain.AjaxResult;
import com.zNova.common.utils.StringUtils;
import com.zNova.system.domain.HardwareDto;
import com.zNova.system.service.AiReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Anonymous
@RestController
@RequestMapping("/system/ai")
public class AiReviewController extends BaseController
{
    @Autowired
    private AiReviewService aiReviewService;

    @PostMapping("/review")
    public AjaxResult review(@RequestBody HardwareDto dto)
    {
        if (dto == null || StringUtils.isEmpty(dto.getCpuModel()) || StringUtils.isEmpty(dto.getGpuModel()))
        {
            return AjaxResult.error("CPU and GPU are required.");
        }
        String content = aiReviewService.generateReview(dto);
        Map<String, Object> data = new HashMap<>();
        data.put("content", content);
        return AjaxResult.success(data);
    }
}
