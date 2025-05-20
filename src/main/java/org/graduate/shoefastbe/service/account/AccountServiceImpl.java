package org.graduate.shoefastbe.service.account;

import lombok.AllArgsConstructor;
import org.graduate.shoefastbe.base.authen.TokenHelper;
import org.graduate.shoefastbe.base.error_success_handle.CodeAndMessage;
import org.graduate.shoefastbe.base.error_success_handle.SuccessHandle;
import org.graduate.shoefastbe.base.error_success_handle.SuccessResponse;
import org.graduate.shoefastbe.common.enums.RoleEnums;
import org.graduate.shoefastbe.dto.AccountCreateRequest;
import org.graduate.shoefastbe.dto.account.*;
import org.graduate.shoefastbe.entity.AccountDetail;
import org.graduate.shoefastbe.entity.Account;
import org.graduate.shoefastbe.mapper.AccountDetailMapper;
import org.graduate.shoefastbe.mapper.AccountMapper;
import org.graduate.shoefastbe.repository.AccountDetailRepository;
import org.graduate.shoefastbe.repository.AccountRepository;
import org.graduate.shoefastbe.util.MailUtil;
import org.graduate.shoefastbe.validation.AccountValidationHelper;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountValidationHelper accountValidationHelper;
    private final AccountDetailRepository accountDetailRepository;
    private final AccountDetailMapper accountDetailMapper;
    private final AccountMapper accountMapper;
    @Override
    @Transactional
    public SuccessResponse singUp(AccountCreateRequest account) {
        accountValidationHelper.signUpValidate(account);
        validatePassword(account.getPassword());
        if(accountRepository.existsByUsername(account.getUsername())){
            throw new RuntimeException("User name is exist!");
        }
        if(accountDetailRepository.existsByEmail(account.getEmail())){
            throw new RuntimeException("Email is exist!!");
        }
        Account accountEntity = accountMapper.getEntityFromRequest(account);
        accountEntity.setPassword(BCrypt.hashpw(account.getPassword(), BCrypt.gensalt()));
        accountEntity.setIsActive(Boolean.TRUE);
        accountEntity.setRole(RoleEnums.CUSTOMER.name());
        accountEntity.setCreateDate(LocalDate.now());
        accountEntity.setModifyDate(LocalDate.now());
        accountRepository.saveAndFlush(accountEntity);
        AccountDetail accountDetail = accountDetailMapper.getEntityFromRequest(account);
        accountDetail.setAccountId(accountEntity.getId());
        accountDetailRepository.save(accountDetail);
        return SuccessHandle.success(CodeAndMessage.ME106);
    }

    public void validatePassword(String password) {
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Mật khẩu có ít nhất 8 ký tự bao gồm chữ hoa, chữ thường và ký tự đặc biệt!");
        }
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }
        }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }
    @Override
    public TokenAndRole login(LoginRequest loginRequest) {
        Account userEntity = accountRepository.findByUsernameAndIsActive(loginRequest.getUsername(), Boolean.TRUE);
        if (Objects.isNull(userEntity) || Boolean.FALSE.equals(userEntity.getIsActive())){
            throw new RuntimeException(CodeAndMessage.ERR4);
        }
        String currentHashedPassword = userEntity.getPassword();
        AccountDetail accountDetail = accountDetailRepository.findByAccountId(userEntity.getId());
        if (BCrypt.checkpw(loginRequest.getPassword(), currentHashedPassword)){
            String accessToken = TokenHelper.generateToken(userEntity);
            return new TokenAndRole(accessToken, userEntity.getRole(), accountDetail.getFullName());
        }
        throw new RuntimeException(CodeAndMessage.ERR1);
    }

    @Override
    public AccountResponse findByUsername(String accessToken) {
        String username = TokenHelper.getUsernameFromToken(accessToken);
        Account account = accountRepository.findByUsernameAndIsActive(username, Boolean.TRUE);
       return getAccountDetail(account, account.getId());
    }

    @Override
    @Transactional
    public SuccessResponse forgotPassword(ForgotPassRequest forgotPassRequest) throws MessagingException {
        AccountDetail accountDetail = accountDetailRepository.findByEmail(forgotPassRequest.getEmail());
        Account account = accountRepository.findByIdAndIsActive(accountDetail.getAccountId(), Boolean.TRUE);
        if(Objects.isNull(account)) throw new RuntimeException(CodeAndMessage.ERR2);
        String newPassword = String.valueOf(UUID.randomUUID());
        account.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        accountRepository.saveAndFlush(account);
        //gửi mail
        try {
            MailUtil.sendmailForgotPassword(accountDetail.getEmail(), newPassword);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return SuccessHandle.success(CodeAndMessage.ME107);
    }

    @Override
    public AccountResponse getDetailById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR2)
        );
        return getAccountDetail(account, id);
    }

    @Override
    @Transactional
    public SuccessResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        validatePassword(changePasswordRequest.getNewPassword());
        Account userEntity = accountRepository.findByUsernameAndIsActive(changePasswordRequest.getUsername(), Boolean.TRUE);
        if (Objects.isNull(userEntity) || Boolean.FALSE.equals(userEntity.getIsActive())){
            throw new RuntimeException(CodeAndMessage.ERR4);
        }
        String currentHashedPassword = userEntity.getPassword();
        if (BCrypt.checkpw(changePasswordRequest.getPassword(), currentHashedPassword)){
            userEntity.setPassword(BCrypt.hashpw(changePasswordRequest.getNewPassword(), BCrypt.gensalt()));
            accountRepository.save(userEntity);
            return SuccessHandle.success(CodeAndMessage.ME108);
        }else{
            throw new RuntimeException(CodeAndMessage.ERR1);
        }
    }

    @Override
    @Transactional
    public AccountDetail updateProfile(AccountUpdateRequest accountUpdateRequest) {
        AccountDetail accountDetail = accountDetailRepository.findByAccountId(accountUpdateRequest.getId());
        Account account = accountRepository.findById(accountUpdateRequest.getId()).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        if(RoleEnums.ADMIN.name().equals(account.getRole()) && accountUpdateRequest.getIsActive().equals(Boolean.FALSE)){
            throw new RuntimeException(CodeAndMessage.ERR12);
        }
        if(accountDetailRepository.existsByEmail(accountUpdateRequest.getEmail()) && !accountDetail.getEmail().equals(accountUpdateRequest.getEmail())){
            throw new RuntimeException("ERR111-Email đã tồn tại");
        }
        account.setIsActive(accountUpdateRequest.getIsActive());
        accountRepository.save(account);
        accountUpdateRequest.setId(accountDetail.getId());
        accountDetailMapper.updateEntityByUpdateAccount(accountDetail, accountUpdateRequest);
        accountDetailRepository.save(accountDetail);
        return accountDetail;
    }

    @Override
    @Transactional
    public AccountResponse createAccount(AccountCreateRequest accountCreateRequest) {
        if (Boolean.TRUE.equals(accountRepository.existsByUsername(accountCreateRequest.getUsername()))) {
            throw new RuntimeException("Username đã tồn tại");
        }
        if (Boolean.TRUE.equals(accountRepository.existsByUsername(accountCreateRequest.getEmail()))){
            throw new RuntimeException("Email đã tồn tại");
        }
        Account account = accountMapper.getEntityFromRequest(accountCreateRequest);
        account.setRole(RoleEnums.CUSTOMER.name());
        account.setIsActive(Boolean.TRUE);
        account.setCreateDate(LocalDate.now());
        account.setModifyDate(LocalDate.now());
        accountRepository.save(account);
        AccountDetail accountDetail = accountDetailMapper.getEntityFromRequest(accountCreateRequest);
        accountDetail.setAccountId(account.getId());
        accountDetailRepository.save(accountDetail);
        return getAccountDetail(account,account.getId());
    }

    @Override
    public Long countAccount() {
        return accountRepository.findAll().stream()
                .filter(account -> Boolean.TRUE.equals(account.getIsActive()))
                .count() -1L;
    }

    @Override
    public Integer getTotalPage() {
        return accountRepository.findAll(PageRequest.of(0,9)).getTotalPages();
    }

    @Override
    public List<AccountResponse> findUserByRole(String roleName, Pageable pageable) {
        return accountRepository.findAccountByRoleName(roleName,pageable);
    }

    @Override
    public List<AccountResponse> findAllUser(Pageable pageable) {
        return accountRepository.findAllAccount(pageable);
    }

    private AccountResponse getAccountDetail(Account account, Long id){
        AccountDetail accountDetail = accountDetailRepository.findByAccountId(id);
        return AccountResponse.builder()
                .id(account.getId())
                .address(accountDetail.getAddress())
                .email(accountDetail.getEmail())
                .phone(accountDetail.getPhone())
                .gender(accountDetail.getGender())
                .username(account.getUsername())
                .birthDate(accountDetail.getBirthdate())
                .fullName(accountDetail.getFullName())
                .roleName(account.getRole())
                .isActive(account.getIsActive())
                .createDate(account.getCreateDate())
                .modifyDate(account.getModifyDate())
                .build();
    }
}
