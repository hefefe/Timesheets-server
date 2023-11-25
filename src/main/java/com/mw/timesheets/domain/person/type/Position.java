package com.mw.timesheets.domain.person.type;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Position {

    BACKEND("backend", Roles.ROLE_USER),
    FRONTEND("frontend", Roles.ROLE_USER),
    DEVOPS("devops", Roles.ROLE_USER),
    QA("quality assurance", Roles.ROLE_USER),
    TEAM_LEADER("team leader", Roles.ROLE_LEADER),
    MANAGER("manager", Roles.ROLE_ADMIN);

    @JsonValue
    private final String roleName;
    private final Roles role;
}
