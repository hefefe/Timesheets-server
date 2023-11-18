package com.mw.timesheets.commons.jwt;

import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.person.type.Roles;

public interface SecurityUtils {

    PersonEntity getPersonByEmail();

    String getEmail();

    Roles getRole();

}

