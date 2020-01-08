package com.diboot.component.excel.anno;

import java.lang.annotation.*;

/**
 * 标记枚举字段
 *
 * @author : uu
 * @version v1.0
 * @Date 2020-01-07  17:23
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelField {

    /**
     * 转化的枚举类型
     * @return
     */
    Class<? extends IEnum> value();

}
