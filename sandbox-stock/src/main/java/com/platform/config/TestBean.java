package com.platform.config;


import cn.hutool.core.lang.Pair;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TestBean {


    private String address;

    private Integer age;

    private Integer id;

    private String name;

    private Date createTime;

    public static void main(String[] args) {


        TestBean bean1 = new TestBean();
        bean1.setId(1);
        bean1.setAge(2);
        bean1.setName("张三");
        bean1.setAddress("中国");
        bean1.setCreateTime(new Date());

        TestBean bean2 = new TestBean();
        bean2.setId(1);
        bean2.setAge(2);
        bean2.setName("张三");
        bean2.setAddress("中国");
        bean2.setCreateTime(new Date());

        List<TestBean> beanList = Lists.newArrayList();
        beanList.add(bean2);
        beanList.add(bean1);

        // 分组后，每个分组的第一个元素作为结果, collect 收集起来，然后进行映射
        List<TestBean> collect = beanList.stream()
                .collect(Collectors.groupingBy(element -> Pair.of(element.getId(), element.getAge())))
                .entrySet().stream()
                .map(entry -> {
                    List<TestBean> value = entry.getValue();
                    Pair<Integer, Integer> key = entry.getKey();

                    TestBean testBean = value.get(0);
                    testBean.setId(key.getKey());
                    testBean.setAge(key.getValue());
                    return testBean;

                }).collect(Collectors.toList());



    }


}
