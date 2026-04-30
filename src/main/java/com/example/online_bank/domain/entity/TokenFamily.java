package com.example.online_bank.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class TokenFamily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Boolean isBlocked;

    @ManyToOne
    @JoinColumn(name = "trusted_device_id", referencedColumnName = "id")
    private TrustedDevice trustedDevice;

    @OneToMany(mappedBy = "family")
    @ToString.Exclude
    private List<RefreshToken> refreshTokens;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
