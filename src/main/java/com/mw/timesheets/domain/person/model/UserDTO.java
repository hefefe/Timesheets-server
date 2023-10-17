package com.mw.timesheets.domain.person.model;

import com.mw.timesheets.domain.person.type.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    private String email;

    private String tempPassword;

    private Roles roles;
}
