package com.platform.contract.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartSigningReq {

    @Schema(description = "合同id")
    @NotNull(message = "id不能为空")
    private Long id;

    private String signParty;
}
