package com.diboot.component.excel.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "diboot.component.file.excel")
public class FileExcelProperties {

    /**
     * 是否初始化，默认true自动安装SQL
     */
    private boolean initSql = true;
}
