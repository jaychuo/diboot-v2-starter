package com.diboot.component.msg.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "diboot.component.file")
public class MsgProperties {

    /**
     * 是否初始化，默认true自动安装SQL
     */
    private boolean initSql = true;
}
