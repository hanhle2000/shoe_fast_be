package org.graduate.shoefastbe.dto.account;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AccountUpdateRequest {
    private Long id;
    private String fullName;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private Boolean isActive;
}
