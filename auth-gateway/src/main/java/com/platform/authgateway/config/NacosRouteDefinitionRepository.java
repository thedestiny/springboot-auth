package com.platform.authgateway.config;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


// https://zhuanlan.zhihu.com/p/458245501
// https://blog.csdn.net/zhulonglove1023/article/details/123801036
// https://blog.csdn.net/qq_38374397/article/details/125874882

// RouteDefinitionRepository 默认情况下，springcloud gateway 是基于内存为存储的，InMemoryRouteDefinitionRepository，
// 也可以是 RedisRouteDefinitionRepository, 对路由的删除只能根据id 去删除路由，但是要获取所有的路由id 是比较复杂的，一般情况下需要清空重新加载，
// 所以网关的配置新的路由信息，如果不重启网关，是无法生效的


@Slf4j
public class NacosRouteDefinitionRepository implements RouteDefinitionRepository {


    private String dataId;
    private String group;
    private ApplicationEventPublisher publisher;
    private NacosConfigManager manager;

    private final Map<String, RouteDefinition> routes = new LinkedHashMap<String, RouteDefinition>();


    public NacosRouteDefinitionRepository(String dataId, String group,
                                          ApplicationEventPublisher publisher, NacosConfigManager manager) {
        this.dataId = dataId;
        this.group = group;
        this.publisher = publisher;
        this.manager = manager;
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {

        try {
            log.info(" NacosRouteDefinitionRepository ");
            String config = manager.getConfigService().getConfig(dataId, group, 5000);
            List<RouteDefinition> definitions = JSONArray.parseArray(config, RouteDefinition.class);
            return Flux.fromIterable(definitions);

        } catch (NacosException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(r -> {
            if (ObjectUtils.isEmpty(r.getId())) {
                return Mono.error(new IllegalArgumentException("id may not be empty"));
            }
            routes.put(r.getId(), r);
            return Mono.empty();
        });

    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            if (routes.containsKey(id)) {
                routes.remove(id);
                return Mono.empty();
            }
            return Mono.defer(() -> Mono.error(new NotFoundException("RouteDefinition not found: " + routeId)));
        });

    }
}
