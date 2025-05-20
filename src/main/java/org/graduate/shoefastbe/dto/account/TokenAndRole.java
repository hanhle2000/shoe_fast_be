package org.graduate.shoefastbe.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenAndRole {
    private String accessToken;
    private String role;
    private String fullName;
}