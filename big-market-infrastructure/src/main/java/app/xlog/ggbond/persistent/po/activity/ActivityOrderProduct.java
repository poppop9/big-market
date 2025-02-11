package app.xlog.ggbond.persistent.po.activity;

import app.xlog.ggbond.persistent.po.SingleTableBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动单商品
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ActivityOrderProduct", indexes = {
        @Index(columnList = "activityOrderProductId"),
})
public class ActivityOrderProduct extends SingleTableBaseEntity {
    private Long activityOrderProductId;  // 活动单商品id
    private String activityOrderProductName;  // 活动单商品名称
    private Double activityOrderProductPrice;  // 活动单商品价格
}