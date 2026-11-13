package com.platform.contract.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.platform.contract.config.EsignConstant;
import com.platform.contract.config.StatusEnum;
import com.platform.contract.config.TypeEnum;
import com.platform.contract.entity.Contract;
import com.platform.contract.entity.ContractTemplate;
import com.platform.contract.entity.UserMaterial;
import com.platform.contract.esign.EsignGateway;
import com.platform.contract.exception.ApiCodeEnum;
import com.platform.contract.exception.ApiException;
import com.platform.contract.mapper.ContractMapper;
import com.platform.contract.mapper.ContractTemplateMapper;
import com.platform.contract.mapper.UserMaterialMapper;
import com.platform.contract.utils.AssertUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Slf4j
@Service
public class ContractService {

    @Resource
    private ContractMapper contractMapper;

    @Resource
    private UserMaterialMapper userMaterialMapper;

    @Resource
    private ContractTemplateMapper contractTemplateMapper;

    @Resource
    private EsignGateway esignGateway;

    public Contract createContract(String partyAUserId, String partyAUserType, Long templateId, String partyBUserId, String partyBUserType) {

        ContractTemplate templateDO = contractTemplateMapper.selectById(templateId);
        AssertUtil.empty(templateDO, ApiCodeEnum.ERROR_BIZ, "合同模版不存在");

        UserMaterial partyA = this.userMaterialMapper.selectUserMaterialByUserIdAndType(partyAUserId, partyAUserType);
        AssertUtil.empty(partyA, ApiCodeEnum.ERROR_BIZ, "甲方信息不存在");
        UserMaterial partyB = this.userMaterialMapper.selectUserMaterialByUserIdAndType(partyBUserId, partyBUserType);
        AssertUtil.empty(partyB, ApiCodeEnum.ERROR_BIZ, "乙方信息不存在");

        this.validateUserMaterial(partyA);
        this.validateUserMaterial(partyB);
        String partyAName = StringUtils.equalsIgnoreCase(partyA.getAccountType(), TypeEnum.ACCOUNT_TYPE_0.getCode()) ? partyA.getDisplayName() : partyA.getOrgName();
        String partyBName = StringUtils.equalsIgnoreCase(partyB.getAccountType(), TypeEnum.ACCOUNT_TYPE_0.getCode()) ? partyB.getDisplayName() : partyB.getOrgName();
        Contract contractDO = new Contract();
        contractDO.setTemplateId(templateId);
        contractDO.setPartyAName(partyAName);
        contractDO.setPartyAId(partyAUserId);
        contractDO.setPartyAType(partyAUserType);
        contractDO.setPartyBName(partyBName);
        contractDO.setPartyBType(partyBUserType);
        contractDO.setPartyBId(partyBUserId);
        contractDO.setCreateTime(new Date());
        contractDO.setUpdateTime(new Date());
        this.contractMapper.insert(contractDO);
        contractDO.setContractNo(generateContractNo(contractDO.getId()));
        this.contractMapper.updateById(contractDO);
        EsignGateway.FilledFileResult filledResult = this.esignGateway.createFilledFile(templateDO.getDocTemplateId(), partyAName, partyBName, contractDO.getContractNo(), partyBName + ".pdf");
        contractDO.setEsignFileId(filledResult.fileId);
        contractDO.setEsignFileUrl(filledResult.fileDownloadUrl);
        contractDO.setStatus(StatusEnum.SIGN_FLOW_STATUS_0.getCode());
        this.contractMapper.updateById(contractDO);
        return contractDO;
    }

    public void startSigning(String userId, Long id) {

        Contract contractDO = this.contractMapper.selectById(id);
        AssertUtil.empty(contractDO, ApiCodeEnum.ERROR_BIZ, "合同不存在或无权操作");
        ContractTemplate templateDO = this.contractTemplateMapper.selectById(contractDO.getTemplateId());
        AssertUtil.empty(templateDO, ApiCodeEnum.ERROR_BIZ, "请先在模板配置页完成 甲方名称/乙方名称 控件配置");
        String contractConfig = templateDO.getContractConfig();
        JSONObject jsonObject = JSONObject.parseObject(contractConfig);
        UserMaterial materialA = this.userMaterialMapper.selectUserMaterialByUserIdAndType(contractDO.getPartyAId(), contractDO.getPartyAType());
        UserMaterial materialB = this.userMaterialMapper.selectUserMaterialByUserIdAndType(contractDO.getPartyBId(), contractDO.getPartyBType());
        String flowId = this.esignGateway.startSigning(contractDO, materialA, materialB, jsonObject);
        contractDO.setEsignFlowId(flowId);
        contractDO.setStatus(StatusEnum.SIGN_FLOW_STATUS_1.getCode());
        this.contractMapper.updateById(contractDO);
    }

    public void revokeContract(String userId, Long id) {

        Contract contractDO = this.contractMapper.selectById(id);
        AssertUtil.empty(contractDO, ApiCodeEnum.ERROR_BIZ, "合同不存在或无权操作");
        if (contractDO.getEsignFlowId() != null) {
            JSONObject data = this.esignGateway.revokeContractFlow(contractDO.getEsignFlowId());
            log.info("data:{}", data);
            this.refreshContractStatus((String)null, contractDO.getId());
        }
    }

    public void refreshContractStatus(String userId, Long id) {
        Contract contractDO = this.contractMapper.selectById(id);
        AssertUtil.empty(contractDO, ApiCodeEnum.ERROR_BIZ, "合同不存在或无权操作");
        if (contractDO.getEsignFlowId() != null) {
            JSONObject data = this.esignGateway.getFlowDetail(contractDO.getEsignFlowId());
            if (data.containsKey("signFlowStatus")) {
                String flowStatus = data.getString("signFlowStatus");
                StringBuilder text = new StringBuilder();
                JSONArray signers = data.getJSONArray("signers");
                if (!CollectionUtils.isEmpty(signers)) {
                    for(int i = 0; i < signers.size(); ++i) {
                        JSONObject signer = signers.getJSONObject(i);
                        String signOrder = signer.getString("signOrder");
                        String signerType = signer.getString("signerType");
                        String signStatus = signer.getString("signStatus");
                        String name = "";
                        if (StringUtils.equals(TypeEnum.ACCOUNT_TYPE_0.getCode(), signerType)) {
                            JSONObject psnSigner = signer.getJSONObject("psnSigner");
                            name = psnSigner.getString("psnName");
                        }

                        if (StringUtils.equals(TypeEnum.ACCOUNT_TYPE_1.getCode(), signerType)) {
                            JSONObject orgSigner = signer.getJSONObject("orgSigner");
                            name = orgSigner.getString("orgName");
                        }

                        text.append("序号").append(signOrder).append(": ").append(name).append(" 签署状态：").append(StatusEnum.transSignStatusName(signStatus)).append(" \n");
                    }
                }

                contractDO.setDescription(text.toString());
                contractDO.setStatus(flowStatus);
                this.contractMapper.updateById(contractDO);
            }

        }
    }

    public String signContract(String userId, Long id, String signParty) {

        Contract contractDO = this.contractMapper.selectById(id);
        AssertUtil.empty(contractDO, ApiCodeEnum.ERROR_BIZ, "合同不存在或无权操作");
        if (contractDO.getEsignFlowId() == null) {
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "草稿合同尚未发起签署流程");
        } else if (StringUtils.equals(signParty, TypeEnum.SIGN_PARTY_A.getCode())) {
            UserMaterial material = this.userMaterialMapper.selectUserMaterialByUserIdAndType(contractDO.getPartyAId(), contractDO.getPartyAType());
            return this.esignGateway.getContractSignUrl(contractDO, material);
        } else if (StringUtils.equals(signParty, TypeEnum.SIGN_PARTY_B.getCode())) {
            UserMaterial material = this.userMaterialMapper.selectUserMaterialByUserIdAndType(contractDO.getPartyBId(), contractDO.getPartyBType());
            return this.esignGateway.getContractSignUrl(contractDO, material);
        } else {
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "未找到合同签署方");
        }
    }

    public JSONObject downloadContract(String userId, Long id) {
        Contract contractDO = this.contractMapper.selectById(id);
        AssertUtil.empty(contractDO, ApiCodeEnum.ERROR_BIZ, "合同不存在或无权操作");
        return this.esignGateway.getFileDownloadUrl(contractDO.getEsignFlowId());
    }

    public JSONObject previewContract(String userId, Long id) {
        Contract contractDO = this.contractMapper.selectById(id);
        AssertUtil.empty(contractDO, ApiCodeEnum.ERROR_BIZ, "合同不存在或无权操作");
        return this.esignGateway.getFilePreviewUrl(contractDO.getEsignFlowId(), contractDO.getEsignFileId());
    }

    private void validateUserMaterial(UserMaterial materialDO) {
        String accountType = materialDO.getAccountType();
        AssertUtil.isTrue(StringUtils.isBlank(materialDO.getDisplayName()), ApiCodeEnum.ERROR_BIZ, "名称不能为空", new Object[0]);
        AssertUtil.isTrue(StringUtils.isBlank(materialDO.getMobile()), ApiCodeEnum.ERROR_BIZ, "电话号码不能为空", new Object[0]);
        if (StringUtils.equals(accountType, TypeEnum.ACCOUNT_TYPE_1.getCode())) {
            AssertUtil.isTrue(StringUtils.isBlank(materialDO.getOrgName()), ApiCodeEnum.ERROR_BIZ, "企业名称不能为空", new Object[0]);
            AssertUtil.isTrue(StringUtils.isBlank(materialDO.getOrgIdCardNum()), ApiCodeEnum.ERROR_BIZ, "社会统一信用代码不能为空", new Object[0]);
        }

    }

    public void onEsignCallback(String flowId, String signResult) {
        if (!StringUtils.isAnyBlank(flowId, signResult)) {
            Contract contractDO = contractMapper.selectByEsignFlowId(flowId);
            if (!ObjectUtils.isEmpty(contractDO)) {
                contractDO.setStatus(signResult);
                this.contractMapper.updateById(contractDO);
            }
        }
    }

    public void refreshEsignCallback(String flowId) {
        if (!StringUtils.isAnyBlank(flowId)) {
            Contract contractDO = contractMapper.selectByEsignFlowId(flowId);
            if (!ObjectUtils.isEmpty(contractDO)) {
                this.refreshContractStatus("", contractDO.getId());
            }
        }
    }

    public void delContract(String userId, Contract contractDTO) {
        this.refreshContractStatus(userId, contractDTO.getId());
        Contract contractDO = this.contractMapper.selectById(contractDTO.getId());
        AssertUtil.empty(contractDO, ApiCodeEnum.ERROR_BIZ, "合同不存在或无权操作");
        AssertUtil.notEquals(contractDO.getStatus(), StatusEnum.SIGN_FLOW_STATUS_0.getCode(), ApiCodeEnum.ERROR_BIZ, "只有草稿状态合同才能删除!", new Object[0]);
        this.contractMapper.deleteById(contractDTO.getId());
    }

    @Async("async1")
    public void launchContractAuto(String userId) {

        UserMaterial userMaterial = new UserMaterial();
        try {
            Contract contract = this.createContract("123456", TypeEnum.ACCOUNT_TYPE_1.getCode(), EsignConstant.CONTRACT_TEMPLATE_ID, userMaterial.getUserId(), userMaterial.getAccountType());
            this.startSigning(userId, contract.getId());
        } catch (Exception e) {
            log.warn("launchContractAuto exception {}", e.getMessage(), e);
        }
    }

    private static String generateContractNo(Long id) {
        return "HT-" + DateTimeFormatter.BASIC_ISO_DATE.format(LocalDate.now()) + "-" + String.format("%05d", id);
    }
}
