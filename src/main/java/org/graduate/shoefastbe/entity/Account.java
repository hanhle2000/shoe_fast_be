package org.graduate.shoefastbe.entity;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

import lombok.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String role;
    private Boolean isActive;
    private LocalDate createDate;
    private LocalDate modifyDate;
}
