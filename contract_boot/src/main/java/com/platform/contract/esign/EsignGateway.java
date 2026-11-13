package com.platform.contract.esign;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.platform.contract.config.EsignProperties;
import com.platform.contract.config.HeaderConstant;
import com.platform.contract.config.TypeEnum;
import com.platform.contract.entity.Contract;
import com.platform.contract.entity.UserMaterial;
import com.platform.contract.exception.ApiCodeEnum;
import com.platform.contract.exception.ApiException;
import com.platform.contract.utils.AssertUtil;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class EsignGateway {

    @Resource
    private EsignProperties esignProperties;

    @Resource
    private OkHttpClient okHttpClient;

    public FilledFileResult createFilledFile(String docTemplateId, String partyAName, String partyBName, String contractNo, String fileName) {

        JSONObject req = new JSONObject();
        req.put("docTemplateId", docTemplateId);
        req.put("fileName", fileName);
        JSONArray components = new JSONArray();
        components.add(this.buildComponent(this.esignProperties.getTemplate().getComponentKeyA(), partyAName));
        components.add(this.buildComponent(this.esignProperties.getTemplate().getComponentKeyB(), partyBName));
        if (StringUtils.isNoneBlank(contractNo)) {
            components.add(this.buildComponent(this.esignProperties.getTemplate().getContractNo(), contractNo));
        }

        req.put("components", components);
        // 根据模版创建合同
        JSONObject root = this.requestJson("POST", "/v3/files/create-by-doc-template", JSON.toJSONString(req));
        JSONObject data = this.dataOf(root, "/v3/files/create-by-doc-template");
        String fileId = data.getString("fileId");
        this.waitForFileReady(fileId);
        String fileDownloadUrl = data.getString("fileDownloadUrl");
        return new FilledFileResult(fileId, fileDownloadUrl);
    }

    public String startSigning(Contract contract, UserMaterial ma, UserMaterial mb, JSONObject contractConfig) {

        AssertUtil.empty(contract.getEsignFileId(), ApiCodeEnum.ERROR_BIZ, "合同文件尚未生成，无法发起签署");

        JSONObject req = new JSONObject();
        JSONArray docs = new JSONArray();
        JSONObject doc = new JSONObject();
        doc.put("fileId", contract.getEsignFileId());
        doc.put("fileName", contract.getPartyBName() + ".pdf");
        docs.add(doc);
        req.put("docs", docs);
        JSONObject flowConfig = new JSONObject();
        flowConfig.put("signFlowTitle", contract.getPartyAName() + "-" + contract.getPartyBName() + "合同");
        flowConfig.put("autoFinish", true);
        JSONObject charge = new JSONObject();
        charge.put("chargeMode", 0);
        flowConfig.put("chargeConfig", charge);
        if (StringUtils.isNoneBlank(this.esignProperties.getNotifyUrl())) {
            flowConfig.put("notifyUrl", this.esignProperties.getNotifyUrl() + "/arcee/api/esign/callback");
        }

        req.put("signFlowConfig", flowConfig);
        JSONArray signers = new JSONArray();
        signers.add(this.buildPartyASigner(contract, ma, contractConfig, 2, Boolean.TRUE));
        signers.add(this.buildPartyBSigner(contract, mb, contractConfig, 1, Boolean.FALSE));
        req.put("signers", signers);
        JSONObject root = this.requestJson("POST", "/v3/sign-flow/create-by-file", JSON.toJSONString(req));
        JSONObject data = this.dataOf(root, "/v3/sign-flow/create-by-file");
        String flowId = data.getString("signFlowId");
        String flowStartUrl = String.format("/v3/sign-flow/%s/start", flowId);
        this.requestJson("POST", flowStartUrl, "{}");
        return flowId;
    }

    private JSONObject buildPartyASigner(Contract contract, UserMaterial material, JSONObject contractConfig, Integer signOrder, Boolean autoSign) {
        JSONObject signer = new JSONObject();
        signer.put("noticeConfig", this.buildNoticeConfig());
        if (StringUtils.equalsIgnoreCase(material.getAccountType(), TypeEnum.ACCOUNT_TYPE_0.getCode())) {
            JSONObject psnSignerInfo = new JSONObject();
            psnSignerInfo.put("psnAccount", material.getMobile());
            JSONObject psnInfo = new JSONObject();
            psnInfo.put("psnName", material.getDisplayName());
            psnSignerInfo.put("psnInfo", psnInfo);
            signer.put("psnSignerInfo", psnSignerInfo);
        }

        if (StringUtils.equalsIgnoreCase(material.getAccountType(), TypeEnum.ACCOUNT_TYPE_1.getCode())) {
            JSONObject orgSignerInfo = new JSONObject();
            if (autoSign) {
                orgSignerInfo.put("orgId", material.getOrgId());
            } else {
                orgSignerInfo.put("orgName", material.getOrgName());
            }

            JSONObject orgInfo = new JSONObject();
            orgInfo.put("orgIDCardNum", material.getOrgIdCardNum());
            orgInfo.put("orgIDCardType", "CRED_ORG_USCC");
            orgInfo.put("legalRepName", material.getLegalRepName());
            orgInfo.put("legalRepIDCardNum", material.getLegalRepIdCardNum());
            orgInfo.put("legalRepIDCardType", "CRED_PSN_CH_IDCARD");
            orgSignerInfo.put("orgInfo", orgInfo);
            JSONObject transactor = new JSONObject();
            transactor.put("psnAccount", material.getMobile());
            JSONObject psnInfo = new JSONObject();
            psnInfo.put("psnName", material.getDisplayName());
            transactor.put("psnInfo", psnInfo);
            if (!autoSign) {
                orgSignerInfo.put("transactorInfo", transactor);
            }

            signer.put("orgSignerInfo", orgSignerInfo);
        }

        JSONObject signConfig = new JSONObject();
        signConfig.put("signOrder", signOrder);
        signer.put("signConfig", signConfig);
        JSONArray signFields = new JSONArray();
        signFields.add(this.buildSignField(contract.getEsignFileId(), autoSign, "contract-" + contract.getId() + "-" + signOrder, contractConfig.getString("partyAX"), contractConfig.getString("partyAY"), contractConfig.getString("positionPage")));
        EsignProperties.StraddleSeal straddleSeal = this.esignProperties.getStraddleSeal();
        if (straddleSeal != null && straddleSeal.isEnabled()) {
            String pageRange = this.resolveStraddlePageRange(straddleSeal, contract.getEsignFileId());
            if (pageRange != null) {
                signFields.add(this.buildStraddleSignField(contract.getEsignFileId(), "contract-" + contract.getId() + "-rider", straddleSeal, pageRange, autoSign));
            }
        }

        signer.put("signFields", signFields);
        signer.put("signerType", material.getAccountType());
        return signer;
    }

    private JSONObject buildStraddleSignField(String fileId, String customBizNum, EsignProperties.StraddleSeal cfg, String pageRange, Boolean autoSign) {
        JSONObject field = new JSONObject();
        field.put("customBizNum", customBizNum);
        field.put("fileId", fileId);
        JSONObject config = new JSONObject();
        config.put("assignedSealId", "");
        config.put("autoSign", autoSign);
        config.put("signFieldStyle", "2");
        config.put("freeMode", false);
        JSONObject position = new JSONObject();
        position.put("positionPage", pageRange);
        position.put("positionX", cfg.getPositionX());
        position.put("positionY", cfg.getPositionY());
        config.put("signFieldPosition", position);
        field.put("normalSignFieldConfig", config);
        return field;
    }

    private String resolveStraddlePageRange(EsignProperties.StraddleSeal cfg, String fileId) {
        String configured = cfg.getPositionPage();
        if (org.springframework.util.StringUtils.hasText(configured) && !"ALL".equalsIgnoreCase(configured.trim())) {
            return configured.trim();
        } else {
            JSONObject root = this.requestJson("GET", "/v3/files/" + fileId, "{}");
            JSONObject data = root != null && root.containsKey("data") && root.get("data") != null ? root.getJSONObject("data") : new JSONObject();
            if (data.containsKey("fileTotalPageCount") && data.get("fileTotalPageCount") != null) {
                int pages = data.getIntValue("fileTotalPageCount");
                if (pages < 2) {
                    return null;
                } else {
                    List<String> pageList = new ArrayList(pages);

                    for (int i = 1; i <= pages; ++i) {
                        pageList.add(String.valueOf(i));
                    }

                    return String.join(",", pageList);
                }
            } else {
                throw new ApiException(ApiCodeEnum.ERROR_BIZ, "配置参数为空", new Object[0]);
            }
        }
    }

    private JSONObject buildPartyBSigner(Contract contract, UserMaterial material, JSONObject contractConfig, Integer signOrder, Boolean autoSign) {
        JSONObject signer = new JSONObject();
        signer.put("noticeConfig", this.buildNoticeConfig());
        if (StringUtils.equalsIgnoreCase(material.getAccountType(), TypeEnum.ACCOUNT_TYPE_0.getCode())) {
            JSONObject psnSignerInfo = new JSONObject();
            psnSignerInfo.put("psnAccount", material.getMobile());
            JSONObject psnInfo = new JSONObject();
            psnInfo.put("psnName", material.getDisplayName());
            psnSignerInfo.put("psnInfo", psnInfo);
            signer.put("psnSignerInfo", psnSignerInfo);
        }

        if (StringUtils.equalsIgnoreCase(material.getAccountType(), TypeEnum.ACCOUNT_TYPE_1.getCode())) {
            JSONObject orgSignerInfo = new JSONObject();
            orgSignerInfo.put("orgName", material.getOrgName());
            JSONObject orgInfo = new JSONObject();
            orgInfo.put("orgIDCardNum", material.getOrgIdCardNum());
            orgInfo.put("orgIDCardType", "CRED_ORG_USCC");
            orgInfo.put("legalRepName", material.getLegalRepName());
            orgInfo.put("legalRepIDCardNum", material.getLegalRepIdCardNum());
            orgInfo.put("legalRepIDCardType", "CRED_PSN_CH_IDCARD");
            orgSignerInfo.put("orgInfo", orgInfo);
            JSONObject transactor = new JSONObject();
            transactor.put("psnAccount", material.getMobile());
            JSONObject psnInfo = new JSONObject();
            psnInfo.put("psnName", material.getDisplayName());
            transactor.put("psnInfo", psnInfo);
            if (!autoSign) {
                orgSignerInfo.put("transactorInfo", transactor);
            }

            signer.put("orgSignerInfo", orgSignerInfo);
        }

        JSONObject signConfig = new JSONObject();
        signConfig.put("signOrder", signOrder);
        signer.put("signConfig", signConfig);
        JSONArray signFields = new JSONArray();
        signFields.add(this.buildSignField(contract.getEsignFileId(), autoSign, "contract-" + contract.getId() + "-" + signOrder, contractConfig.getString("partyBX"), contractConfig.getString("partyBY"), contractConfig.getString("positionPage")));
        signer.put("signFields", signFields);
        signer.put("signerType", material.getAccountType());
        return signer;
    }

    public JSONObject getFlowDetail(String signFlowId) {
        String flowDetailUrl = String.format("/v3/sign-flow/%s/detail", signFlowId);
        JSONObject jsonObject = this.requestJson("GET", flowDetailUrl, "{}");
        return this.dataOf(jsonObject, flowDetailUrl);
    }

    public JSONObject revokeContractFlow(String signFlowId) {
        String revokeFlowUrl = String.format("/v3/sign-flow/%s/revoke", signFlowId);
        JSONObject jsonObject = this.requestJson("POST", revokeFlowUrl, "{}");
        return this.dataOf(jsonObject, revokeFlowUrl);
    }

    public JSONObject enterpriseIdentifyInfo(String orgName) {
        String identityInfoUrl = "/v3/organizations/identity-info?orgName=" + orgName;
        JSONObject jsonObject = this.requestJson("GET", identityInfoUrl, "{}");
        return this.dataOf(jsonObject, identityInfoUrl);
    }

    public JSONObject getFileDownloadUrl(String signFlowId) {
        String flowDownloadUrl = String.format("/v3/sign-flow/%s/file-download-url", signFlowId);
        JSONObject root = this.requestJson("GET", flowDownloadUrl, "{}");
        return this.dataOf(root, flowDownloadUrl);
    }

    public JSONObject getFilePreviewUrl(String signFlowId, String docFileId) {
        String flowPreviewUrl = String.format("/v3/sign-flow/%s/preview-file-download-url", signFlowId);
        JSONObject root = this.requestJson("GET", flowPreviewUrl + "?docFileId=" + docFileId, "{}");
        return this.dataOf(root, flowPreviewUrl);
    }

    public String getContractSignUrl(Contract contractDO, UserMaterial material) {
        JSONObject req = new JSONObject();
        req.put("clientType", "ALL");
        req.put("needLogin", true);
        JSONObject operator = new JSONObject();
        operator.put("psnAccount", material.getMobile());
        req.put("operator", operator);
        if (StringUtils.equalsIgnoreCase(material.getAccountType(), TypeEnum.ACCOUNT_TYPE_1.getCode())) {
            JSONObject organization = new JSONObject();
            organization.put("orgName", material.getOrgName());
            req.put("organization", organization);
        }

        req.put("urlType", 1);
        String signUrl = String.format("/v3/sign-flow/%s/sign-url", contractDO.getEsignFlowId());
        JSONObject root = this.requestJson("POST", signUrl, JSON.toJSONString(req));
        JSONObject data = this.dataOf(root, signUrl);
        String url = data.getString("url");
        String shortUrl = data.getString("shortUrl");
        return StringUtils.isEmpty(url) ? shortUrl : url;
    }

    public DocTemplateResult docTemplateCreateUrl(String fileId, String templateName) {

        JSONObject req = new JSONObject();
        req.put("docTemplateName", templateName);
        req.put("docTemplateType", 0);
        req.put("fileId", fileId);
        if (StringUtils.isNotBlank(this.esignProperties.getNotifyUrl())) {
            req.put("redirectUrl", this.esignProperties.getNotifyUrl() + "/promoters/contracts");
        }

        JSONObject root = this.requestJson("POST", "/v3/doc-templates/doc-template-create-url", JSON.toJSONString(req));
        JSONObject data = this.dataOf(root, "/v3/doc-templates/doc-template-create-url");
        String docTemplateCreateUrl = data.getString("docTemplateCreateUrl");
        String docTemplateId = data.getString("docTemplateId");
        return new DocTemplateResult(docTemplateCreateUrl, docTemplateId);
    }

    public String docTemplateEditUrl(String docTemplateId) {
        JSONObject req = new JSONObject();
        if (StringUtils.isNotBlank(this.esignProperties.getNotifyUrl())) {
            req.put("redirectUrl", this.esignProperties.getNotifyUrl() + "/promoters/contracts");
        }

        String editUrl = String.format("/v3/doc-templates/%s/doc-template-edit-url", docTemplateId);
        JSONObject root = this.requestJson("POST", editUrl, JSON.toJSONString(req));
        JSONObject data = this.dataOf(root, editUrl);
        String docTemplateEditUrl = data.getString("docTemplateEditUrl");
        return docTemplateEditUrl;
    }

    public String uploadTemplateToEsign(Path localPath, String fileName) {
        try {
            EsignFileBean bean = new EsignFileBean(localPath.toFile().getAbsolutePath());
            JSONObject req = new JSONObject();
            req.put("contentMd5", bean.getFileContentMD5());
            req.put("fileName", fileName);
            req.put("fileSize", bean.getFileSize());
            req.put("convertToPDF", false);
            req.put("contentType", HeaderConstant.CONTENTTYPE_STREAM.VALUE());
            JSONObject root = this.requestJson("POST", "/v3/files/file-upload-url", JSON.toJSONString(req));
            JSONObject data = root.getJSONObject("data");
            String fileId = data.getString("fileId");
            String fileUploadUrl = data.getString("fileUploadUrl");
            this.doUploadHttp(fileUploadUrl, "PUT", bean.getFileBytes(), bean.getFileContentMD5(), HeaderConstant.CONTENTTYPE_STREAM.VALUE());
            this.waitForFileReady(fileId);
            return fileId;
        } catch (Exception e) {
            log.warn("uploadTemplateToEsign upload failure {}", e.getMessage(), e);
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "文件上传失败: " + e.getMessage(), new Object[0]);
        }
    }

    public void waitForFileReady(String fileId) {
        for (int i = 0; i < 15; ++i) {
            JSONObject root = this.requestJson("GET", "/v3/files/" + fileId, (String) null);
            JSONObject data = root.containsKey("data") && root.get("data") != null ? root.getJSONObject("data") : new JSONObject();
            String status = data.containsKey("fileStatus") && data.get("fileStatus") != null ? data.getString("fileStatus") : "";
            if ("2".equalsIgnoreCase(status) || "5".equalsIgnoreCase(status)) {
                return;
            }

            try {
                Thread.sleep(2000L);
            } catch (InterruptedException var7) {
                Thread.currentThread().interrupt();
                throw new ApiException(ApiCodeEnum.ERROR_BIZ, "文件处理超时", new Object[]{"fileStatus not ready for fileId=" + fileId});
            }
        }

        throw new ApiException(ApiCodeEnum.ERROR_BIZ, "文件处理超时", new Object[]{"fileStatus not ready for fileId=" + fileId});
    }

    public void doUploadHttp(String uploadUrl, String method, byte[] param, String fileContentMd5, String contentType) {
        MediaType mediaType = MediaType.parse(contentType);
        RequestBody body = RequestBody.create(mediaType, param);
        Request request = (new Request.Builder()).url(uploadUrl).put(body).header("Content-Type", contentType).header("Content-MD5", fileContentMd5).build();

        HttpRequest post = HttpUtil.createPost(uploadUrl);
        post.header("Content-Type", contentType);
        post.header("Content-MD5", fileContentMd5);
        post.body(param);

        this.execute(request);
    }

    public String execute(Request request) {

        Response response = null;

        String resp = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (Objects.isNull(responseBody)) {
                    log.warn("response is null, req is [{}]", request);
                    return resp;
                }
                return responseBody.string();
            }

            log.warn("response is {} for [{}], errorBody [{}]", new Object[]{response, request, this.errorBody(response)});
        } catch (Exception e) {
            log.error("execute error [{}][{}]", new Object[]{request, e.getMessage(), e});
            return null;
        } finally {
            if (response != null) {
                response.close();
            }

        }

        return null;
    }

    private String errorBody(Response response) {
        if (Objects.isNull(response)) {
            return null;
        } else {
            try {
                ResponseBody body = response.body();
                return Objects.isNull(body) ? null : body.string();
            } catch (Exception e) {
                log.warn("read errorBody fail", e);
                return null;
            }
        }
    }

    public JSONObject requestJson(String method, String apiPath, String bodyJson) {

        String httpMethod = method.toUpperCase();

        try {
            Map<String, String> headerMap = EsignHttpHelper.signAndBuildSignAndJsonHeader(httpMethod, this.esignProperties.getAppId(), this.esignProperties.getAppSecret(), bodyJson, apiPath);
            String url = this.esignProperties.getHost() + apiPath;
            log.info("requestJson method {} url {} request: {}", new Object[]{httpMethod, url, bodyJson});
            String response = null;
            if (StringUtils.equalsIgnoreCase(httpMethod, "GET")) {
                HttpRequest request = HttpUtil.createGet(url);
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    request.header(entry.getKey(), entry.getValue());
                }
                response = request.execute().body();
            }

            if (StringUtils.equalsIgnoreCase(httpMethod, "POST")) {
                HttpRequest request = HttpUtil.createPost(url);
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    request.header(entry.getKey(), entry.getValue());
                }
                request.body(bodyJson);
                response = request.execute().body();
            }

            log.info("requestJson method {} path {} response: {}", httpMethod, apiPath, response);
            if (StringUtils.isBlank(response)) {
                throw new ApiException(ApiCodeEnum.ERROR_BIZ, "E签宝响应为空", apiPath);
            } else {
                JSONObject root = JSON.parseObject(response);
                int errCode = 0;
                if (root.containsKey("errCode") && root.get("errCode") != null) {
                    errCode = root.getIntValue("errCode");
                } else if (root.containsKey("code") && root.get("code") != null) {
                    errCode = root.getIntValue("code");
                }

                if (errCode == 0) {
                    return root;
                } else {
                    String errMsg = root.containsKey("errMsg") && root.get("errMsg") != null ? root.getString("errMsg") : (root.containsKey("message") && root.get("message") != null ? root.getString("message") : "unknown");
                    throw new ApiException(ApiCodeEnum.ERROR_BIZ, "E签宝响应报错", new Object[]{apiPath, errMsg});
                }
            }
        } catch (Exception e) {
            log.warn("requestJson method {} error {}", new Object[]{httpMethod, e.getMessage(), e});
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "E签宝请求失败", new Object[]{apiPath, e.getMessage()});
        }
    }

    private JSONObject buildComponent(String componentKey, String componentValue) {
        JSONObject component = new JSONObject();
        component.put("componentId", "");
        component.put("componentKey", componentKey);
        component.put("componentValue", componentValue);
        return component;
    }

    private JSONObject buildNoticeConfig() {
        JSONObject notice = new JSONObject();
        notice.put("noticeTypes", "1");
        return notice;
    }

    private JSONObject dataOf(JSONObject root, String apiPath) {
        if (root != null && root.containsKey("data") && root.get("data") != null) {
            return root.getJSONObject("data");
        } else {
            int errCode = root != null && root.containsKey("errCode") && root.get("errCode") != null ? root.getIntValue("errCode") : -1;
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, apiPath, new Object[]{errCode, "响应缺少 data 节点: " + (root == null ? "null" : JSON.toJSONString(root))});
        }
    }

    private JSONObject buildSignField(String fileId, Boolean autoSign, String customBizNum, String positionX, String positionY, String positionPage) {
        JSONObject field = new JSONObject();
        field.put("customBizNum", customBizNum);
        field.put("fileId", fileId);
        JSONObject signDateConfig = new JSONObject();
        if (autoSign) {
            signDateConfig.put("showSignDate", "1");
        } else {
            signDateConfig.put("showSignDate", "2");
        }

        field.put("signDateConfig", signDateConfig);
        JSONObject config = new JSONObject();
        config.put("assignedSealId", "");
        config.put("autoSign", autoSign);
        config.put("freeMode", false);
        config.put("movableSignField", false);
        config.put("orgSealBizTypes", "ALL");
        config.put("psnSealStyles", "0,1");
        config.put("signFieldStyle", 1);
        JSONObject position = new JSONObject();
        position.put("positionPage", positionPage);
        position.put("positionX", positionX);
        position.put("positionY", positionY);
        config.put("signFieldPosition", position);
        field.put("normalSignFieldConfig", config);
        field.put("signFieldType", 0);
        return field;
    }

    @Data
    public static class DocTemplateResult {

        public final String createUrl;
        public final String docTemplateId;

        public DocTemplateResult(String createUrl, String docTemplateId) {
            this.createUrl = createUrl;
            this.docTemplateId = docTemplateId;
        }
    }

    @Data
    public static class FilledFileResult {

        public final String fileId;
        public final String fileDownloadUrl;

        public FilledFileResult(String fileId, String fileDownloadUrl) {
            this.fileId = fileId;
            this.fileDownloadUrl = fileDownloadUrl;
        }
    }
}
