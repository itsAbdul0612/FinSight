package com.technerd.finsight.entity;

import com.technerd.finsight.security.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Getter
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
    private BigDecimal spentAmount;

    private String month;

    private Boolean isBreached = false;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToOne
    private Category category;
}
