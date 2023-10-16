package com.platform.authcommon.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.*;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RecommendFactory {

    public static DataModel buildJdbcDataModel(List<? extends Recommend> preferenceList) {
        FastByIDMap<PreferenceArray> fastByIdMap = new FastByIDMap<>();
        // 创建模型
        Map<Long, List<Recommend>> map = preferenceList.stream()
                .collect(Collectors.groupingBy(Recommend::getUserId));

        Collection<List<Recommend>> list = map.values();
        log.info(" start !");
        for (List<Recommend> preferences : list) {
            GenericPreference[] array = new GenericPreference[preferences.size()];
            for (int i = 0; i < preferences.size(); i++) {
                Recommend node = preferences.get(i);
                GenericPreference item = new GenericPreference(node.getUserId(), node.getItemId(), node.getValue());
                array[i] = item;
            }
            fastByIdMap.put(array[0].getUserID(), new GenericUserPreferenceArray(Arrays.asList(array)));
        }
        return new GenericDataModel(fastByIdMap);
    }

    /**
     * 创建 dataModel
     * @param preferenceList
     * @return
     */
    public static DataModel buildItemDataModel(List<? extends Recommend> preferenceList) {
        FastByIDMap<PreferenceArray> fastByIdMap = new FastByIDMap<>();
        // 创建模型
        Map<Long, List<Recommend>> map = preferenceList.stream()
                .collect(Collectors.groupingBy(Recommend::getItemId));

        Collection<List<Recommend>> list = map.values();
        log.info(" start !");
        for (List<Recommend> preferences : list) {
            GenericPreference[] array = new GenericPreference[preferences.size()];
            for (int i = 0; i < preferences.size(); i++) {
                Recommend node = preferences.get(i);
                GenericPreference item = new GenericPreference(node.getUserId(), node.getItemId(), node.getValue());
                array[i] = item;
            }
            fastByIdMap.put(array[0].getItemID(), new GenericItemPreferenceArray(Arrays.asList(array)));
        }
        return new GenericDataModel(fastByIdMap);
    }

    /**
     * 构造相似度算法模型
     */
    public enum SIMILARITY {
        /**
         * 皮尔逊相似度算法
         */
        PEARSON,
        /**
         * 欧几里得相似度算法
         */
        EUCLIDEAN,
        /**
         * 余弦相似度算法
         */
        COSINE,
        /**
         * 基于谷本系数计算相似度
         */
        TANIMOTO,
        /**
         * 基于Manhattan距离相似度
         */
        CITY_BLOCK,
        /**
         * 相似性,基于对数似然比的相似度
         */
        LOGLIKELIHOOD
        // AVERAGE_ABSOLUTE_DIFFERENCE
    }

    public static UserSimilarity userSimilarity(SIMILARITY type, DataModel m) throws TasteException {
        return (UserSimilarity) getSimilarity(type, m);
    }

    public static ItemSimilarity itemSimilarity(SIMILARITY type, DataModel m) throws TasteException {
        return (ItemSimilarity) getSimilarity(type, m);
    }

    private static Refreshable getSimilarity(SIMILARITY type, DataModel m) throws TasteException {
        switch (type) {
            case PEARSON:
                return new PearsonCorrelationSimilarity(m);
            case COSINE:
                return new UncenteredCosineSimilarity(m);
            case TANIMOTO:
                return new TanimotoCoefficientSimilarity(m);
            case LOGLIKELIHOOD:
                return new LogLikelihoodSimilarity(m);
            case CITY_BLOCK:
                return new CityBlockSimilarity(m);
            case EUCLIDEAN:
            default:
                return new EuclideanDistanceSimilarity(m);
        }
    }

    /**
     * 构造近邻算法模型
     */
    public enum NEIGHBORHOOD {
        /**
         * 基于固定数量的邻居,对每个用户取固定数量N个最近邻居
         */
        NEAREST,
        /**
         * 基于相似度,对每个用户基于一定的限制，取落在相似度限制以内的所有用户为邻居
         */
        THRESHOLD
    }

    /**
     * 计算最近邻域
     *
     * @param type 算法类型
     * @param s    相似度算法
     * @param m    数据模型
     * @param num  NEAREST - 固定邻居数量 THRESHOLD -最低相似度
     * @return UserNeighborhood
     */
    public static UserNeighborhood userNeighborhood(NEIGHBORHOOD type, UserSimilarity s, DataModel m, double num) throws TasteException {
        switch (type) {
            case NEAREST:
                return new NearestNUserNeighborhood((int) num, s, m);
            case THRESHOLD:
            default:
                return new ThresholdUserNeighborhood(num, s, m);
        }
    }

    /**
     * 获取基于用户的推荐器
     *
     * @param us   相似度算法
     * @param un   最近邻算法
     * @param pref 是否需要首选,即偏好
     * @return RecommenderBuilder
     */
    public static RecommenderBuilder userRecommender(final UserSimilarity us, final UserNeighborhood un, boolean pref) {
        return pref
                //基于用户的推荐引擎
                ? model -> new GenericUserBasedRecommender(model, un, us)
                //基于用户的无偏好值推荐引擎
                : model -> new GenericBooleanPrefUserBasedRecommender(model, un, us);
    }

    /**
     * 获取基于用户的推荐器
     *
     * @param is   相似度算法
     * @param pref 是否需要首选
     * @return RecommenderBuilder
     */
    public static RecommenderBuilder itemRecommender(final ItemSimilarity is, boolean pref) {
        return pref
                //基于物品的推荐引擎
                ? model -> new GenericItemBasedRecommender(model, is)
                //基于物品的无偏好值推荐引擎
                : model -> new GenericBooleanPrefItemBasedRecommender(model, is);
    }

    /**
     * 构造算法评估模型
     */
    public enum EVALUATOR {
        /**
         * 计算平均差值
         */
        AVERAGE_ABSOLUTE_DIFFERENCE,

        /**
         * 计算用户的预测评分和实际评分之间的“均方根”差异。这是这个差值的平均值的平方根
         */
        RMS
    }

    public static RecommenderEvaluator buildEvaluator(EVALUATOR type) {
        switch (type) {
            case RMS:
                return new RMSRecommenderEvaluator();
            case AVERAGE_ABSOLUTE_DIFFERENCE:
            default:
                return new AverageAbsoluteDifferenceRecommenderEvaluator();
        }
    }

    /**
     * 推荐器进度评估，数值越低精度越高
     *
     * @param type    推荐器类型
     * @param rb      推荐器构建器
     * @param mb      数据模型构建器
     * @param dm      数据模型
     * @param trainPt 培训数据占比
     */
    public static void evaluate(EVALUATOR type, RecommenderBuilder rb, DataModelBuilder mb, DataModel dm, double trainPt) throws TasteException {
        System.out.printf("%s Evaluater Score:%s\n", type.toString(), buildEvaluator(type).evaluate(rb, mb, dm, trainPt, 1.0));
    }

    public static void evaluate(RecommenderEvaluator re, RecommenderBuilder rb, DataModelBuilder mb, DataModel dm, double trainPt) throws TasteException {
        System.out.printf("Evaluater Score:%s\n", re.evaluate(rb, mb, dm, trainPt, 1.0));
    }

    public static void statsEvaluator(RecommenderBuilder rb, DataModelBuilder mb, DataModel m, int topn) throws TasteException {
        RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();
        IRStatistics stats = evaluator.evaluate(rb, mb, m, null, topn, GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
        System.out.printf("Recommender IR Evaluator: [Precision:%s,Recall:%s]\n", stats.getPrecision(), stats.getRecall());
    }

    /**
     * 输出结果
     */
    public static void showItems(long uid, List<RecommendedItem> recommendations, boolean skip) {
        if (!skip || recommendations.size() > 0) {
            System.out.printf("uid:%s,", uid);
            for (RecommendedItem recommendation : recommendations) {
                System.out.printf("(%s,%f)", recommendation.getItemID(), recommendation.getValue());
            }
            System.out.println();
        }
    }
}
