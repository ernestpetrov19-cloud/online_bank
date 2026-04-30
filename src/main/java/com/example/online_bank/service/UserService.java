package com.example.online_bank.service;

import com.example.online_bank.domain.entity.User;
import com.example.online_bank.repository.UserRepository;
import com.example.online_bank.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email);
        return new CustomUserDetails(user);
    }

    public void verifyUser(User user) {
        user.setIsVerified(true);
        userRepository.save(user);
    }

    public boolean existsByPhoneNumber(String number) {
        return userRepository.existsUserByPhoneNumber(number);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByUuid(UUID userUuid) {
        return userRepository.findByUuid(userUuid);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    @Transactional
    public void deleteByPhoneNumber(String number) {
        userRepository.deleteByPhoneNumber(number);
    }

    @Transactional
    public void truncateAll() {
        userRepository.deleteAllCascade();
    }

    public void save(User user) {
        userRepository.save(user);
    }
}