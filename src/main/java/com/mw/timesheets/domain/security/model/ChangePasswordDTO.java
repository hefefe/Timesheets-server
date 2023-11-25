package com.mw.timesheets.domain.security.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordDTO{

    @NotNull
    private String password;

    @NotNull
    private String confirmPassword;
}
