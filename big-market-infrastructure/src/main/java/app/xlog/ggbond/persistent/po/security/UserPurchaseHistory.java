package app.xlog.ggbond.persistent.po.security;

import app.xlog.ggbond.persistent.po.ShardingTableBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Table(name = "UserPurchaseHistory")
public class UserPurchaseHistory extends ShardingTableBaseEntity {
    private Long userId;  // 用户id
    private String purchaseName;  // 商品名称
    private String purchaseCategory;  // 商品类型（生鲜、家居、数码 ……）
    private double purchasePrice;  // 商品价格
    private boolean purchaseCount;  // 购买数量
    private boolean purchaseTimes;  // 此次是第几次购买
    @Builder.Default
    private boolean isReturn = false;  // 是否退货
}
