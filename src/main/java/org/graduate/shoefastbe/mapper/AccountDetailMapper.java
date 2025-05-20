package org.graduate.shoefastbe.mapper;

import org.graduate.shoefastbe.dto.AccountCreateRequest;
import org.graduate.shoefastbe.dto.account.AccountUpdateRequest;
import org.graduate.shoefastbe.entity.AccountDetail;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface AccountDetailMapper {
    AccountDetail getEntityFromRequest(AccountCreateRequest account);
    void updateEntityByUpdateAccount(@MappingTarget AccountDetail accountDetail, AccountUpdateRequest updateAccountRequest);
}
