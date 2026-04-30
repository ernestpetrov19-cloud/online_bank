package com.example.online_bank.domain.entity;

import com.example.online_bank.enums.TokenStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column
    private String tokenHash;

    @Column
    private LocalDateTime expiresAt;

    @Column
    private LocalDateTime revokedAt;

    @Column
    private LocalDateTime createdAt;

    @Column
    @Enumerated(STRING)
    private TokenStatus status;

    @Column
    private String uuid;

    @ManyToOne()
    @JoinColumn(name = "family_id")
    private TokenFamily family;
}
