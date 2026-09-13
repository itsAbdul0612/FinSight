package com.technerd.finsight.entity;

import com.technerd.finsight.security.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String icon; // emoji or icon key.

    private Boolean isDefault;

    @ManyToOne
    private User user;

}
