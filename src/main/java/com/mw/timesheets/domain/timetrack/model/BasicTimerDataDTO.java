package com.mw.timesheets.domain.timetrack.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BasicTimerDataDTO {

    private String description;

    private String projectKey;

    private String taskName;

    private TagDTO tag;
}
