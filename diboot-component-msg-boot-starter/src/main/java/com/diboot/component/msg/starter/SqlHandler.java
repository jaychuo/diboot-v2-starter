package com.diboot.component.msg.starter;

import lombok.extern.slf4j.Slf4j;

/**
 * SQL处理类
 * @author Yangzhao
 * @version v2.0
 * @date 2019/08/01
 */
@Slf4j
public class SqlHandler extends com.diboot.core.starter.SqlHandler {

    // 消息SQL
    private static final String MESSAGE_SQL = "SELECT id FROM ${SCHEMA}.message WHERE id=0";
    // 消息模板SQL
    private static final String MESSAGE_TMPL_SQL = "SELECT id FROM ${SCHEMA}.message_template WHERE id=0";

    /**
     * 检查message表是否已存在
     * @return
     */
    public static boolean checkIsMessageTableExists(){
        return checkIsTableExists(MESSAGE_SQL);
    }

    /**
     * 检查message_template表是否已存在
     * @return
     */
    public static boolean checkIsMessageTmplTableExists(){
        return checkIsTableExists(MESSAGE_TMPL_SQL);
    }
}