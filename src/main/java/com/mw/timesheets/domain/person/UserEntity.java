package com.mw.timesheets.domain.person;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.person.type.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "USER")
public class UserEntity extends CommonEntity {

    private String email;

    private String password;

    private String tempPassword;

    @Enumerated(EnumType.STRING)
    private Roles role;

    @OneToOne(mappedBy = "user")
    private PersonEntity person;
}
