package com.platform.authcommon.model;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class ModelTest {

    public static void main(String[] args) {
        // 1001 - 1007 代表观众 A-G
        // 2001 - 2007 代表电影
        List<Customer> list = new ArrayList<>();
        // 用户 物品 评分
        list.add(customer(1001L, 2001L, 3.5F));
        list.add(customer(1001L, 2002L, 1.0F));

        list.add(customer(1002L, 2001L, 2.5F));
        list.add(customer(1002L, 2002L, 3.5F));
        list.add(customer(1002L, 2003L, 3.0F));
        list.add(customer(1002L, 2004L, 3.5F));
        list.add(customer(1002L, 2005L, 2.5F));
        list.add(customer(1002L, 2006L, 3.0F));

        list.add(customer(1003L, 2001L, 3.0F));
        list.add(customer(1003L, 2002L, 3.5F));
        list.add(customer(1003L, 2003L, 1.5F));
        list.add(customer(1003L, 2004L, 5.0F));
        list.add(customer(1003L, 2005L, 3.0F));
        list.add(customer(1003L, 2006L, 3.5F));

        list.add(customer(1004L, 2001L, 2.5F));
        list.add(customer(1004L, 2002L, 3.5F));
        list.add(customer(1004L, 2004L, 3.5F));
        list.add(customer(1004L, 2005L, 4.0F));

        list.add(customer(1005L, 2001L, 3.5F));
        list.add(customer(1005L, 2002L, 2.0F));
        list.add(customer(1005L, 2003L, 4.5F));
        list.add(customer(1005L, 2005L, 3.5F));
        list.add(customer(1005L, 2006L, 2.0F));

        list.add(customer(1006L, 2001L, 3.0F));
        list.add(customer(1006L, 2002L, 4.0F));
        list.add(customer(1006L, 2003L, 2.0F));
        list.add(customer(1006L, 2004L, 3.0F));
        list.add(customer(1006L, 2005L, 3.0F));
        list.add(customer(1006L, 2006L, 2.0F));

        list.add(customer(1007L, 2001L, 4.5F));
        list.add(customer(1007L, 2002L, 1.5F));
        list.add(customer(1007L, 2003L, 3.0F));
        list.add(customer(1007L, 2004L, 5.0F));
        list.add(customer(1007L, 2005L, 3.5F));

        itemModel(list, 1007L);

    }

    public static Customer customer(Long user, Long good, Float val) {

        Customer node = new Customer();
        node.setCustomerId(user);
        node.setCommodityId(good);
        node.setValue(val);
        return node;
    }

    // 物品推荐
    public static Map<Long, Float> itemModel(List<Customer> list, Long itemId) {

        Map<Long, Float> map = new HashMap<>();

        try {
            //1 创建模型数据
            DataModel dataModel = RecommendFactory.buildJdbcDataModel(list);
            //2.创建similar相似度，构建基于用户和物品的相似模型
            // EuclideanDistanceSimilarity  欧几里得距离
            // PearsonCorrelationSimilarity 皮尔逊相似度
            // UncenteredCosineSimilarity   非中心余弦相似性
            ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(dataModel);
            UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(dataModel);
            // 3 获取用户和商品的集合
            LongPrimitiveIterator itemIDs = dataModel.getItemIDs();
            LongPrimitiveIterator userIDs = dataModel.getUserIDs();
            List<Long> users = Lists.newArrayList();
            List<Long> items = Lists.newArrayList();
            while (itemIDs.hasNext()){
                items.add(itemIDs.nextLong());
            }
            while (userIDs.hasNext()){
                users.add(userIDs.nextLong());
            }
            // 顺序排序
            Collections.sort(users);
            Collections.sort(items);
            System.out.println("用户相似度：");
            for (int i = 0; i < users.size(); i++) {
                for (int i1 = 0; i1 < users.size(); i1++) {
                    if(i == i1){
                        System.out.println(users.get(i) + " - " + users.get(i1) + " =  1");
                    }
                    if(i < i1){
                        // 相似度计算
                        double v = userSimilarity.userSimilarity(users.get(i), users.get(i1));
                        BigDecimal bg = BigDecimal.valueOf(v).setScale(2, BigDecimal.ROUND_HALF_UP);
                        System.out.println(users.get(i) + " - " + users.get(i1) + " =  " + bg);
                    }
                }
            }

            System.out.println("物品相似度：");
            for (int i = 0; i < items.size(); i++) {
                for (int i1 = 0; i1 < items.size(); i1++) {
                    if(i == i1){
                        System.out.println(items.get(i) + " - " + items.get(i1) + " =  1");
                    }
                    if(i < i1){
                        // 相似度计算
                        double v = itemSimilarity.itemSimilarity(items.get(i), items.get(i1));
                        System.out.println(items.get(i) + " - " + items.get(i1) + " =  " + v);
                    }
                }
            }


            //System.out.println(v);
        } catch (Exception e) {

        }
        return map;

    }
}
