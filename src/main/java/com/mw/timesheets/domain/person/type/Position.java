package com.mw.timesheets.domain.person.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Position {

    BACKEND(Roles.ROLE_USER),
    FRONTEND(Roles.ROLE_USER),
    DEVOPS(Roles.ROLE_USER),
    QA(Roles.ROLE_USER),
    TEAM_LEADER(Roles.ROLE_MODERATOR),
    MANAGER(Roles.ROLE_ADMIN);

    private final Roles role;
}
