package org.graduate.shoefastbe.mapper;

import org.graduate.shoefastbe.dto.AccountCreateRequest;
import org.graduate.shoefastbe.entity.Account;
import org.mapstruct.Mapper;

@Mapper
public interface AccountMapper {
    Account getEntityFromRequest(AccountCreateRequest accountCreateRequest);
}
