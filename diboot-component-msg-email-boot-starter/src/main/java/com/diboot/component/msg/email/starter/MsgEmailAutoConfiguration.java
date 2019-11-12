package com.diboot.component.msg.email.starter;

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
@EnableConfigurationProperties(MsgEmailProperties.class)
@ComponentScan(basePackages = {"com.diboot.component.msg.email"})
@MapperScan(basePackages = {"com.diboot.component.msg.email.mapper"})
public class MsgEmailAutoConfiguration {

    @Autowired
    MsgEmailProperties msgEmailProperties;

    @Autowired
    Environment environment;

    @Bean
    @ConditionalOnMissingBean(MsgEmailPluginManager.class)
    public MsgEmailPluginManager msgEmailPluginManager(){
        SqlHandler.init(environment);
        MsgEmailPluginManager pluginManager = new MsgEmailPluginManager() {};
        return pluginManager;
    }
}
