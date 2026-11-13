package com.platform.contract.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "esign")
public class EsignProperties {

    private String appId;
    private String appSecret;
    private String host;
    private String notifyUrl;
    private Template template = new Template();
    private StraddleSeal straddleSeal = new StraddleSeal();

    @Data
    public static class Template {

        private String componentKeyA = "partyAName";
        private String componentKeyB = "partyBName";
        private String contractNo = "contractNo";

    }

    @Data
    public static class StraddleSeal {

        private boolean enabled = true;
        private int width = 60;
        private int positionX = 575;
        private int positionY = 400;
        private String positionPage = "";

    }
}
