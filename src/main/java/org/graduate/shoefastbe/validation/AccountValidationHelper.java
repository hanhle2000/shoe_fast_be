package org.graduate.shoefastbe.validation;

import lombok.AllArgsConstructor;
import org.graduate.shoefastbe.base.error_success_handle.CodeAndMessage;
import org.graduate.shoefastbe.dto.AccountCreateRequest;
import org.graduate.shoefastbe.repository.AccountDetailRepository;
import org.graduate.shoefastbe.repository.AccountRepository;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountValidationHelper {
    private final AccountRepository accountRepository;
    private final AccountDetailRepository accountDetailRepository;
    public void signUpValidate(AccountCreateRequest signUpRequest){
        if (accountRepository.existsByUsername(signUpRequest.getUsername())){
            throw new RuntimeException(CodeAndMessage.ME104);
        }
        if (accountDetailRepository.existsByEmail(signUpRequest.getEmail())){
            throw new RuntimeException(CodeAndMessage.ME105);
        }
    }
}
