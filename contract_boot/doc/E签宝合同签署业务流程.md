# 基于 Spring Boot 4 + E签宝的电子合同签署系统实战

> 一个完整的电子合同签署平台该怎么做？本文围绕 **contract_boot** 项目，拆解「用户资料维护 → 合同模板 → 创建合同并发起签署 → E签宝回调」四段核心业务流程，带你跑通 E签宝开放平台 V3 API 的完整接入链路。

## 一、项目背景与技术栈

contract_boot 是一套面向 B 端的电子合同签署系统，对接 E签宝（eSign）开放平台，支持个人与企业两类签署方，覆盖合同模板上传、控件配置、合同生成、发起签署、签署回调、合同下载预览全流程。

**技术选型**：

| 组件 | 版本 | 作用 |
|---|---|---|
| JDK | 25 | 运行环境 |
| Spring Boot | 4.0.6 | Web 框架（starter-webmvc） |
| MyBatis-Plus | 3.5.18 | ORM（spring-boot4-starter） |
| Hutool | 5.8.48 | HTTP 工具与加解密 |
| OkHttp | 5.3.2 | 文件上传 |
| springdoc-openapi | 2.8.9 | Swagger UI 接口文档 |
| fastjson | 1.2.83 | JSON 序列化 |

**三张核心表**：

- `t_user_material` —— 用户材料表，区分个人（account_type=0）/ 公司（account_type=1）
- `t_contract_template` —— 合同模板表，存储 E签宝文件 ID、文档模板 ID、签署控件坐标配置
- `t_contract` —— 合同表，记录甲乙方信息、签署流程 ID、合同状态

## 二、业务流程一：维护用户资料

签署合同的前提是甲乙双方都已在平台登记资料。系统按账户类型区分两类信息：

**个人账户（account_type = 0）**：只需 `displayName`（姓名）+ `mobile`（手机号）。手机号即 E签宝的个人账号标识，用于后续发起签署时定位签署人。

**企业账户（account_type = 1）**：在个人信息基础上增加企业资质字段：

- `orgName` —— 公司名称
- `orgIdCardNum` —— 统一社会信用代码（E签宝证件类型固定为 `CRED_ORG_USCC`）
- `legalRepName` —— 法定代表人姓名
- `legalRepIdCardNum` —— 法定代表人证件号（类型 `CRED_PSN_CH_IDCARD`）
- 经办人信息复用 `displayName` + `mobile`

资料校验逻辑集中在 `ContractService.validateUserMaterial()`：名称、手机号为公共必填项；企业类型额外校验公司名称与社会信用代码。创建合同时会按账户类型取名称——个人用 `displayName`，企业用 `orgName`，作为合同上的甲乙方名称。

查询时通过 `UserMaterialMapper.selectUserMaterialByUserIdAndType(userId, accountType)` 精确定位，保证一个用户可同时存在个人与企业两套资料。

## 三、业务流程二：创建合同模板

模板是合同的骨架，这段流程分三步走，对应 `TemplateController` 的三个接口。

**第一步：上传模板文件**（`POST /api/template/upload`）

接收模板名称与 PDF 文件。`TemplateService.upload()` 先把文件落地到本地 `uploads` 目录，再调用 `EsignGateway.uploadTemplateToEsign()` 上传到 E签宝：

1. 计算文件 MD5、大小，调用 `/v3/files/file-upload-url` 获取 fileId 和上传地址
2. 通过 OkHttp `PUT` 把文件字节流推送到上传地址
3. 轮询 `/v3/files/{fileId}` 等待文件状态变为就绪（`waitForFileReady`，最多轮询 15 次，每次间隔 2 秒）

上传成功后将 `esignFileId` 连同本地存储路径写入 `t_contract_template`。

**第二步：跳转 E签宝配置控件**（`POST /api/template/getEsignConfig`）

这一步把用户引导到 E签宝的模板编辑器，在 PDF 上拖拽设置签署区域、文本填充控件。逻辑分两种情况：

- **首次配置**（`docTemplateId` 为空）：调用 `/v3/doc-templates/doc-template-create-url` 创建文档模板，拿到 `docTemplateId` 和 `docTemplateCreateUrl`，返回前端跳转。用户在 E签宝页面里设置好「甲方名称」「乙方名称」「合同编号」等文本控件，以及签章区域。
- **再次编辑**（`docTemplateId` 已存在）：调用 `/v3/doc-templates/{id}/doc-template-edit-url` 拿到编辑链接返回。

**第三步：保存签署坐标**（`POST /api/template/updateTemplateConfig`）

用户在 E签宝配置完控件后，还需要在本地记录甲乙方的签章坐标，用于后续发起签署时定位盖章位置。`TemplateConfReq` 包含：

- `positionPage` —— 签章所在页码（默认第 3 页）
- `partyAX / partyAY` —— 甲方签章坐标
- `partyBX / partyBY` —— 乙方签章坐标

这些值序列化为 JSON 存入 `contract_config` 字段。发起签署时读取出来，构建 `signFields` 传给 E签宝。

## 四、业务流程三：创建合同并发起签署

这是整个系统的核心链路，`POST /api/contracts/create` 一次调用串联了两件事：创建合同 + 发起签署。

**创建合同**（`ContractService.createContract`）：

1. 校验模板存在、甲乙方用户资料存在且通过 `validateUserMaterial` 校验
2. 按账户类型解析甲乙方名称，生成合同编号（格式 `HT-yyyyMMdd-00001`，自增补零）
3. 调用 `EsignGateway.createFilledFile()`，基于文档模板生成填充后的合同 PDF：
   - 请求 `/v3/files/create-by-doc-template`，传入 `docTemplateId` 和组件列表
   - 组件包括甲方名称（componentKey=`partyAName`）、乙方名称（`partyBName`）、合同编号（`contractNo`），把用户资料填充到模板对应位置
   - 等待文件就绪，拿到 `fileId` 和 `fileDownloadUrl`
4. 合同状态置为 `0`（草稿），保存文件信息

**发起签署**（`ContractService.startSigning`）：

1. 读取模板的 `contractConfig`（签章坐标）和甲乙方用户资料
2. 调用 `EsignGateway.startSigning()` 构建签署流程：
   - 请求 `/v3/sign-flow/create-by-file`，body 包含三部分：
     - `docs`：合同文件 fileId
     - `signFlowConfig`：流程标题（甲方-乙方合同）、`autoFinish=true`（签完自动归档）、`notifyUrl`（回调地址）、计费配置
     - `signers`：两个签署方
       - **甲方**（signOrder=2，autoSign=true）：企业类型自动盖章，签署区域用 partyA 坐标，可选配置骑缝章
       - **乙方**（signOrder=1，autoSign=false）：需手动签署，签署区域用 partyB 坐标
   - 个人签署方构建 `psnSignerInfo`（psnAccount=手机号，psnName=姓名）
   - 企业签署方构建 `orgSignerInfo`（orgName、orgIDCardNum、legalRepName、transactorInfo 经办人）
3. 拿到 `signFlowId` 后，调用 `/v3/sign-flow/{flowId}/start` 启动流程
4. 合同状态置为 `1`（签署中），保存 `esignFlowId`

**获取签署链接**（`POST /api/contracts/signContract`）：

签署方调用此接口，系统根据当前 userId 判断是甲方还是乙方，调用 `/v3/sign-flow/{flowId}/sign-url` 获取签署页面链接，前端跳转后用户在 E签宝完成签署。

此外还提供刷新状态（`/v3/sign-flow/{flowId}/detail`）、撤销、下载（完成后的合同）、预览（签署中的合同）等辅助接口。

## 五、业务流程四：E签宝签署信息回调

签署状态变更后，E签宝会主动回调平台通知。回调入口 `EsignCallBackApi`（`POST /api/esign/callback`）做了两件事。

**验签**：E签宝请求头带 `x-tsign-open-app-id`、`x-tsign-open-timestamp`、`x-tsign-open-signature`，通过 `EsignEncryption.callBackCheck()` 用 appSecret 对 timestamp + queryString + body 重新计算签名比对，验签失败返回 401。

**处理 action**：

| action | 含义 | 处理 |
|---|---|---|
| `SIGN_FLOW_UPDATE` | 签署人签署完成 | 刷新合同详情，更新各签署人状态 |
| `SIGN_FLOW_FINISH` | 流程结束 | 更新合同状态为最终状态 |
| `SIGN_FLOW_COMPLETE` | 流程完成 | 更新合同状态为最终状态 |

刷新逻辑走 `refreshContractStatus`：调用 `/v3/sign-flow/{flowId}/detail` 拉取流程详情，解析 `signFlowStatus` 写入合同状态，同时遍历 `signers` 数组拼接每个签署人的签署状态描述（姓名 + 签署状态），写入 `description` 字段供前端展示。

## 六、合同状态机

合同状态贯穿全流程，理解状态流转是排查问题的关键：

```
草稿(0) ──发起签署──▶ 签署中(1) ──全部签完──▶ 完成(2)
                         │
                         ├──撤销──▶ 撤销(3)
                         ├──过期──▶ 过期(5)
                         └──拒签──▶ 拒签(7)
```

只有草稿状态的合同允许删除；只有已完成的合同允许下载；签署中的合同可以预览。

## 七、踩坑与总结

接入 E签宝 V3 API 有几个容易踩的点：

1. **文件上传是两段式**：先调 `/v3/files/file-upload-url` 拿上传地址，再 `PUT` 推文件，最后还要轮询等状态就绪，不能上传完就当作成功。
2. **模板与文档模板是两个概念**：上传的 PDF 是 file（得到 fileId），配置控件后才是 docTemplate（得到 docTemplateId）。创建合同时用的是 docTemplateId。
3. **签署坐标要自己存**：E签宝的签署区域虽然可在模板编辑器里拖，但发起签署时 signFields 的坐标要自己传，所以本地必须维护 `contractConfig`。
4. **甲乙方 autoSign 策略不同**：本系统甲方自动盖章（企业静默签），乙方手动签，signOrder 控制先后顺序。
5. **回调地址要配对**：`signFlowConfig.notifyUrl` 要与 `EsignCallBackApi` 的路径一致，且必须公网可达（项目用 cpolar 内网穿透做本地调试）。

整套流程跑通后，平台就具备了完整的电子合同签署能力：企业上传模板、配置控件，用户维护资料，系统自动填充合同并发起签署，E签宝回调驱动状态流转，最终合同可下载归档。希望这套拆解能帮你快速落地自己的电子签约系统。
