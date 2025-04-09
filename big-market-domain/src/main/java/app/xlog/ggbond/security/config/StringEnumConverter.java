package app.xlog.ggbond.security.config;

import app.xlog.ggbond.security.model.UserPurchaseHistoryBO;
import cn.idev.excel.converters.Converter;
import cn.idev.excel.converters.ReadConverterContext;

import java.util.EnumSet;

/**
 * Excel - String 转枚举
 */
public class StringEnumConverter implements Converter<UserPurchaseHistoryBO.PurchaseCategory> {
    @Override
    public UserPurchaseHistoryBO.PurchaseCategory convertToJavaData(ReadConverterContext<?> context) {
        // 从 Excel 中读取数据，转换为枚举
        String stringValue = context.getReadCellData().getStringValue();
        for (UserPurchaseHistoryBO.PurchaseCategory purchaseCategory : EnumSet.allOf(UserPurchaseHistoryBO.PurchaseCategory.class)) {
            if (purchaseCategory.getInfo().equals(stringValue)){
                return purchaseCategory;
            }
        }
        return null;
    }
}
