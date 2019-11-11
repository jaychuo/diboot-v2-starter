package com.diboot.component.excel.starter;

import lombok.extern.slf4j.Slf4j;

/**
 * SQL处理类
 * @author Yangzhao
 * @version v2.0
 * @date 2019/08/01
 */
@Slf4j
public class SqlHandler extends com.diboot.core.starter.SqlHandler {

    // 列定义SQL
    private static final String EXCEL_COLUMN_SQL = "SELECT id FROM ${SCHEMA}.excel_column WHERE id=0";
    // Excel导入记录SQL
    private static final String EXCEL_IMPORT_RECORD_SQL = "SELECT id FROM ${SCHEMA}.excel_import_record WHERE id=0";

    /**
     * 检查excel_import_record表是否已存在
     * @return
     */
    public static boolean checkIsExcelColumnTableExists(){
        return checkIsTableExists(EXCEL_COLUMN_SQL);
    }

    /**
     * 检查excel_column表是否已存在
     * @return
     */
    public static boolean checkIsExcelImportRecordTableExists(){
        return checkIsTableExists(EXCEL_IMPORT_RECORD_SQL);
    }
}