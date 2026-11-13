package com.platform.contract.api;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.platform.contract.config.EsignProperties;
import com.platform.contract.config.TypeEnum;
import com.platform.contract.esign.EsignEncryption;
import com.platform.contract.service.ContractService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class EsignCallBackApi {

    @Resource
    private EsignProperties esignProperties;

    @Resource
    private ContractService contractService;

    @PostMapping({"/api/esign/callback"})
    public ResponseEntity<String> callback(@RequestHeader(value = "x-tsign-open-app-id", required = false) String appId,
                                           @RequestHeader(value = "x-tsign-open-timestamp", required = false) String timestamp,
                                           @RequestHeader(value = "x-tsign-open-signature", required = false) String signature,
                                           HttpServletRequest request,
                                           @RequestBody String body) {

        log.info("appId {} timestamp {} signature {}", appId, timestamp, signature);

        String queryString = request.getQueryString() == null ? "" : request.getQueryString();
        boolean valid = EsignEncryption.callBackCheck(timestamp, queryString, body, this.esignProperties.getAppSecret(), signature);
        log.info("callback queryString {} body: {}", queryString, body);
        if (!valid) {
            log.warn("E签宝 回调验签失败 {}", body);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"code\":401,\"message\":\"验签失败\"}");
        } else {
            try {
                JSONObject root = JSON.parseObject(body);
                if (root != null && root.containsKey("action")) {
                    String action = root.getString("action");
                    if (root.containsKey("signFlowId")) {
                        String flowId = root.getString("signFlowId");
                        this.contractService.refreshEsignCallback(flowId);
                    }

                    if (root.containsKey("flowId")) {
                        String flowId = root.getString("flowId");
                        this.contractService.refreshEsignCallback(flowId);
                    }

                    if (StringUtils.equals(TypeEnum.SIGN_FLOW_UPDATE.getCode(), action)) {
                        String flowId = root.getString("flowId");
                        this.contractService.refreshEsignCallback(flowId);
                    }

                    if (StringUtils.equals(TypeEnum.SIGN_FLOW_FINISH.getCode(), action)) {
                        String flowId = root.getString("flowId");
                        String flowStatus = root.getString("flowStatus");
                        this.contractService.onEsignCallback(flowId, flowStatus);
                    }

                    if (StringUtils.equals(TypeEnum.SIGN_FLOW_COMPLETE.getCode(), action)) {
                        String flowId = root.getString("signFlowId");
                        String flowStatus = root.getString("signFlowStatus");
                        this.contractService.onEsignCallback(flowId, flowStatus);
                    }
                }
            } catch (Exception e) {
                log.warn("处理 E签宝 回调失败: {}", e.getMessage());
            }

            return ResponseEntity.ok("{\"code\":0}");
        }
    }
}
