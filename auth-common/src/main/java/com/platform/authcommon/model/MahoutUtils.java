package com.platform.authcommon.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import com.alibaba.fastjson.JSONObject;
import com.github.javafaker.Faker;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class MahoutUtils {

    // mock 数据工具类
    public static Faker faker = new Faker(Locale.SIMPLIFIED_CHINESE);
    public static Snowflake flake = new Snowflake();


    // 产生用户数据和物品数据 数据信息
    public static List<Long> seeds(Long start, Integer num) {
        List<Long> results = Lists.newArrayList();
        for (Integer i = 0; i < num; i++) {
            results.add(start + i);
        }
        return results;
    }

    // 随机取数
    public static List<Long> rands(List<Long> ids, Integer num) {
        Set<Long> sets = Sets.newHashSet();
        while (true) {
            // 随机取数据
            Integer rnd = faker.random().nextInt(0, ids.size() - 1);
            sets.add(ids.get(rnd));
            if (sets.size() > num) {
                break;
            }
        }
        return Lists.newArrayList(sets);

    }

    public static Customer customer(Long user, Long good, Float val) {
        Customer node = new Customer(flake.nextId(), user, good, val);
        return node;
    }


    public static void main(String[] args) {

        // 制造用户数据和物品数据
        List<Long> users = seeds(10000L, 10);
        List<Long> goods = seeds(20000L, 100);
        // 设置物品和用户的评价基准
        Map<Long, Float> maps = new HashMap<>();
        for (Long good : goods) {
            Boolean flag = faker.random().nextBoolean();
            maps.put(good, flag ? 3.5f : 4f);
        }
        List<Customer> customers = Lists.newArrayList();
        // 用户和物品进行评分
        for (int i = 0; i < users.size(); i++) {
            Long use = users.get(i);
            int size = 1;
            if(use % 3 == 0){
                size = 5;
            }
            // 获取一组商品数据
            List<Long> rands = rands(goods, faker.random().nextInt(goods.size() - size, goods.size() - 1));
            for (Long rand : rands) {
                // 取随机数 用户 物品 评分 存入数据列表中
                Integer it = faker.random().nextInt(-1, 1);
                customers.add(customer(use, rand, maps.get(rand) + it));
            }

        }
        // 打乱顺序
        Collections.shuffle(customers);
        // 根据用户和物品进行排序
        CollUtil.sort(customers, (o1, o2) -> {
            if (o1.getCustomerId().compareTo(o2.getCustomerId()) > 0) {
                return 1;
            } else if (o1.getCustomerId().compareTo(o2.getCustomerId()) == 0) {
                if (o1.getCommodityId().compareTo(o2.getCommodityId()) > 0) {
                    return 1;
                }
            }
            return -1;
        });
        // 打印用户对物品的评分数据
        log.info("data ============== start ");
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            log.info(customer.getCustomerId() + " == " + customer.getCommodityId() + " == " + customer.getValue());
        }
        log.info("data ============== end, total == " + customers.size());

        // 基于用户推荐，根据评分排序
        Map<Long, Float> recommends = userModel(customers, users.get(4));
        List<Map.Entry<Long, Float>> list = new ArrayList<>(recommends.entrySet());
        list.sort(Map.Entry.comparingByValue());
        CollUtil.reverse(list);
        log.info("user recommend result :");
        for (Map.Entry<Long, Float> entry : list) {
            log.info(entry.getKey() + "  -> " + entry.getValue());
        }

        // 基于物品推荐，根据评分排序
        Map<Long, Float> itemRecommends = itemModel(customers, goods.get(4));
        List<Map.Entry<Long, Float>> list1 = new ArrayList<>(itemRecommends.entrySet());
        list1.sort(Map.Entry.comparingByValue());
        CollUtil.reverse(list1);
        log.info("item recommend result :");
        for (Map.Entry<Long, Float> entry : list1) {
            log.info(entry.getKey() + "  -> " + entry.getValue());
        }


    }

    final static int NEIGHBORHOOD_NUM = 2; //临近的用户个数
    final static int RECOMMENDER_NUM = 5; //推荐物品的最大个数

    // 用户推荐
    public static Map<Long, Float> userModel(List<Customer> list, Long uid) {
        List<Long> idList = new ArrayList<>();
        Map<Long, Float> map = new HashMap<>();
        try {
            // List<CustomerPreference> list = new ArrayList<>();// baseMapper.listAllCustomerPreference();
            //创建模型数据
            DataModel dataModel = RecommendFactory.buildJdbcDataModel(list);
            //2.创建similar相似度
            // 欧几里得、皮尔逊 EuclideanDistanceSimilarity PearsonCorrelationSimilarity 用户相识相似度
            UserSimilarity similarity = new EuclideanDistanceSimilarity(dataModel);
            //3.获取用户 userNeighborhood
            // 计算最近邻域，邻居有两种算法，基于固定数量的邻居和基于相似度的邻居，这里使用基于固定数量的邻居
            UserNeighborhood neighbor = new NearestNUserNeighborhood(NEIGHBORHOOD_NUM, similarity, dataModel);
            //4.构建推荐器 recommend
            // 协同过滤推荐有两种，分别是基于用户的和基于物品的，这里使用基于用户的协同过滤推荐
            Recommender recommender = new GenericUserBasedRecommender(dataModel, neighbor, similarity);
            // 获取所有用户信息
            LongPrimitiveIterator iter = dataModel.getUserIDs();///得到用户ID
            IDRescorer rescorer = new FilterRescorer(list.stream().map(Customer::getCustomerId).collect(Collectors.toSet()));
            // 展示类似的5个商品 rescorer
            // List<RecommendedItem> recommendedItems = recommender.recommend(uid, 5, rescorer);
            // 给 用户 uid 推荐 9 个商品
            List<RecommendedItem> items = recommender.recommend(uid, RECOMMENDER_NUM, rescorer);
            log.info(" calculate start !");
            log.info("uid -- > " + uid);
            for (RecommendedItem ritem : items) {
                log.info("({} , {})", ritem.getItemID(), ritem.getValue());
                idList.add(ritem.getItemID());
                map.put(ritem.getItemID(), ritem.getValue());
            }
            List<Long> idLists = items.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
            log.info(" calculate end ! id -> {}", JSONObject.toJSONString(idLists));

        } catch (Exception e) {
            log.info("encounter error is {} ", e.getMessage(), e);
        }
        return map;

    }

    // 物品推荐
    public static Map<Long, Float> itemModel(List<Customer> list, Long itemId) {

        List<Long> idList = new ArrayList<>();
        Map<Long, Float> map = new HashMap<>();

        try {
            //创建模型数据
            DataModel dataModel = RecommendFactory.buildJdbcDataModel(list);
            itemCF(dataModel);
            //2.创建similar相似度
            // EuclideanDistanceSimilarity 用户相识相似度
            ItemSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
            //3.获取用户 userNeighborhood
            // UserNeighborhood neighbor = new NearestNUserNeighborhood(NEIGHBORHOOD_NUM, similarity, dataModel);
            //4.构建推荐器 recommend
            ItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, similarity);
            // 获取所有用户信息
            LongPrimitiveIterator itemIds = dataModel.getItemIDs();///得到 itemID
            // IDRescorer rescorer = new FilterRescorer(list.stream().map(Customer::getCustomerId).collect(Collectors.toSet()));
            // 展示类似的5个商品 rescorer
            // List<RecommendedItem> recommendedItems = recommender.recommend(uid, 5, rescorer);
            List<RecommendedItem> items = recommender.mostSimilarItems(itemId, RECOMMENDER_NUM);
            log.info("item  {} calculate start !", itemId);
            for (RecommendedItem item : items) {
                log.info("({}, {})", item.getItemID(), item.getValue());
                idList.add(item.getItemID());
                map.put(item.getItemID(), item.getValue());
            }
            List<Long> idLists = items.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
            log.info(" calculate end ! id -> {}", JSONObject.toJSONString(idLists));

        } catch (Exception e) {
            log.info("encounter error is {} ", e.getMessage(), e);
        }
        return map;

    }

    /**
     * itemCF
     *
     * @param dataModel
     * @throws TasteException
     */
    public static void itemCF(DataModel dataModel) throws TasteException {
        ItemSimilarity itemSimilarity = RecommendFactory.itemSimilarity(RecommendFactory.SIMILARITY.EUCLIDEAN, dataModel);
        RecommenderBuilder recommenderBuilder = RecommendFactory.itemRecommender(itemSimilarity, true);
        RecommendFactory.evaluate(RecommendFactory.EVALUATOR.AVERAGE_ABSOLUTE_DIFFERENCE, recommenderBuilder, null, dataModel, 0.7);
        RecommendFactory.statsEvaluator(recommenderBuilder, null, dataModel, 2);
        LongPrimitiveIterator iter = dataModel.getUserIDs();
        while (iter.hasNext()) {
            long uid = iter.nextLong();
            List<RecommendedItem> list = recommenderBuilder.buildRecommender(dataModel).recommend(uid, RECOMMENDER_NUM);
            list.forEach(node -> {
                System.out.printf("(%s,%f)", node.getItemID(), node.getValue());
            });
            RecommendFactory.showItems(uid, list, true);
        }
    }

}
