package com.technerd.finsight.core.category;

import com.technerd.finsight.user.User;
import com.technerd.finsight.core.transaction.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String icon; // emoji or icon key.

    private Boolean isActive = Boolean.TRUE;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @ManyToOne
    private User user;

}
