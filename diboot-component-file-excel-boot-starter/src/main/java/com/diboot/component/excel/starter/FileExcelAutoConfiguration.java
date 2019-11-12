package com.diboot.component.excel.starter;

import com.diboot.component.file.starter.FileAutoConfiguration;
import com.diboot.component.file.starter.FilePluginManager;
import com.diboot.core.plugin.PluginManager;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@EnableConfigurationProperties(FileExcelProperties.class)
@ComponentScan(basePackages = {"com.diboot.component.excel"})
@MapperScan(basePackages = {"com.diboot.component.excel.mapper"})
public class FileExcelAutoConfiguration extends FileAutoConfiguration {

    @Autowired
    FileExcelProperties fileProperties;

    @Autowired
    Environment environment;

    @Bean
    @ConditionalOnMissingBean(FileExcelPluginManager.class)
    public FileExcelPluginManager fileExcelPluginManager(){
        // 初始化SCHEMA
        SqlHandler.init(environment);
        FileExcelPluginManager pluginManager = new FileExcelPluginManager() {};
        // 检查数据库字典是否已存在
        if(fileProperties.isInitSql() && SqlHandler.checkIsExcelColumnTableExists() == false && SqlHandler.checkIsExcelImportRecordTableExists() == false){
            SqlHandler.initBootstrapSql(pluginManager.getClass(), environment, "file-excel");
        }
        return pluginManager;
    }
}
