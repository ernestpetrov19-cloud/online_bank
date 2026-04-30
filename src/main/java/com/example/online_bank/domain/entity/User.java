package com.example.online_bank.domain.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * Класс пользователь с атрибутами: телефоном, фио, случайно сгенерированным UUID (UUID.randomUUID()).
 * Генерируется случайный пин-код.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "user_bank")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column
    private UUID uuid;

    @Column
    private String passwordHash;

    @Column()
    private Integer failedAttempts;

    @Column
    private Boolean isBlocked;

    @Column
    private LocalDateTime blockedExpiredAt;

    @Column(unique = true)
    @Email
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    @Column
    private String name;

    @Column
    private String surname;

    @Column
    private String patronymic;

    @Column
    private Boolean isVerified;

    @ToString.Exclude
    @OneToMany(mappedBy = "holder", cascade = REMOVE, orphanRemoval = true, fetch = LAZY)
    private List<Account> accounts;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", fetch = LAZY, cascade = REMOVE)
    private List<VerificationCode> verificationCode;

    @ManyToMany()
    @ToString.Exclude
    @JoinTable(
            name = "role_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = LAZY, cascade = REMOVE)
    private List<TrustedDevice> trustedDevice;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", fetch = LAZY, cascade = REMOVE)
    private List<TokenFamily> tokenFamilies;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", fetch = LAZY, cascade = REMOVE)
    private List<UserCategoryStats> userCategoryStats;

    @ToString.Exclude
    @OneToMany(fetch = LAZY, mappedBy = "user", cascade = REMOVE)
    private List<UserQuest> userQuest;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = REMOVE)
    private List<DeviceChallenge> deviceChallenges;
}
