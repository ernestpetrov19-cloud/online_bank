package com.example.online_bank.controller;

import com.example.online_bank.domain.dto.UserContainer;
import com.example.online_bank.domain.entity.Role;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.service.TokenService;
import com.example.online_bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token/")
@RequiredArgsConstructor
public class JwtTokenController {
    private final TokenService tokenService;
    private final UserService userService;

    @PostMapping("/get-access-token")
    public ResponseEntity<String> regenerateToken(@RequestParam String email) {
        User user = userService.findByEmail(email);
        UserContainer userContainer = new UserContainer(user.getUuid().toString(), user.getName(), user.getRoles().stream().map(Role::getName).toList());
        return ResponseEntity.status(200).body(tokenService.getAccessToken(userContainer));
    }
}
