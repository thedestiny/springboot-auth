package com.platform.migrate.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-03-28 5:03 PM
 */

@Slf4j
@Controller
public class ShortController {


    /**
     * 短链跳转
     * 302，代表临时重定向,同样的请求再次访问不会被浏览器缓存，
     * 301, 代表永久重定向,同样的请求再次访问会被浏览器缓存
     * @param shortCode
     * @return
     */
    @GetMapping("/{shortCode}")
    public RedirectView redirect(@PathVariable String shortCode) {
        String destUrl = "https://baidu.com";
        // todo some business 统计打开次数
        RedirectView view = new RedirectView(destUrl + "/" + shortCode);
        view.setStatusCode(HttpStatus.MOVED_PERMANENTLY); // 301
        view.setStatusCode(HttpStatus.MOVED_TEMPORARILY); // 302
        return view;
    }

}
