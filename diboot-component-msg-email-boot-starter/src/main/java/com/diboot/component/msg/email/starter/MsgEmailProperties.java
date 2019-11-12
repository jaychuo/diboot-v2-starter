package com.diboot.component.msg.email.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "diboot.component.file")
public class MsgEmailProperties {

}
