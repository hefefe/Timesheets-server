package com.mw.timesheets.domain.security;

import com.mw.timesheets.domain.person.UserEntity;
import com.mw.timesheets.domain.security.model.AccessTokenDTO;
import com.mw.timesheets.domain.security.model.AuthenticationDTO;
import com.mw.timesheets.domain.security.model.CheckTokenDTO;
import com.mw.timesheets.domain.security.model.TokenDTO;

public interface JwtService {

    AuthenticationDTO refreshToken(TokenDTO tokenDTO);

    CheckTokenDTO checkToken(AccessTokenDTO accessTokenDTO);

    String getUsernameFromAccessToken(String token);

    AuthenticationDTO buildAuthenticationToken(UserEntity userEntity, boolean requiredToChangePassword);

    void blockTokens(TokenDTO tokenDTO);
}
