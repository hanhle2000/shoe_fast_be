package org.graduate.shoefastbe.service.account;

import org.graduate.shoefastbe.base.error_success_handle.SuccessResponse;
import org.graduate.shoefastbe.dto.AccountCreateRequest;
import org.graduate.shoefastbe.dto.account.*;
import org.graduate.shoefastbe.entity.AccountDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.mail.MessagingException;
import java.util.List;

public interface AccountService {
    SuccessResponse singUp(AccountCreateRequest account);
    TokenAndRole login(LoginRequest loginRequest);
    AccountResponse findByUsername(String username);
    SuccessResponse forgotPassword(ForgotPassRequest forgotPassRequest) throws MessagingException;
    AccountResponse getDetailById(Long id);
    SuccessResponse changePassword(ChangePasswordRequest changePasswordRequest);
    AccountDetail updateProfile(AccountUpdateRequest accountUpdateRequest);
    AccountResponse createAccount( AccountCreateRequest accountCreateRequest );
    Long countAccount();
    Integer getTotalPage();
    List<AccountResponse> findUserByRole(String roleName, Pageable pageable);
    List<AccountResponse> findAllUser(Pageable pageable);
}
