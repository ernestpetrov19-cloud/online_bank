package com.example.online_bank.mapper;

import com.example.online_bank.domain.dto.OperationInfoDto;
import com.example.online_bank.domain.entity.Operation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OperationMapper {

    /**
     * OPERATION INFO DTO
     */
    @Mapping(target = "accountNumber", source = "operation.account.accountNumber")
    OperationInfoDto toOperationInfoDto(Operation operation);
}
