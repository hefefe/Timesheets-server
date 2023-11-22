package com.mw.timesheets.domain.security.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ChangePasswordDTO extends LoginDTO {

    @NotNull
    private String confirmPassword;

    @NotNull
    private String tempPassword;
}
