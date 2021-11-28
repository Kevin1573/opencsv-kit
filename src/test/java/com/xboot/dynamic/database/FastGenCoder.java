package com.xboot.dynamic.database;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.util.Collections;

public class FastGenCoder {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3308/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";
        String username = "root";
        String password = "123456";

        String moduleName = "dynamic-database-web-start";
        String projectDir  = PathGen.paths(moduleName);
        String fileSeparator = PathGen.separator;
        System.out.println(projectDir);

        String classPath = projectDir + fileSeparator + "main" + fileSeparator + "java";
        String xmlPath = projectDir + fileSeparator + "main" + fileSeparator + "resources" + fileSeparator + "mapper";

        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("xboot") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir(classPath); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.xboot.dynamic.database.modules") // 设置父包名
                            .moduleName("system") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, xmlPath)); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("user"); // 设置需要生成的表名
                    //.addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                //.templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
