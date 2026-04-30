package com.example.online_bank.repository;

import com.example.online_bank.domain.entity.VerificationCode;
import com.example.online_bank.enums.CodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    List<VerificationCode> findAllByExpiresAtBefore(LocalDateTime now);

    /**
     * Найти код по id пользователя и типу кода, код не верифицирован и время истечения БОЛЬШЕ (наступит позже) времени,
     * приведенным в параметре
     *
     * @param code        Код верификации
     * @param userId      Id пользователя
     * @param type        Тип кода (EMAIL, PHONE, RESET)
     * @param currentTime Текущее время для проверки
     * @return {@link Optional} с найденным кодом или пустой, если код не найден/истек
     * @implNote Метод ищет коды, которые:
     * <ul>
     *   <li>Соответствуют указанному коду и пользователю</li>
     *   <li>Имеют нужный тип</li>
     *   <li>Еще не были использованы (isVerified = false)</li>
     *  <li>Время истечения больше указанного времени (expires_at > currentTime)</li>
     * </ul>
     * @example т.е. время истечения - 15:00, а время в параметре - 14:30.
     * Время истечение > времени в параметре, значит запись найдется
     * IsAfter - позже
     */
    Optional<VerificationCode> findByVerificationCodeAndUser_IdAndCodeTypeAndIsVerifiedIsFalseAndExpiresAtAfter(
            String code,
            Long userId,
            CodeType type,
            LocalDateTime currentTime
    );

    /**
     * Найти все коды, где время истечения раньше времени в параметре
     *
     * @example Время истечения - 15:00, время в параметре - 15:30. Время истечения < времени в параметре, значит
     * код истёк. AtBefore - раньше
     */
    List<VerificationCode> findAllByExpiresAtBeforeAndUser_Id(LocalDateTime currentTime, Long userId);

    @Modifying
    void deleteAllByIsVerifiedTrueAndUser_id(Long user_id);

    Optional<VerificationCode> findVerifiedCodeByVerificationCode(String verificationCode);

    @Modifying
    @Query("""
            update VerificationCode v
            set
            v.verificationCode = :newVerificationCode,
            v.expiresAt =:newExpDate
            where
            v.user.id = (select u.id from User u where u.email = :userEmail)
            """)
    void updateVerifiedCodeByUser_Email(
            @Param("userEmail") String userEmail,
            @Param("newVerificationCode") String newVerificationCode,
            @Param("newExpDate") LocalDateTime newExpDate
    );
}
