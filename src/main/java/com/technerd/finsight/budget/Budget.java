package com.technerd.finsight.budget;

import com.technerd.finsight.category.Category;
import com.technerd.finsight.security.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Service
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","category_id","month"}))
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 12, scale = 2)
    private BigDecimal allocatedAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal spentAmount = BigDecimal.ZERO;

    private String month;

    private Boolean isBreached = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Category category;

}
