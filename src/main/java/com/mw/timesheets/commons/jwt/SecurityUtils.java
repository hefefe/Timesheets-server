package com.mw.timesheets.commons.jwt;

import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.person.type.Position;

public interface SecurityUtils {

    PersonEntity getPersonByEmail();

    String getEmail();

    Position getPosition();

}

