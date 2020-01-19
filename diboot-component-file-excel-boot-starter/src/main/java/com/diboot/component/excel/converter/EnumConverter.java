package com.diboot.component.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.diboot.component.excel.anno.ExcelField;
import com.diboot.component.excel.anno.IEnum;
import com.diboot.component.excel.handle.EnumHandler;
import com.diboot.core.util.V;

import java.util.Arrays;

/**
 * 枚举转化器
 *
 * @author : uu
 * @version : v1.0
 * @Date 2020-01-07  16:53
 */
public class EnumConverter implements Converter<String> {


    @Override
    public Class supportJavaTypeKey() {
        return IEnum.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public String convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String colName = contentProperty.getHead().getHeadNameList().get(0);
        //获取枚举类型
        ExcelField excelField = contentProperty.getField().getAnnotation(ExcelField.class);
        IEnum iEnum = EnumHandler.findItemValueByItemName(cellData.getStringValue(), excelField.value());
        if (V.notEmpty(cellData.getStringValue())) {
            if (V.isEmpty(iEnum)) {
                StringBuffer stringBuffer = new StringBuffer();
                Arrays.stream(excelField.value().getEnumConstants()).forEach(
                        item -> stringBuffer.append(item.getItemName()).append(",")
                );
                throw new Exception("[" + colName + "]列数据格式有误，只允许填写：" + stringBuffer.toString());
            } else {
                return iEnum.getItemValue();
            }
        }

        return null;
    }

    @Override
    public CellData convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        ExcelField excelField = contentProperty.getField().getAnnotation(ExcelField.class);
        IEnum iEnum = EnumHandler.findItemNameByItemValue(value, excelField.value());
        if (V.notEmpty(value)) {
            if (V.isEmpty(iEnum)) {
                StringBuffer stringBuffer = new StringBuffer();
                Arrays.stream(excelField.value().getEnumConstants()).forEach(
                        item -> stringBuffer.append(item.getItemValue()).append(",")
                );
                throw new Exception("数据库格式有误，只允许填写：" + stringBuffer.toString());
            } else {
                return new CellData(iEnum.getItemName());
            }
        }
        return new CellData("");
    }
}
