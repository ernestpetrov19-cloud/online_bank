package com.example.online_bank.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class DeviceChallenge {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column
    private String deviceName;
    @Column
    private String deviceId;
    @Column
    private String userAgent;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
