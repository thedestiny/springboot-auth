package com.platform.productserver.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量操作对象返回
 * @Description
 * @Date 2023-09-20 4:48 PM
 */

@Data
public class BatchTradeResp implements Serializable {


    private static final long serialVersionUID = 4162685945481542445L;

    private List<BaseNode> dataList;



}
