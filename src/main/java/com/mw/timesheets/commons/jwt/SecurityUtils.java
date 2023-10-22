package com.mw.timesheets.commons.jwt;

import com.mw.timesheets.domain.person.UserEntity;

public interface SecurityUtils {

    UserEntity getUserByEmail();
}
