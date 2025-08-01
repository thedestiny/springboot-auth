package com.platform.authmpg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Slf4j
@EnableAsync
@EnableCaching
@EnableScheduling
@EnableAspectJAutoProxy
@SpringBootApplication
@EnableTransactionManagement
public class AuthMpgApplication {

    public static void main(String[] args) {
        //
        //// @Autowired, @Value, @Inject
        //AutowiredAnnotationBeanPostProcessor bp = new AutowiredAnnotationBeanPostProcessor();
        //// @Resource, @PostConstruct, @PreDestroy
        //CommonAnnotationBeanPostProcessor cbp = new CommonAnnotationBeanPostProcessor();
        //// @Configuration, @ComponentScan, @Bean, @Import, @ImportResource, @PropertySource, (触发扫描 @Component)
        //ConfigurationClassPostProcessor ccp = new ConfigurationClassPostProcessor();

        log.info("start app !");
        SpringApplication.run(AuthMpgApplication.class, args);
    }

}
