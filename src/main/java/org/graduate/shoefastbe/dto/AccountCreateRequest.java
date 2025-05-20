package org.graduate.shoefastbe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AccountCreateRequest {
    @NotNull(message = "Username không null")
    @NotEmpty(message = "Username không trống")
    private String username;
    @NotNull(message = "Password không null")
    @NotEmpty(message = "Password không trống")
    private String password;
    //account detail
    @NotNull(message = "FullName không được null")
    @NotEmpty(message = "FullName không được trống")
    private String fullName;
    @NotNull(message = "Gender không được null")
    @NotEmpty(message = "Gender không được trống")
    private String gender;
    @NotNull(message = "Phone không được null")
    @NotEmpty(message = "Phone không được trống")
    private String phone;
    @NotNull(message = "Email không được null")
    @NotEmpty(message = "Email không được trống")
    private String email;
    @NotNull(message = "Address không được null")
    @NotEmpty(message = "Address không được trống")
    private String address;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate birthdate;
    private Boolean isActive;
}
