package com.technerd.finsight.security.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refreshToken;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime lastUsedAt;

    private Boolean isValid;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
