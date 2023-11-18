package com.mw.timesheets.domain.security.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AuthenticationDTO extends TokenDTO {

    private Long accessTokenValidityTime;
    private Long refreshTokenValidityTime;
    private boolean requiredToChangePassword;
}
