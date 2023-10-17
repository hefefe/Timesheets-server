package com.mw.timesheets.domain.security;

import com.mw.timesheets.domain.security.model.AuthenticationDTO;
import com.mw.timesheets.domain.security.model.ChangePasswordDTO;
import com.mw.timesheets.domain.security.model.LoginDTO;
import com.mw.timesheets.domain.security.model.TokenDTO;

public interface AuthenticationService {

    AuthenticationDTO auth(LoginDTO loginDTO);

    void logout(TokenDTO tokenDTO);

    void resetUserPassword(Long id);

    AuthenticationDTO setUserPassword(ChangePasswordDTO changePasswordDTO);
}
