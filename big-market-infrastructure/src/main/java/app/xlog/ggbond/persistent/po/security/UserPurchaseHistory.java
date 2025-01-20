package app.xlog.ggbond.persistent.po.security;

import app.xlog.ggbond.persistent.po.ShardingTableBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 用户购买历史
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "UserPurchaseHistory", indexes = {
        @Index(columnList = "userId"),
})
public class UserPurchaseHistory extends ShardingTableBaseEntity {
    private Long userId;  // 用户id
    private String purchaseName;  // 商品名称
    private PurchaseCategory purchaseCategory;  // 商品类型
    private double purchasePrice;  // 商品价格
    private Long purchaseCount;  // 购买数量
    private Long purchaseTimes;  // 此次是第几次购买
    private @Builder.Default boolean isReturn = false;  // 是否退货

    /**
     * 商品类型
     */
    @Getter
    @AllArgsConstructor
    public enum PurchaseCategory {
        FOOD("食品"),
        HOME("家居"),
        DIGITAL("数码"),
        TRAPPINGS("服饰"),
        MAKE_UP("美妆"),
        MOTHER_AND_BABY("母婴"),
        CAR("汽车"),
        SPORTS("运动"),
        BOOKS("图书"),
        MEDICINES_AND_HEALTHCARE("药品保健"),
        OFFICE("办公"),
        HORTICULTURE("园艺"),
        PET("宠物"),
        ;

        private final String info;
    }

    /**
     * 对字段的中文翻译
     */
    @Getter
    @AllArgsConstructor
    public enum FieldName {
        USER_ID("userId", "用户id"),
        PURCHASE_NAME("purchaseName", "商品名称"),
        PURCHASE_CATEGORY("purchaseCategory", "商品类型"),
        PURCHASE_PRICE("purchasePrice", "商品价格"),
        PURCHASE_COUNT("purchaseCount", "购买数量"),
        PURCHASE_TIMES("purchaseTimes", "购买次数"),
        IS_RETURN("isReturn", "是否退货"),
        ;

        private final String english;
        private final String chinese;

        /**
         * 根据英文字段名获取中文字段名
         */
        public static String getChinese(String english) {
            for (FieldName fieldName : FieldName.values()) {
                if (fieldName.english.equals(english)) {
                    return fieldName.chinese;
                }
            }
            return null;
        }
    }


}
