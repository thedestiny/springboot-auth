package com.platform.productserver;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 模板项目生成内容 mpg
 * @Description
 * @Date 2022-08-17 5:28 PM
 */

@Slf4j
public class MybatisGenerator {

    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }


    public static void main(String[] args) {


        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        String property = System.getProperty("user.dir");
        String content = FileUtil.readString(property + "/pom1.xml", "UTF-8");
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = "/Users/admin/Desktop/winsome";//
        FileUtil.del(projectPath);

        String groupId = "com.platform";
        String artifactId = "linaea";

        content = content.replace("${groupId}", groupId);
        content = content.replace("${artifactId}", artifactId);
        content = content.replace("${proName}", artifactId);
        FileUtil.writeString(content, projectPath + "/pom.xml", "UTF-8");

        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("destiny");
        gc.setOpen(false);
        gc.setServiceName("%sService");
        gc.setBaseResultMap(true);
        gc.setEnableCache(true);
        gc.setIdType(IdType.AUTO);
        gc.setBaseColumnList(true);
        gc.setActiveRecord(false);
        gc.setEnableCache(false);
        gc.setSwagger2(false);
        // 设置时间类型 为 Date
        gc.setDateType(DateType.ONLY_DATE);

        //gc.setSwagger2(true); // 实体属性 Swagger2 注解
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/account?useUnicode=true&useSSL=false&characterEncoding=utf8");
        // dsc.setSchemaName("public");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("123456");
        mpg.setDataSource(dsc);


        // 包配置
        PackageConfig pc = new PackageConfig();
        // pc.setModuleName("com.platform.linaea");
        // pc.setParent("com.baomidou.ant");
        pc.setParent(groupId + "." + artifactId);
        pc.setController("web");
        pc.setEntity("entity");
        pc.setMapper("mapper");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                ConfigBuilder config = getConfig();
                Map<String, String> packageInfo = config.getPackageInfo();
                packageInfo.put("req", groupId + "." + artifactId + ".domain");
            }
        };

        String source = "/tpl";
        // 如果模板引擎是 freemarker
        String templatePath = source + "/mapper.xml.ftl";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });

        focList.add(new FileOutConfig(source + "/entity_req.java.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/java/" + pc.getParent().replace(".", "/")
                        + "/domain/" + tableInfo.getEntityName() + "Req" + StringPool.DOT_JAVA;
            }
        });

        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();
        templateConfig.setEntity(source + "/entity.java");
        templateConfig.setController(source + "/controller.java");
        templateConfig.setService(source + "/service.java");
        templateConfig.setServiceImpl(source + "/serviceImpl.java");
        templateConfig.setMapper(source + "/mapper.java");

        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // strategy.setSuperEntityClass("你自己的父类实体,没有就不用设置!");
        // strategy.setSuperEntityClass("com.platform.linaea.base.BaseEntity");
        // strategy.setSuperControllerClass("com.platform.linaea.base.BaseController");
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        // 公共父类
        // strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
        // 写于父类中的公共字段
        // strategy.setSuperEntityColumns("id");
        // strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
        strategy.setInclude("t_order");
        // strategy.setSuperEntityColumns("create_time", "update_time", "id");
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix("t_");
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        // getObjectMap()
        mpg.execute();

        String config = FileUtil.readString(property + "/application1.yml", "UTF-8");
        FileUtil.writeString(config, projectPath + "/src/main/resources/application.yml", "UTF-8");
    }
}
