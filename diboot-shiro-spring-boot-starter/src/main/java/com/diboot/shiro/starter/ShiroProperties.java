package com.diboot.shiro.starter;

import com.diboot.shiro.authz.config.AuthConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * shiro相关配置文件
 * @author : wee
 * @version : v1.0
 * @Date 2019-10-11  10:55
 */
@ConfigurationProperties(prefix = "diboot.shiro")
public class ShiroProperties {

    @NestedConfigurationProperty
    private AuthConfiguration configuration = new AuthConfiguration();

    public AuthConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(AuthConfiguration configuration) {
        this.configuration = configuration;
    }
}
