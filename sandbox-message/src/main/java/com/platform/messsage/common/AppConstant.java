package com.platform.messsage.common;

public class AppConstant {


    /**
     * direct 模式
     */
    public static final String DIRECT_EXCHANGE = "direct_exchange";
    public static final String DIRECT_ROUTE_KEY = "direct_route_key";
    public static final String DIRECT_QUEUE = "direct_queue";

    /**
     * topic 模式
     */
    public static final String TOPIC_EXCHANGE = "topic_exchange";
    public static final String TOPIC_QUEUE_PHONE = "topic.queue.phone";
    public static final String TOPIC_QUEUE_MALL = "topic.queue.mall";
    public static final String TOPIC_QUEUE_ALL = "topic.queue.#";

    /**
     * fanout 模式
     */
    public static final String FANOUT_EXCHANGE = "fanout_exchange";
    public static final String FANOUT_QUEUE_A = "fanout_queue_a";
    public static final String FANOUT_QUEUE_B = "fanout_queue_b";


}
