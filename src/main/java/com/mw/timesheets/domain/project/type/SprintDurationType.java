package com.mw.timesheets.domain.project.type;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SprintDurationType {

    ONE_WEEK("one week", 1),
    TWO_WEEKS("two weeks", 2),
    THREE_WEEKS("three weeks", 3),
    FOUR_WEEKS("four weeks", 4);

    @JsonValue
    private final String name;
    private final Integer duration;

    public SprintDurationType getTypeFromValue(String value) {
        return Arrays.stream(SprintDurationType.values())
                .filter(sprintDurationType -> sprintDurationType.getName().equals(value))
                .findFirst()
                .orElseThrow(() -> new CustomErrorException("no such sprint duration", HttpStatus.BAD_REQUEST));
    }
}
