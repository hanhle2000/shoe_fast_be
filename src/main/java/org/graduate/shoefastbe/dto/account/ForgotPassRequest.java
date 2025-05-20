package org.graduate.shoefastbe.dto.account;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ForgotPassRequest {
    private String email;
}
