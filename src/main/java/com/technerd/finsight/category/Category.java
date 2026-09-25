package com.technerd.finsight.category;

import com.technerd.finsight.security.entity.User;
import com.technerd.finsight.transaction.enums.TransactionType;
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

    @Column(nullable = false)
    private TransactionType transactionType;



    @ManyToOne
    private User user;

}
