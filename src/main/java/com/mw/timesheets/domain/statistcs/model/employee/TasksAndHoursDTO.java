package com.mw.timesheets.domain.statistcs.model.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasksAndHoursDTO {

    private String taskName;

    private Long timeOfCompletion;
}
