package com.platform.productserver.ext;

import com.platform.productserver.entity.PkgOutLog;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Date 2023-08-29 4:54 PM
 */

@Data
public class PkgOutLogExt extends PkgOutLog implements Serializable {

    private static final long serialVersionUID = 274163106243424742L;

    // 查询数据量
    private Integer fetchSize;
    // 起始id
    private Long startId;
}
