package com.platform.contract.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.platform.contract.dto.EsignConfigModel;
import com.platform.contract.dto.TemplateConfDto;
import com.platform.contract.dto.req.EsignConfigReq;
import com.platform.contract.dto.req.TemplateConfReq;
import com.platform.contract.dto.res.TemplateConfRes;
import com.platform.contract.dto.res.UploadTemplateRes;
import com.platform.contract.exception.ApiCodeEnum;
import com.platform.contract.exception.ApiException;
import com.platform.contract.service.TemplateService;
import com.platform.contract.utils.AppHttpUtil;
import com.platform.contract.utils.AssertUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@Slf4j
@RestController
@Tag(name = "合同模版控制器", description = "合同模版的查询与修改")
@RequestMapping({"/api/template"})
public class TemplateController {

    @Resource
    private HttpServletRequest request;

    @Resource
    private TemplateService templateService;

    @Operation(summary = "E签宝配置模版控件")
    @PostMapping({"/getEsignConfig"})
    public EsignConfigModel getEsignConfigUrl(@RequestBody @Valid EsignConfigReq req) {

        EsignConfigModel model = this.templateService.getEsignConfigUrl(req.getId());
        return model;
    }

    @Operation(summary = "上传合同模版文件")
    @PostMapping({"upload"})
    public UploadTemplateRes upload(@RequestParam String name, @RequestParam MultipartFile file) {

        String userId = AppHttpUtil.getUserId(request);
        AssertUtil.empty(userId, ApiCodeEnum.ERROR_BIZ, "没有用户信息", userId);

        try {
            this.templateService.upload(userId, name, file);
        } catch (Exception e) {
            log.warn("upload fail {}", e.getMessage(), e);
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "upload fail", e.getMessage());
        }

        return new UploadTemplateRes();
    }

    @Operation(summary = "编辑合同模版参数")
    @PostMapping({"/updateTemplateConfig"})
    public TemplateConfRes updateTemplateConfig(@RequestBody @Valid TemplateConfReq req) {

        String userId = AppHttpUtil.getUserId(request);
        AssertUtil.empty(userId, ApiCodeEnum.ERROR_BIZ, "没有用户信息", userId);
        TemplateConfDto confDto = new TemplateConfDto();
        BeanUtil.copyProperties(req, confDto);
        confDto.setId(req.getId());
        confDto.setContractConfig(JSONObject.toJSONString(req));
        templateService.updateTemplateConfig(confDto);
        return new TemplateConfRes();
    }

}
