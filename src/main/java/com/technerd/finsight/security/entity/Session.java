package com.technerd.finsight.security.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @NotNull
    private String refreshToken;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    private Boolean isValid;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
