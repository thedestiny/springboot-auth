package com.platform.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Masseur implements Serializable {

    private static final long serialVersionUID = 6207223812626144515L;

    private Long id;

    private String realName;

    // 绑定的项目id
    private List<ItemInfo> items;


}
