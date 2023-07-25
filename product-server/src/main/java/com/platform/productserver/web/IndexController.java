package com.platform.productserver.web;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.platform.authcommon.api.OrderApi;
import com.platform.authcommon.dto.OrderDto;
import com.platform.productserver.config.ProductAppConfig;
import com.platform.productserver.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping(value = "api")
public class IndexController {

    @Autowired
    private ProductAppConfig appConfig;


    @Autowired
    private OrderApi orderApi;



    @GetMapping(value = "/user/currentUser")
    @ResponseBody
    public String userInfo(){

        //从Header中获取用户信息
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userStr = request.getHeader("user");
        JSONObject json = JSONObject.parseObject(userStr);
        UserDto user = new UserDto();
        user.setUsername(json.getString("user_name"));
        user.setId(json.getLong("id"));
        user.setRoles(Convert.toList(String.class, json.get("authorities")));

        log.info("user info {}", user);

       return "user";
    }


    @GetMapping(value = "index")
    @ResponseBody
    public String index() {


        OrderDto dto = orderApi.queryOrderInfo("order");
        log.info("order info {}", dto);
        log.info("product service config {}", appConfig);

        JSONObject json = new JSONObject();
        json.put("code", 0);
        json.put("msg", "成功");
        json.put("order", dto);
        return json.toJSONString();
    }


}
