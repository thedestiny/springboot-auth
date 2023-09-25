package com.platform.authcommon.config;

import com.google.common.collect.Maps;
import com.platform.authcommon.common.aop.annotation.RestMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 */
@Slf4j
@Component
public class AppHandlerMapping extends RequestMappingHandlerMapping {

    private static final Map<HandlerMethod, RequestMappingInfo> MAPPING_INFO_MAP = Maps.newHashMap();

    public AppHandlerMapping() {
    }

    /**
     * RequestMappingInfo
     */
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        HandlerMethod handlerMethod = super.createHandlerMethod(handler, method);
        MAPPING_INFO_MAP.put(handlerMethod, mapping);
        // validateMethodMapping
        log.info("mapping is {}", mapping);
        super.registerHandlerMethod(handler, method, mapping);
    }

    /**
     * 解析自定义注解和映射关系，创建 RequestMappingInfo
     */
    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {

        RequestMappingInfo mappingInfo = super.getMappingForMethod(method, handlerType);
        if (mappingInfo != null) {
            return mappingInfo;
        }
        return createCustomRequestMappingInfo(method);
    }

    private RequestMappingInfo createCustomRequestMappingInfo(Method method) {
        RestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(method, RestMapping.class);
        if (mapping != null) {
            return RequestMappingInfo.paths(mapping.value())
                    .methods(mapping.method())
                    .build();
        }
        return null;
    }

}
