package com.platform.authmpg;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @Description
 * @Author liangkaiyang
 * @Date 2024-11-21 4:01 PM
 */
public class CodeGenerateMPG {


    public static void main(String[] args) {

        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/knead-business/src/main/java");
        gc.setAuthor("kai yang");
        gc.setOpen(false);
        // 是否覆盖项目
        gc.setFileOverride(true);
        gc.setActiveRecord(false);
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setBaseResultMap(true);
        gc.setBaseColumnList(true);
        gc.setDateType(DateType.ONLY_DATE);
        gc.setMapperName("%sMapper");

        gc.setSwagger2(true); //实体属性 Swagger2 注解
        mpg.setGlobalConfig(gc);

        List<TableFill> tableFillList = new ArrayList<>();
        TableFill createField = new TableFill("createTime", FieldFill.INSERT);
        TableFill modifiedField = new TableFill("updateTime", FieldFill.INSERT_UPDATE);
        tableFillList.add(createField);
        tableFillList.add(modifiedField);


        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();

        // brew services start mysql
        dsc.setUrl("jdbc:mysql://localhost:3306/waimai?useUnicode=true&useSSL=false&characterEncoding=utf8");
        dsc.setUsername("root");
        dsc.setPassword("123456");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");


        mpg.setDataSource(dsc);

        dsc.setTypeConvert(new MySqlTypeConvert() {
            @Override
            public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                //System.out.println("转换类型：" + fieldType);
                if ("datetime".equals(fieldType)) {
                    //datetime转换成java.util.Date类型
                    globalConfig.setDateType(DateType.ONLY_DATE);
                }
                return super.processTypeConvert(globalConfig, fieldType);
            }
        });


        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        String pack = "com.platform.business";

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName("");
        pc.setParent(pack);
        pc.setController("controller");
        pc.setEntity("domain");
        pc.setMapper("mapper");
        pc.setService("service");
        pc.setServiceImpl("service.impl");

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
//
        // 自定义controller的代码模板
        String entityTpl = "/tpl/entity.java.ftl";
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(entityTpl) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String expand = projectPath + "/knead-business/src/main/java/" + transPath(pc.getParent()) + File.separator + pc.getEntity();
                return String.format((expand + File.separator + "%s" + ".java"), tableInfo.getEntityName());
            }
        });

        // 自定义controller的代码模板
        String mapperTpl = "/tpl/mapper.java.ftl";
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(mapperTpl) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 + pc.getModuleName()
                modify(tableInfo);
                String expand = projectPath + "/knead-business/src/main/java/" + transPath(pc.getParent()) + File.separator + pc.getMapper();
                return String.format((expand + File.separator + "%s" + ".java"), tableInfo.getMapperName());
            }
        });

        String xmlTpl = "/tpl/mapper.xml.ftl";
        // 自定义配置会被优先输出  + pc.getModuleName()
        focList.add(new FileOutConfig(xmlTpl) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/knead-business/src/main/resources/mapper/business"
                        + "/" + tableInfo.getXmlName() + StringPool.DOT_XML;
            }
        });
//
//
        // // 自定义controller的代码模板
        // String serviceTpl = "/tpl/service.java.ftl";
        // // 自定义配置会被优先输出
        // focList.add(new FileOutConfig(serviceTpl) {
        //     @Override
        //     public String outputFile(TableInfo tableInfo) {
        //         // 自定义输出文件名 + pc.getModuleName()
        //         modify(tableInfo);
        //         String expand = projectPath + "/src/main/java/" + pc.getParent() + File.separator + pc.getService();
        //         return String.format((expand + File.separator + "%s" + ".java"), tableInfo.getServiceName());
        //     }
        // });
//
        // // 自定义controller的代码模板
        // String serviceImplTpl = "/tpl/serviceImpl.java.ftl";
        // // 自定义配置会被优先输出
        // focList.add(new FileOutConfig(serviceImplTpl) {
        //     @Override
        //     public String outputFile(TableInfo tableInfo) {
        //         // 自定义输出文件名 + pc.getModuleName()
        //         modify(tableInfo);
        //         String expand = projectPath + "/src/main/java/" + pc.getParent() + File.separator + pc.getServiceImpl();
        //         return String.format((expand + File.separator + "%s" + ".java"), tableInfo.getServiceImplName());
        //     }
        // });
//
//
        // // 自定义controller的代码模板
        // String conImplTpl = "/tpl/controller.java.ftl";
        // // 自定义配置会被优先输出
        // focList.add(new FileOutConfig(conImplTpl) {
        //     @Override
        //     public String outputFile(TableInfo tableInfo) {
        //         // 自定义输出文件名 + pc.getModuleName()
        //         modify(tableInfo);
        //         String expand = projectPath + "/src/main/java/" + pc.getParent() + File.separator + pc.getController();
        //         return String.format((expand + File.separator + "%s" + ".java"), tableInfo.getControllerName());
        //     }
        // });
//

//
//
        // // 如果模板引擎是 freemarker /tpl/mapper.xml.ftl
        // String xmlTpl = "/templates/mapper.xml.ftl";


        cfg.setFileOutConfigList(focList);
        // 使用自定义模板
        mpg.setCfg(cfg);

        // TableInfo

        mpg.setPackageInfo(pc);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        templateConfig.setEntity("");
        // templateConfig.setService(null);
        templateConfig.setController("");
        // templateConfig.setServiceImpl(null);
        templateConfig.setMapper("");
        templateConfig.setXml("");
        templateConfig.setEntityKt("");
        // templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        //strategy.setSuperEntityClass("com.destiny.treasure.base.BaseEntity");
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        // table fill 策略
        // strategy.setTableFillList(tableFillList);
        // 公共父类
        // 排除哪些表
        // strategy.setExclude();
        // 包含哪些表
        // strategy.setInclude("victory_focus_fund");
//        strategy.setSuperServiceClass("com.destiny.treasure.base.BaseService");
//        strategy.setSuperControllerClass("com.destiny.treasure.base.BaseController");
//        strategy.setSuperServiceImplClass("com.destiny.treasure.base.BaseServiceImpl");
        strategy.setEntitySerialVersionUID(true);
        // 写于父类中的公共字段
        // strategy.setSuperEntityColumns("id");

        // String[] split = TestUtils.sl.split("\n");
        // strategy.setInclude();
        //strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix("t_");
//        strategy.setInclude("tb_item_order","tb_item_order_detail", "tb_item_refund", "tb_item_refund_detail", "tb_trade_order");

        strategy.setInclude("t_cms_article");
        mpg.setStrategy(strategy);

        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();


    }

    public static void modify(TableInfo tableInfo) {
        // tableInfo.setEntityName(tableInfo.getEntityName().substring(1));
//        tableInfo.setMapperName(tableInfo.getMapperName().substring(1));
//        tableInfo.setServiceImplName(tableInfo.getServiceImplName().substring(1));
//        tableInfo.setControllerName(tableInfo.getControllerName().substring(1));
    }

    public static String transPath(String pth){
        return pth.replaceAll("\\.", File.separator);

    }


}
