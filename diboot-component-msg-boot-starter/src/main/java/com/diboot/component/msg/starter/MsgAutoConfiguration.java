package com.diboot.component.msg.starter;

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
@EnableConfigurationProperties(MsgProperties.class)
@ComponentScan(basePackages = {"com.diboot.component.msg"})
@MapperScan(basePackages = {"com.diboot.component.msg.mapper"})
public class MsgAutoConfiguration {

    @Autowired
    MsgProperties fileProperties;

    @Autowired
    Environment environment;

    @Bean
    @ConditionalOnMissingBean(MsgPluginManager.class)
    public MsgPluginManager msgPluginManager(){
        // 初始化SCHEMA
        SqlHandler.init(environment);
        MsgPluginManager pluginManager = new MsgPluginManager() {};
        // 检查数据库字典是否已存在
        if(fileProperties.isInitSql() && SqlHandler.checkIsMessageTableExists() == false && SqlHandler.checkIsMessageTmplTableExists() == false){
            SqlHandler.initBootstrapSql(pluginManager.getClass(), environment, "msg");
        }
        return pluginManager;
    }
}
