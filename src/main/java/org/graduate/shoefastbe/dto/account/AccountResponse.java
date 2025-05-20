package org.graduate.shoefastbe.dto.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AccountResponse {
    private Long id;
    private String username;
    private LocalDate createDate;
    private LocalDate modifyDate;
    private Boolean isActive;
    private String roleName;
    private String fullName;
    private String gender;
    private String phone;
    private String email;
    private String address;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate birthDate;
}
