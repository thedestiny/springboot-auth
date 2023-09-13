package com.platform.productserver.utils;


import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.platform.productserver.dto.MenuDto;
import com.platform.productserver.entity.Menu;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MenuUtils {


    public static void main(String[] args) {

        Menu m1 = new Menu(1, "根节点", 0);
        Menu m2 = new Menu(2, "用户管理", 1);
        Menu m3 = new Menu(3, "菜单管理", 1);
        Menu m4 = new Menu(4, "用户列表", 2);
        Menu m5 = new Menu(5, "用户日志", 2);
        Menu m6 = new Menu(6, "用户编辑", 4);
        Menu m7 = new Menu(7, "菜单列表", 3);

        List<Menu> lists = Lists.newArrayList(m1, m2, m3, m4, m5, m6, m7);

        List<MenuDto> list = Lists.newArrayList();

        Map<Integer, MenuDto> maps = new HashMap<>();
        for (Menu node : lists) {
            MenuDto dto = new MenuDto(node.getId(), node.getName(), node.getParentId());
            maps.put(dto.getId(), dto);
            list.add(dto);
        }

        for (MenuDto dto : list) {
            Integer parentId = dto.getParentId();
            List<MenuDto> children = dto.getChildren();
            if(CollUtil.isEmpty(children)){
                children = new ArrayList<>();
            }
            // 添加 节点 children
            if(maps.containsKey(parentId)){
                MenuDto dto1 = maps.get(parentId);
                children.add(dto1);
            }


        }




    }

}
