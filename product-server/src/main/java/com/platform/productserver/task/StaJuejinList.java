package com.platform.productserver.task;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.platform.productserver.dto.DataNode;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author kaiyang
 * @Date 2023-12-14 3:42 下午
 */

@Slf4j
public class StaJuejinList {

    public static void main(String[] args) throws InterruptedException {

        List<DataNode> data = data();
        Set<String> collect = data.stream().map(DataNode::getUserId).collect(Collectors.toSet());
        System.out.println("参加人数 :: " + collect.size() + " 文章数量 :: " + data.size());

        Map<String, List<DataNode>> collect1 = data.stream().collect(Collectors.groupingBy(DataNode::getUserId));
        collect1.forEach((key, value) -> {
            System.out.println(key + "\t" + value.size());
        });

    }


    public static List<DataNode> data() throws InterruptedException {
        String url = "https://api.juejin.cn/content_api/v1/article/query_list?aid=2608&uuid=6977589444536108582&spider=0";

        List<DataNode> nodeList = new ArrayList<>();

        Integer cnt = 0;
        HttpRequest post = HttpUtil.createPost(url);
        post.header("Content-Type", "application/json");
        String reqBody = "{\n" +
                "    \"theme_id\": \"7218019389664067621\",\n" +
                "    \"cursor\": \"ELE\",\n" +
                "    \"sort_type\": 2,\n" +
                "    \"owner_type\": 40\n" +
                "}";

        while (true) {
            post.body(reqBody.replace("ELE", String.valueOf(cnt)));
            String execute = post.execute().body();
            log.info("resp is {}", execute);
            JSONObject json = JSONObject.parseObject(execute);
            boolean extracted = extracted(nodeList, json);
            if (!extracted) {
                break;
            }
            TimeUnit.SECONDS.sleep(3 + new Random().nextInt( 6));
            cnt = Integer.parseInt(json.getString("cursor"));
            log.info("cnt is {}", cnt);
        }


        return nodeList;

    }


    private static boolean extracted(List<DataNode> nodeList, JSONObject json) {
        JSONArray data = json.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            JSONObject node = data.getJSONObject(i);
            JSONObject info = node.getJSONObject("article_info");
            String user_id = info.getString("user_id");
            String article_id = info.getString("article_id");
            Long time = info.getLong("mtime") * 1000;
            String format = DateUtil.format(new DateTime(time), "yyyy-MM-dd");
            DataNode ele = new DataNode();
            ele.setTime(format);
            ele.setArticleId(article_id);
            ele.setUserId(user_id);
            if (StrUtil.compare(format, "2023-12-01", true) < 0) {
                return false;
            }
            nodeList.add(ele);
        }
        return true;
    }

}
