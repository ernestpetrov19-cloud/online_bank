package com.example.online_bank.mapper;

import com.example.online_bank.domain.dto.RegistrationDtoRequest;
import com.example.online_bank.domain.event.SendOtpEvent;
import com.example.online_bank.domain.dto.UserContainer;
import com.example.online_bank.domain.entity.Role;
import com.example.online_bank.domain.entity.User;
import com.example.online_bank.service.RoleService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "email", source = "dto.email")
    @Mapping(target = "code", source = "code")
    SendOtpEvent toSendOtpEvent(RegistrationDtoRequest dto, String code);

    //NoNeed - id, failedAttempts, isBlocked, blockedExpiredAt, accounts, verifiedCode
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "uuid", source = "user.uuid", qualifiedByName = "uuidToString")
    @Mapping(target = "roles", source = "user.roles", qualifiedByName = "rolesToString")
    UserContainer toUserContainer(User user);

    @Mapping(target = "phoneNumber", source = "phone")
    @Mapping(target = "passwordHash", source = "password", qualifiedByName = "encodePassword")
    @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "failedAttempts", constant = "0")
    @Mapping(target = "isBlocked", constant = "false")
    @Mapping(target = "isVerified", constant = "false")
    @Mapping(target = "roles", expression = """
            java(List.of(roleService.findRoleByName(com.example.online_bank.enums.Roles.ROLE_USER.getValue())))
            """)
    User toUser(RegistrationDtoRequest dto, @Context RoleService roleService, @Context BCryptPasswordEncoder passwordEncoder);

    @Mapping(target = "phoneNumber", source = "phone")
    @Mapping(target = "passwordHash", source = "password", qualifiedByName = "encodePassword")
    @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "failedAttempts", constant = "0")
    @Mapping(target = "isBlocked", constant = "false")
    @Mapping(target = "isVerified", constant = "false")
    @Mapping(target = "roles", expression = """
            java(List.of(roleService.findRoleByName(com.example.online_bank.enums.Roles.ROLE_ADMIN.getValue())))
            """)
    User toAdmin(RegistrationDtoRequest dto, @Context RoleService roleService, @Context BCryptPasswordEncoder passwordEncoder);

    //для UserContainer
    @Named(value = "rolesToString")
    default List<String> rolesToString(List<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .toList();
    }

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid.toString();
    }

    @Named("encodePassword")
    default String encodePassword(@Context BCryptPasswordEncoder encoder, String password) {
        return encoder.encode(password);
    }
}
