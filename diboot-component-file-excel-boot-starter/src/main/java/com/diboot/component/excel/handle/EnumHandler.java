package com.diboot.component.excel.handle;


import com.diboot.component.excel.anno.IEnum;

import java.util.Arrays;

/**
 * 枚举统一助手类
 *
 * @author : uu
 * @version v1.0
 * @Description: 枚举工具类
 * @Date 2019-04-15  21:45
 */
public class EnumHandler {

    /**
     * <h3>根据itemName获取itemValue</h3>
     *
     * @param itemName 需要枚举
     * @param tClass   枚举
     * @return 返回T类型的枚举
     */
    public static <T extends IEnum> T findItemValueByItemName(String itemName, Class<T> tClass) {
        return Arrays.stream(tClass.getEnumConstants())
                .filter(codeEnum -> codeEnum.getItemName().equals(itemName))
                .findFirst()
                //操作不成功，统一返回NULL
                .orElse(null);
    }

    /**
     * <h3>根据itemValue获取itemName</h3>
     *
     * @param itemValue 需要枚举
     * @param tClass   枚举
     * @return 返回T类型的枚举
     */
    public static <T extends IEnum> T findItemNameByItemValue(String itemValue, Class<T> tClass) {
        return Arrays.stream(tClass.getEnumConstants())
                .filter(codeEnum -> codeEnum.getItemValue().equals(itemValue))
                .findFirst()
                //操作不成功，统一返回NULL
                .orElse(null);
    }
}
