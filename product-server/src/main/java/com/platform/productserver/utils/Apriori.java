package com.platform.productserver.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.*;

/**
 * 关联规则算法
 */
public class Apriori {
    // 支持度阈值
    private final static int SUPPORT = 2;
    // 置信度阈值
    private final static double CONFIDENCE = 0.7;
    // 项之间的分隔符
    private final static String GAP = ";";
    // 项之间的分隔符
    private final static String CON = "-->";

    /**
     * 这个是用来找出1-频繁项集的方法，因为1-频繁项集的特殊性，
     * 所以需要特别的方法来返回1-频繁项集
     */
    private Map<String, Integer> findFrequentMap(List<String> dataList) {
        Map<String, Integer> resultSetMap = new HashMap<>();
        for (String data : dataList) {
            String[] element = data.split(GAP);
            //这是把所有的购买记录一条条的筛选出来
            for (String string : element) {
                string += GAP;
                if (!resultSetMap.containsKey(string)) {
                    resultSetMap.put(string, 1);
                } else {
                    resultSetMap.put(string, resultSetMap.get(string) + 1);
                }
            }
        }
        //返回的是一个各种商品对应出现频次的Map（或可称之为频繁项集）。键为商品序号，值为出现次数。
        return resultSetMap;
    }

    /**
     * 使用先验知识，判断候选集是否是频繁项集
     */
    private boolean hasInfrequentSubset(String candidateString, Map<String, Integer> setMap) {
        String[] strings = candidateString.split(GAP);
        //找出候选集所有的子集，并判断每个子集是否属于频繁子集
        for (int i = 0; i < strings.length; ++i) {
            String subString = "";
            for (int j = 0; j < strings.length; ++j) {
                if (j != i) {
                    subString += strings[j] + GAP;
                }
            }
            if (setMap.get(subString) == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据上面的频繁项集的集合选出候选集
     */
    private Map<String, Integer> aprioriGen(Map<String, Integer> setMap) {
        //此处传入的参数就是上面返回的频繁项集。
        Map<String, Integer> candidateSetMap = new HashMap<>();
        // 对每个商品取集合
        Set<String> candidateSet = setMap.keySet();
        //单独考虑每个商品的支持度，如果合格，就可以进行拼接。否则丢掉。
        for (String s1 : candidateSet) {
            String[] strings1 = s1.split(GAP);
            String s1string = "";
            for (String temp : strings1) {
                s1string += temp + GAP;
            }
            for (String s2 : candidateSet) {
                //此处也是默认商品序号是有序的。这样先判定前len-1项是否相等。
                //如果前面相等，第len项不相等，那么就可以拼接成len+1长度的候选集了。
                String[] strings2 = s2.split(GAP);
                boolean flag = true;
                for (int i = 0; i < strings1.length - 1; ++i) {
                    if (strings1[i].compareTo(strings2[i]) != 0) {
                        flag = false;
                        break;
                    }
                }
                if (flag && strings1[strings1.length - 1].compareTo(strings2[strings2.length - 1]) < 0) {
                    //连接步：产生候选
                    String c = s1string + strings2[strings2.length - 1] + GAP;
                    if (hasInfrequentSubset(c, setMap)) {
                        //剪枝步：删除非频繁的候选
                    } else {
                        candidateSetMap.put(c, 0);
                    }
                }
            }
        }
        return candidateSetMap;
    }

    /**
     * 算法主程序
     */
    public Map<String, Integer> apriori(List<String> dataList) {

        Map<String, Integer> maps = findFrequentMap(dataList);
        Map<String, Integer> frequentSetMap = new TreeMap<>(maps);
        // Into the loop choose
        while (CollUtil.isNotEmpty(maps)) {
            Map<String, Integer> candidateSetMap = aprioriGen(maps);
            //得到的就是候选集 candidateSetMap ，当然我们只要key部分即可啦！
            Set<String> candidateKeySet = candidateSetMap.keySet();
            //扫描，进行计数
            for (String data : dataList) {
                for (String candidate : candidateKeySet) {
                    boolean flag = true;
                    String[] strings = candidate.split(GAP);
                    for (String string : strings) {
                        //意味着在Data，也就是在初始的购物记录中查找当前的频繁项集中的某一条。寻找string如果不成功，则返回-1；
                        // 功能：查找某个元素在ArrayList中第一次出现的位置。
                        if (!StrUtil.contains(data, string + GAP)) {flag = false; break;}
                    }
                    //如果查找成功，那么就可以丢到正式的候选集中了。
                    if (flag) { candidateSetMap.put(candidate, candidateSetMap.get(candidate) + 1);}
                }
            }
            //从候选集中找到符合支持度的频繁项集，stepFrequentSetMap顾名思义就是每一次找到的新的频繁集。
            //所以在置入新的频繁集之前，都要先把上次的清空掉。
            maps.clear();
            for (String candidate : candidateKeySet) {
                Integer count = candidateSetMap.get(candidate);
                if (count >= SUPPORT) {
                    maps.put(candidate, count);
                }
            }
            // 合并所有频繁集 puaAll的作用是把一个Map的所有元素置入并且去重。
            frequentSetMap.putAll(maps);
        }
        //While循环结束的条件是新的频繁项集的大小为0.也就是必须要完全空了才出来。
        //这时候已经确保了frequentSetMap包含有所有的频繁项集了。
        return frequentSetMap;
    }

    /**
     * 求一个集合所有的非空真子集
     为了以后可以用在其他地方，这里我们不是用递归的方法
     */

    private List<String> subset(String sourceSet) {
        //“按位对应法”,从1-2^strings.length-1位，可以用二进制来表示是否取到该值。
        /*
        如集合A={a,b,c},对于任意一个元素，在每个子集中，要么存在，要么不存在。 映射为子集：
        (a,b,c)
        (1,1,1)->(a,b,c)
        (1,1,0)->(a,b)
        (1,0,1)->(a,c)
        (1,0,0)->(a)
        (0,1,1)->(b,c)
        (0,1,0)->(b)
        (0,0,1)->(c)
        (0,0,0)->@(@表示空集)
        */
        List<String> result = new ArrayList<>();
        String[] strings = sourceSet.split(GAP);
        for (int i = 1; i < (Math.pow(2, strings.length)) - 1; ++i) {
            String item = "";
            int ii = i;
            int[] flag = new int[strings.length];
            int count = 0;
            while (ii >= 0 && count < strings.length) {
                flag[count] = ii % 2;
                //此处可以理解为右移操作，即检查完当前位之后，可以跳到更高位去检测是否取值。
                ii = ii / 2;
                ++count;
            }
            for (int s = 0; s < flag.length; ++s) {
                if (flag[s] == 1) {
                    //此处应该是从右边开始往左边加。所以item在后面
                    item += strings[s] + GAP + item;
                }
            }
            result.add(item);
        }
        return result;
    }

    /**
     * 集合运算，A/B
     *
     * @return
     */
    private String expect(String stringA, String stringB) {
        String result = "";
        String[] stringAs = stringA.split(GAP);
        String[] stringBs = stringB.split(GAP);

        for (int i = 0; i < stringAs.length; ++i) {
            boolean flag = true;
            for (int j = 0; j < stringBs.length; ++j) {
                //如果指定的数与参数相等返回0。
                // 如果指定的数小于参数返回 -1。
                // 如果指定的数大于参数返回 1。
                if (stringAs[i].compareTo(stringBs[j]) == 0) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result += stringAs[i] + GAP;
            }
        }
        return result;
    }

    /**
     * 由频繁项集产生关联规则
     */
    public TreeMap<String, Double> relationRules(Map<String, Integer> frequentSetMap) {
        TreeMap<String, Double> relationMap = new TreeMap<>();
        Set<String> KeySet = frequentSetMap.keySet();
        for (String key : KeySet) {
            List<String> keySubset = subset(key);
            for (String keySubSetItem : keySubset) {
                //子集keySubsetItem也是频繁项
                Integer count = frequentSetMap.get(keySubSetItem);
                if (count != null) {
                    /*
                       置信度
                    　　置信度(confidence)揭示了A出现时B是否一定出现，如果出现，则出现的概率是多大。如果A->B的置信度是100%，则说明A出现时B一定会出现（返回来不一定）。
                       上图中底板共出现5次，其中4次同时购买了胶皮，底板->胶皮的置信度是80%。 用公式表示是，物品A->B的置信度=物品{A,B}的支持度 / 物品{A}的支持度：
                    　　Confidence(A->B) = support({A,B}) / support({A}) = P(B|A)
                    */
                    Double confidence = (1.0 * frequentSetMap.get(key)) / (1.0 * frequentSetMap.get(keySubSetItem));
                    if (confidence > CONFIDENCE) {
                        relationMap.put(keySubSetItem + CON + expect(key, keySubSetItem), confidence);
                    }
                }
            }
        }
        return relationMap;
    }

    //参考https://www.jianshu.com/p/7270d2f47a53
    public static void main(String[] args) {
        ArrayList<String> dataList = new ArrayList<>();
        dataList.add("1;2;5;");
        dataList.add("2;4;");
        dataList.add("2;3;");
        dataList.add("1;2;4;");
        dataList.add("1;3;");
        dataList.add("2;3;");
        dataList.add("1;3;");
        dataList.add("1;2;3;5;");
        dataList.add("1;2;3;");
        Map<String, String> maps = new HashMap<>();
        maps.put("1", "牛奶");
        maps.put("2", "面包");
        maps.put("3", "鸡蛋");
        maps.put("4", "火腿");
        maps.put("5", "芝士");
        List<String> resList = new ArrayList<>();
        for (String ele : dataList) {
            String res = ele;
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();
                res = res.replace(key, val);
            }
            resList.add(res);
        }

        Apriori apriori2 = new Apriori();
        System.out.println("=========频繁项集==========");
        Map<String, Integer> frequentSetMap = apriori2.apriori(resList);
        Set<String> keySet = frequentSetMap.keySet();
        for (String key : keySet) {
            System.out.println(key + " : " + frequentSetMap.get(key));
        }
        System.out.println("===========关联规则==========");
        Map<String, Double> relationRulesMap = apriori2.relationRules(frequentSetMap);
        Set<String> rrKeySet = relationRulesMap.keySet();
        for (String rrKey : rrKeySet) {
            System.out.println(rrKey + "  :  " + relationRulesMap.get(rrKey));
        }
    }
}
