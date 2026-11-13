package com.platform.contract.config;


import com.google.common.collect.Lists;

import java.util.List;

public class EsignConstant {

    // http 请求方法
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
    public static final String HTTP_PUT = "PUT";
    public static final String HTTP_DELETE = "DELETE";

    // 甲方列表
    public static final List<String> PARTY_A_LIST = Lists.newArrayList("34567");

    // 合同模版
    public static final Long CONTRACT_TEMPLATE_ID = 7L;
    // 创建合同模版
    public static final String DOC_TEMPLATE_CREATE_URL = "/v3/doc-templates/doc-template-create-url";
    // 编辑合同模版
    public static final String DOC_TEMPLATE_EDIT_URL = "/v3/doc-templates/%s/doc-template-edit-url";
    // 根据合同模版创建合同
    public static final String CREATE_BY_DOC_TEMPLATE = "/v3/files/create-by-doc-template";
    // 上传合同模版
    public static final String FILE_UPLOAD_URL = "/v3/files/file-upload-url";
    // 开始签订合同
    public static final String CREATE_BY_FILE = "/v3/sign-flow/create-by-file";
    // 开启流程
    public static final String CONTRACT_START = "/v3/sign-flow/%s/start";
    // 合同详情
    public static final String CONTRACT_DETAIL = "/v3/sign-flow/%s/detail";
    // 撤销合同
    public static final String CONTRACT_REVOKE = "/v3/sign-flow/%s/revoke";
    // 下载签订完成的合同
    public static final String CONTRACT_DOWNLOAD = "/v3/sign-flow/%s/file-download-url";
    // 预览签署中的合同
    public static final String CONTRACT_PREVIEW = "/v3/sign-flow/%s/preview-file-download-url";
    // 开始签署
    public static final String CONTRACT_SIGN_URL = "/v3/sign-flow/%s/sign-url";
    // 消息回调
    public static final String SIGN_CALLBACK = "/api/esign/callback";
    // 认证信息
    public static final String IDENTITY_INFO = "/v3/organizations/identity-info";
}
