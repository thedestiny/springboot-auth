package com.platform.productserver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReferenceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
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

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 模板项目生成内容 mpg
 *
 * @Description
 * @Date 2022-08-17 5:28 PM
 */

@Slf4j
public class MybatisGenerator {

    public static void main(String[] args) {

        // 项目生成地址
        String projectPath = "/Users/admin/Desktop/trade03";
        // groupId 和 artifactId
        String groupId = "com.platform";
        String artifactId = "book";
        String author = "kaiyang";

        String url = "localhost:3306/ershoushu";
        String username = "root";
        String password = "123456";


        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        String property = System.getProperty("user.dir");

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor(author);
        gc.setOpen(false);
        gc.setServiceName("%sService");
        gc.setBaseResultMap(true);
        gc.setEnableCache(true);
        gc.setIdType(IdType.AUTO);
        gc.setBaseColumnList(true);
        gc.setActiveRecord(false);
        gc.setEnableCache(false);
        gc.setSwagger2(false);
        gc.setKotlin(false);

        // 设置时间类型 为 Date
        gc.setDateType(DateType.ONLY_DATE);

        //gc.setSwagger2(true); // 实体属性 Swagger2 注解
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(StrUtil.format("jdbc:mysql://{}?useUnicode=true&useSSL=false&characterEncoding=utf8", url));
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername(username);
        dsc.setPassword(password);
        mpg.setDataSource(dsc);
        // 包配置
        PackageConfig pc = new PackageConfig();
        // pc.setModuleName("com.platform.linaea");
        pc.setParent(groupId + "." + artifactId);
        pc.setController("web");
        pc.setEntity("entity.order");
        pc.setMapper("mapper");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        mpg.setPackageInfo(pc);

        AppEngine engine = new AppEngine();
        Map<String, Object> parameters = new HashMap<>();
        // 是否使用 mybatis-plus
        parameters.put("mybatisPlusFlag", false);
        engine.setParameters(parameters);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                ConfigBuilder config = getConfig();
                // 设置不同的扩展信息
                Map<String, String> packageInfo = config.getPackageInfo();
                packageInfo.put("req", groupId + "." + artifactId + ".dto");
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
                modify(tableInfo);
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
                        + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });

        focList.add(new FileOutConfig(source + "/entity_req.java.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/java/" + pc.getParent().replace(".", "/")
                        + "/dto/" + tableInfo.getEntityName() + "Req" + StringPool.DOT_JAVA;
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
        templateConfig.setXml(source + "/mapper.xml");
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        // strategy.setSuperEntityClass("你自己的父类实体,没有就不用设置!");
        // strategy.setSuperEntityClass("com.platform.linaea.base.BaseEntity");
        // strategy.setSuperControllerClass("com.platform.linaea.base.BaseController");

        // 公共父类
        // strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
        // 写于父类中的公共字段
        // strategy.setSuperEntityColumns("id");
        // strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
        // strategy.setInclude("tb_name", "tb_name2", "tb_name3");
        // strategy.setSuperEntityColumns("create_time", "update_time", "id");
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix("t_","lfb_");
        strategy.setEntitySerialVersionUID(true);
        // 写于父类中的公共字段
        // strategy.setSuperEntityColumns("id", "update_time", "create_time");
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(engine);
        // getObjectMap()

        String content = FileUtil.readString(property + "/pom1.xml", Charset.forName("UTF-8"));
        FileUtil.del(projectPath);
        mpg.execute();

        content = content.replace("${groupId}", groupId);
        content = content.replace("${artifactId}", artifactId);
        content = content.replace("${proName}", artifactId);
        // 添加 pom git readme 以及配置文件
        FileUtil.writeString(content, projectPath + "/pom.xml", "UTF-8");
        FileUtil.writeString(".idea\ntarget\n*.iml\n*.log", projectPath + "/.gitignore",  Charset.forName("UTF-8"));
        FileUtil.writeString("#### reademe", projectPath + "/README.md", "UTF-8");
        String config = FileUtil.readString(property + "/application1.yml",  Charset.forName("UTF-8"));
        FileUtil.writeString(config, projectPath + "/src/main/resources/application.yml",  Charset.forName("UTF-8"));
    }

    /**
     * 修改 table_info 信息
     *
     * @param tableInfo
     */
    public static void modify(TableInfo tableInfo) {

        String tableName = tableInfo.getEntityName();
        tableInfo.setConvert(true);
        // ReflectUtil.setFieldValue(, , );

    }


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

}
