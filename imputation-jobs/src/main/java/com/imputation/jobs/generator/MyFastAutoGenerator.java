package com.imputation.jobs.generator;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 实体类生成工具
 */
public class MyFastAutoGenerator {
    public static void main(String[] args) {
        List<String> tables = new ArrayList<>();
        tables.add("jobs");
        tables.add("files");
        tables.add("file_chunk");
        tables.add("users");


        /**
         * url:数据库链接地址
         * username:数据库账号
         * password：数据库密码
         */
        FastAutoGenerator.create("jdbc:mysql://39.103.140.193:3307/imputation?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true","root","qwe@11")
                .globalConfig(builder -> {
                    builder.author("fansp")//作者
                            .outputDir("E:\\IdeaProjects\\imputation\\meta-imputation\\imputation-jobs\\src\\main\\java")    //输出路径(写到java目录)
                            .enableSwagger()//开启swagger
                            .commentDate("yyyy-MM-dd")
                            .fileOverride();//开启覆盖之前生成的文件

                })
                .packageConfig(builder -> {
                    builder.parent("com.imputation.jobs")
                            .moduleName("practice")
                            .entity("entity")
                            .service("bo")
                            .serviceImpl("bo.impl")
//                            .controller("controller")
                            .mapper("mapper")
                            .xml("mapper")
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml,"E:\\IdeaProjects\\imputation\\meta-imputation\\imputation-jobs\\src\\main\\resources\\mapper"));
                })
                .strategyConfig(builder -> {//策略配置
                    builder.addInclude(tables)//要生成代码的表名
//                            .addTablePrefix("prs_")//过滤表名前缀
                            .serviceBuilder()//service策略配置
                            .formatServiceFileName("%sBo")//bo类名 %根据表名替换
                            .formatServiceImplFileName("%sBoImpl")//同上
                            .entityBuilder()//实体类策略配置
                            .enableLombok()//开启lombok
                            .logicDeleteColumnName("deleted")//说明逻辑删除是哪个字段
                            .enableTableFieldAnnotation()//属性上加上注解
//                            .controllerBuilder()//controller策略
//                            .formatFileName("%sController")//controller类名 %根据表名替换
//                            .enableRestStyle()//开启RestController
                            .mapperBuilder()//mapper策略
                            .enableBaseResultMap()  //生成通用的resultMap
                            .superClass(BaseMapper.class)//继承那个类
                            .formatMapperFileName("%sMapper")
                            .enableMapperAnnotation()//@mapper开启
                            .formatXmlFileName("%sMapper");//xml名
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();

    }
}
