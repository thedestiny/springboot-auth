package com.platform.contract.service;

import com.platform.contract.dto.EsignConfigModel;
import com.platform.contract.dto.TemplateConfDto;
import com.platform.contract.entity.ContractTemplate;
import com.platform.contract.esign.EsignGateway;
import com.platform.contract.exception.ApiCodeEnum;
import com.platform.contract.mapper.ContractTemplateMapper;
import com.platform.contract.utils.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class TemplateService {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Autowired
    private ContractTemplateMapper contractTemplateMapper;

    @Autowired
    private EsignGateway esignGateway;

    public ContractTemplate upload(String userId, String name, MultipartFile file) throws Exception {
        // 上传合同模版

        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        } else {
            String originalName = file.getOriginalFilename();
            String extension = originalName == null ? "" : originalName.toLowerCase();
            if (!extension.endsWith(".pdf")) {
                throw new IllegalArgumentException("仅支持 PDF 或 DOCX 合同模板");
            } else {
                Path path = Paths.get(this.uploadDir).toAbsolutePath().normalize();
                Files.createDirectories(path);
                String storedName = UUID.randomUUID().toString() + (extension.endsWith(".pdf") ? ".pdf" : ".docx");
                Path target = path.resolve(storedName).normalize();
                if (!target.startsWith(path)) {
                    throw new IllegalArgumentException("无效文件名");
                } else {
                    file.transferTo(target.toFile());

                    try {
                        String esignFileId = this.esignGateway.uploadTemplateToEsign(target, originalName);
                        ContractTemplate template = new ContractTemplate();
                        template.setName(name);
                        template.setStoragePath(target.toString());
                        template.setUserId(userId);
                        template.setEsignFileId(esignFileId);
                        template.setUpdateTime(new Date());
                        template.setCreateTime(new Date());
                        contractTemplateMapper.insert(template);
                        return template;
                    } catch (Exception exception) {
                        Files.deleteIfExists(target);
                        throw exception;
                    }
                }
            }
        }
    }


    public EsignConfigModel getEsignConfigUrl(Long id) {

        ContractTemplate templateDO = contractTemplateMapper.selectById(id);
        AssertUtil.empty(templateDO, ApiCodeEnum.ERROR_BIZ, "合同模版不存在", id);

        if (StringUtils.isBlank(templateDO.getDocTemplateId())) {

            EsignGateway.DocTemplateResult docTemplateResult = this.esignGateway.docTemplateCreateUrl(templateDO.getEsignFileId(), templateDO.getName());
            templateDO.setDocTemplateId(docTemplateResult.docTemplateId);
            templateDO.setUpdateTime(new Date());
            contractTemplateMapper.updateById(templateDO);

            EsignConfigModel model = new EsignConfigModel();
            model.setCreateUrl(docTemplateResult.createUrl);
            model.setDocTemplateId(docTemplateResult.docTemplateId);
            model.setAlreadyConfigured(false);
            return model;
        } else {
            String editUrl = this.esignGateway.docTemplateEditUrl(templateDO.getDocTemplateId());
            EsignConfigModel model = new EsignConfigModel();
            model.setEditUrl(editUrl);
            model.setDocTemplateId(templateDO.getDocTemplateId());
            model.setAlreadyConfigured(true);
            return model;
        }
    }

    public void updateTemplateConfig(TemplateConfDto confDto) {

        ContractTemplate templateDO = contractTemplateMapper.selectById(confDto.getId());
        AssertUtil.empty(templateDO, ApiCodeEnum.ERROR_BIZ, "合同模版不存在", confDto.getId());


        ContractTemplate newTemplateDO = new ContractTemplate();
        newTemplateDO.setId(confDto.getId());
        newTemplateDO.setContractConfig(confDto.getContractConfig());
        newTemplateDO.setUpdateTime(new Date());
        this.contractTemplateMapper.updateById(newTemplateDO);
    }
}
