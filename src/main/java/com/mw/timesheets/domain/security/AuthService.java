package com.mw.timesheets.domain.security;

import com.mw.timesheets.domain.security.model.ChangePasswordDTO;

public interface AuthService {

    void resetUserPassword(Long id);

    void setUserPassword(ChangePasswordDTO changePassword);
}
