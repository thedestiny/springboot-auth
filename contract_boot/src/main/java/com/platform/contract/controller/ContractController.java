package com.platform.contract.controller;

import com.alibaba.fastjson.JSONObject;
import com.platform.contract.config.EsignConstant;
import com.platform.contract.config.TypeEnum;
import com.platform.contract.dto.req.CreateContractReq;
import com.platform.contract.dto.req.RevokeContractReq;
import com.platform.contract.dto.req.StartSigningReq;
import com.platform.contract.dto.res.*;
import com.platform.contract.entity.Contract;
import com.platform.contract.exception.ApiCodeEnum;
import com.platform.contract.service.ContractService;
import com.platform.contract.utils.AppHttpUtil;
import com.platform.contract.utils.AssertUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@Tag(name = "合同控制器", description = "合同创建、查询与修改")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private HttpServletRequest request;

    @Operation(summary = "创建合同")
    @PostMapping({"/create"})
    public CreateContractRes createContract(@RequestBody @Valid CreateContractReq contractReq, HttpServletRequest request) {

        String userId = AppHttpUtil.getUserId(request);
        AssertUtil.empty(userId, ApiCodeEnum.ERROR_BIZ, "没有用户信息", "createContract", userId);

        Contract contract = this.contractService.createContract(userId, TypeEnum.ACCOUNT_TYPE_1.getCode(),
                contractReq.getTemplateId(),
                contractReq.getPartyBUserId(), contractReq.getPartyBUserType());
        this.contractService.startSigning(userId, contract.getId());
        return new CreateContractRes();
    }

    @Operation(summary = "开始签署合同")
    @PostMapping({"/startSigning"})
    public StartSigningRes startSigning(@RequestBody StartSigningReq signingReq) {

        String userId = AppHttpUtil.getUserId(request);
        AssertUtil.empty(userId, ApiCodeEnum.ERROR_BIZ, "没有用户信息", "createContract", userId);

        contractService.startSigning(userId, signingReq.getId());
        return new StartSigningRes();
    }

    @Operation(summary = "刷新合同状态")
    @PostMapping({"/refresh"})
    public RefreshContractRes refreshContractStatus(@RequestBody StartSigningReq contractReq) {

        String userId = AppHttpUtil.getUserId(request);
        AssertUtil.empty(userId, ApiCodeEnum.ERROR_BIZ, "没有用户信息", "refreshContractStatus", userId);

        contractService.refreshContractStatus(userId, contractReq.getId());
        return new RefreshContractRes();
    }

    @Operation(summary = "撤销合同")
    @PostMapping({"/revoke"})
    public RevokeContractRes revokeContract(@RequestBody RevokeContractReq contractReq) {

        String userId = AppHttpUtil.getUserId(request);
        AssertUtil.empty(userId, ApiCodeEnum.ERROR_BIZ, "没有用户信息", "revokeContract", userId);

        this.contractService.revokeContract(userId, contractReq.getId());
        return new RevokeContractRes();
    }

    @Operation(summary = "合同下载,已完成的合同才能下载")
    @PostMapping({"/download"})
    public StartSigningRes downloadUrl(@RequestBody StartSigningReq downloadReq) {

        String userId = AppHttpUtil.getUserId(request);
        AssertUtil.empty(userId, ApiCodeEnum.ERROR_BIZ, "没有用户信息", "downloadUrl", userId);

        JSONObject downloadUrl = this.contractService.downloadContract(userId, downloadReq.getId());
        StartSigningRes contractDownloadResp = new StartSigningRes();
        contractDownloadResp.setDownloadInfo(downloadUrl);
        return contractDownloadResp;
    }

    @Operation(summary = "合同预览")
    @PostMapping({"/preview"})
    public StartSigningRes contractPreview(@RequestBody StartSigningReq previewReq, HttpServletRequest request) {

        String userId = AppHttpUtil.getUserId(request);
        AssertUtil.empty(userId, ApiCodeEnum.ERROR_BIZ, "没有用户信息", "contractPreview", userId);

        JSONObject downloadUrl = this.contractService.previewContract(userId, previewReq.getId());
        StartSigningRes contractPreviewResp = new StartSigningRes();
        contractPreviewResp.setDownloadInfo(downloadUrl);
        return contractPreviewResp;
    }

    @Operation(summary = "甲方乙方签署合同")
    @PostMapping({"/signContract"})
    public SignContractRes signContract(@RequestBody StartSigningReq signingReq) {

        String userId = AppHttpUtil.getUserId(request);
        AssertUtil.empty(userId, ApiCodeEnum.ERROR_BIZ, "没有用户信息", "signContract", userId);


        if (EsignConstant.PARTY_A_LIST.contains(userId)) {
            signingReq.setSignParty(TypeEnum.SIGN_PARTY_A.getCode());
        } else {
            signingReq.setSignParty(TypeEnum.SIGN_PARTY_B.getCode());
        }

        String signUrl = this.contractService.signContract(userId, signingReq.getId(), signingReq.getSignParty());
        SignContractRes signContractResp = new SignContractRes();
        signContractResp.setSignUrl(signUrl);
        return signContractResp;
    }

}
