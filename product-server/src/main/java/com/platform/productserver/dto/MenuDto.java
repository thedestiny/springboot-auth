package com.platform.productserver.dto;


import lombok.Data;

import java.util.List;

@Data
public class MenuDto {


    private Integer id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 序号
     */
    private Integer seq;

    /**
     * 父级id
     */
    private Integer parentId;

    /**
     * 子节点列表
     */
    private List<MenuDto> children;


    public MenuDto(Integer id, String name, Integer parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    public MenuDto() {
    }
}
