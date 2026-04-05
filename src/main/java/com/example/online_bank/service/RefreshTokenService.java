package com.example.online_bank.service;

import com.example.online_bank.domain.entity.RefreshToken;
import com.example.online_bank.domain.entity.TokenFamily;
import com.example.online_bank.enums.TokenStatus;
import com.example.online_bank.repository.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.online_bank.enums.TokenStatus.CREATED;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void createRefreshTokenEntity(
            String token,
            TokenFamily tokenFamily,
            LocalDateTime expiredAt,
            LocalDateTime createdAt
    ) {
        try {
            String tokenUuid = jwtService.getJwtTokenUuid(token);

            RefreshToken refreshToken = RefreshToken.builder()
                    //fixme пока не хэшируется
                    .tokenHash(bCryptPasswordEncoder.encode(token))
                    .expiresAt(expiredAt)
                    .revokedAt(null)
                    .createdAt(createdAt)
                    .status(CREATED)
                    .uuidHash(tokenUuid)
                    .family(tokenFamily)
                    .build();
            save(refreshToken);
        } catch (JwtException e) {
            log.error(e.getMessage());
            throw new BadCredentialsException(e.getMessage());
        }
    }

    public void save(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    public void revoke(RefreshToken refreshToken) {
        refreshToken.setStatus(TokenStatus.REVOKED);
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findByUUidHash(String hashUuid) {
        return refreshTokenRepository.findByUuidHash(hashUuid).orElseThrow(EntityNotFoundException::new);
    }

    public void revokeAllByFamily(TokenFamily family) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByFamily(family);
        tokens.forEach(token -> {
            token.setRevokedAt(LocalDateTime.now());
            token.setStatus(TokenStatus.REVOKED);
            refreshTokenRepository.save(token);
        });
    }

    public RefreshToken parseToken(String token) {
        String uuid = jwtService.getJwtTokenUuid(token);
        return findByUUidHash(uuid);
    }
}
