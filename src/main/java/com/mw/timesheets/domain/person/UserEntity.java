package com.mw.timesheets.domain.person;

import com.mw.timesheets.commons.CommonEntity;
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
public class UserEntity extends CommonEntity {

    private String email;

    private String password;

    private String tempPassword;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private RoleEntity role;


    @OneToOne(mappedBy = "user")
    private PersonEntity person;
}
