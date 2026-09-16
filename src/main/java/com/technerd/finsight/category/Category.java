package com.technerd.finsight.category;

import com.technerd.finsight.security.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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

    private Boolean isDefault = false; // This field needs be taken care of, can't have so many false in db.

//    @CreationTimestamp
//    private LocalDateTime createdAt;

    @ManyToOne
    private User user;

}
