-- ============================================================
-- DDL generated from entity classes (MySQL)
-- Contract         -> t_contract
-- UserMaterial     -> t_user_material
-- ContractTemplate -> t_contract_template
-- ============================================================

DROP TABLE IF EXISTS t_contract;
CREATE TABLE t_contract
(
    id             BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    template_id    BIGINT       DEFAULT NULL COMMENT '合同模板ID',
    party_a_name   VARCHAR(100) DEFAULT NULL COMMENT '甲方名称',
    party_a_type   VARCHAR(32)  DEFAULT NULL COMMENT '甲方类型',
    party_a_id     VARCHAR(64)  DEFAULT NULL COMMENT '甲方证件号/用户ID',
    party_b_name   VARCHAR(100) DEFAULT NULL COMMENT '乙方名称',
    party_b_type   VARCHAR(32)  DEFAULT NULL COMMENT '乙方类型',
    party_b_id     VARCHAR(64)  DEFAULT NULL COMMENT '乙方证件号/用户ID',
    status         VARCHAR(32)  DEFAULT NULL COMMENT '合同状态',
    description    VARCHAR(500) DEFAULT NULL COMMENT '合同描述',
    esign_flow_id  VARCHAR(128) DEFAULT NULL COMMENT 'e签宝签署流程ID',
    esign_file_id  VARCHAR(128) DEFAULT NULL COMMENT 'e签宝文件ID',
    esign_file_url VARCHAR(500) DEFAULT NULL COMMENT 'e签宝文件地址',
    contract_no    VARCHAR(64)  DEFAULT NULL COMMENT '合同编号',
    contract_name  VARCHAR(200) DEFAULT NULL COMMENT '合同名称',
    create_by      VARCHAR(64)  DEFAULT NULL COMMENT '创建人',
    create_time    DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_by      VARCHAR(64)  DEFAULT NULL COMMENT '更新人',
    update_time    DATETIME     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_contract_template_id (template_id),
    KEY idx_contract_no (contract_no),
    KEY idx_contract_esign_flow_id (esign_flow_id)
) ENGINE = InnoDB COMMENT ='合同表';

DROP TABLE IF EXISTS t_user_material;
CREATE TABLE t_user_material
(
    id                   BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id              VARCHAR(64)  DEFAULT NULL COMMENT '用户ID',
    account_type         VARCHAR(32)  DEFAULT NULL COMMENT '账户类型',
    display_name         VARCHAR(100) DEFAULT NULL COMMENT '显示名称',
    mobile               VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    org_name             VARCHAR(200) DEFAULT NULL COMMENT '机构名称',
    org_id_card_num      VARCHAR(64)  DEFAULT NULL COMMENT '机构证件号',
    legal_rep_name       VARCHAR(100) DEFAULT NULL COMMENT '法定代表人姓名',
    legal_rep_id_card_num VARCHAR(64) DEFAULT NULL COMMENT '法定代表人证件号',
    create_by            VARCHAR(64)  DEFAULT NULL COMMENT '创建人',
    create_time          DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_by            VARCHAR(64)  DEFAULT NULL COMMENT '更新人',
    update_time          DATETIME     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_material_user_id (user_id)
) ENGINE = InnoDB COMMENT ='用户材料表';

DROP TABLE IF EXISTS t_contract_template;
CREATE TABLE t_contract_template
(
    id              BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    storage_path    VARCHAR(500) DEFAULT NULL COMMENT '模板存储路径',
    name            VARCHAR(200) DEFAULT NULL COMMENT '模板名称',
    user_id         VARCHAR(64)  DEFAULT NULL COMMENT '用户ID',
    esign_file_id   VARCHAR(128) DEFAULT NULL COMMENT 'e签宝文件ID',
    doc_template_id VARCHAR(128) DEFAULT NULL COMMENT 'e签宝文档模板ID',
    contract_config TEXT COMMENT '合同模板配置(JSON)',
    create_by       VARCHAR(64)  DEFAULT NULL COMMENT '创建人',
    create_time     DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_by       VARCHAR(64)  DEFAULT NULL COMMENT '更新人',
    update_time     DATETIME     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_contract_template_user_id (user_id),
    KEY idx_contract_template_esign_file_id (esign_file_id)
) ENGINE = InnoDB COMMENT ='合同模板表';
