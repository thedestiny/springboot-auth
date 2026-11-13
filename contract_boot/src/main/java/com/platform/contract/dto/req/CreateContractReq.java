package com.platform.contract.dto.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateContractReq implements Serializable {

    private Long templateId;

    private String partyBUserId;

    private String partyBUserType;

}
