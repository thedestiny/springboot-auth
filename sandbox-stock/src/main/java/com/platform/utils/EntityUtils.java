package com.platform.utils;

import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Field;

public class EntityUtils {


    public static void main(String[] args) {

//
//        parse(AddressBook.class);
//        parse(Category.class);
//        parse(Dish.class);
//        parse(DishFlavor.class);
//        parse(Employee.class);
//        parse(OrderDetail.class);
//        parse(Orders.class);
//        parse(Setmeal.class);
//        parse(SetmealDish.class);
//        parse(ShoppingCart.class);
//        parse(User.class);




    }




    public static void parse(Class klzz){
        String name = klzz.getSimpleName();
        Field[] fields = klzz.getDeclaredFields();

        String result = StrUtil.format("CREATE TABLE `{}` (\n", Entity2SqlUtils.humpToLine(name));

        for (Field node : fields) {
            String fieldName = node.getName();
            if(StrUtil.equals(fieldName, "serialVersionUID")){
                  continue;
            }
            String typeName = node.getType().getSimpleName();
            // System.out.println(fieldName + " \t " + typeName);

            String field =  Entity2SqlUtils.humpToLine(fieldName);
            String type = Entity2SqlUtils.transType(typeName);
            String tp = StrUtil.format("  `{}` {}  NULL COMMENT '{}',\n", field, type, fieldName);
            if(field.equals("id")){
                tp = StrUtil.format("  `{}` {} NOT NULL AUTO_INCREMENT COMMENT '{}',\n", field, type, fieldName);
            }

            result += tp;
        }

        result += "  PRIMARY KEY (`id`) USING BTREE\n";
        result += ") ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COMMENT='" + name +"';\n";

        System.out.println("\n" + result);

    }

}
