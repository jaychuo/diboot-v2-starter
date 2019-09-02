package com.diboot.core.starter;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.diboot.core.plugin.PluginManager;
import com.diboot.core.util.D;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * DibootCore自动配置类
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/08/01
 */
@Configuration
@EnableConfigurationProperties(CoreProperties.class)
@ComponentScan(basePackages={"com.diboot.core"})
@MapperScan(basePackages = {"com.diboot.core.mapper"})
public class CoreAutoConfiguration{

    @Autowired
    Environment environment;

    @Autowired
    CoreProperties coreProperties;

    @Bean
    @ConditionalOnMissingBean(PluginManager.class)
    public PluginManager pluginManager(){
        PluginManager pluginManager = new PluginManager() {};
        // 检查数据库字典是否已存在
        if(coreProperties.isInitSql() && SqlHandler.checkIsTableExists(SqlHandler.DICTIONARY_SQL) == false){
            String jdbcUrl = environment.getProperty("spring.datasource.url");
            String dbName = SqlHandler.extractDatabase(jdbcUrl);
            String sqlPath = "META-INF/sql/init-core-"+dbName+".sql";
            SqlHandler.initBootstrapSql(pluginManager.getClass(), jdbcUrl, sqlPath);
        }
        return pluginManager;
    }

    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        //处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        converter.setSupportedMediaTypes(fastMediaTypes);
        // 配置转换格式
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // 设置fastjson的序列化参数：禁用循环依赖检测，数据兼容浏览器端（避免JS端Long精度丢失问题）
        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.BrowserCompatible);
        fastJsonConfig.setDateFormat(D.FORMAT_DATETIME_Y4MDHM);
        converter.setFastJsonConfig(fastJsonConfig);

        HttpMessageConverter<?> httpMsgConverter = converter;
        return new HttpMessageConverters(httpMsgConverter);
    }

}