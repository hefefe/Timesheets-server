package com.mw.timesheets.domain.person.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Position {

    BACKEND,
    FRONTEND,
    DEVOPS,
    QA,
    TEAM_LEADER,
    MANAGER
}
