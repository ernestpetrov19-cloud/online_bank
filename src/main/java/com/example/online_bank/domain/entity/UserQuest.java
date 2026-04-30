package com.example.online_bank.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * Сущность UserQuest (Связка и прогресс)
 * Эта таблица «раздает» квесты конкретным людям.
 */
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserQuest {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "quest_id", referencedColumnName = "id")
    private Quest quest;

    @Column()
    private Boolean isComplete;

    @Column()
    private Integer userProgress;
}
