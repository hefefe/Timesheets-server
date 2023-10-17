package com.mw.timesheets.domain.statistcs.model.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasksDoneDTO {

    private String type;

    private Integer numberOfTasks;
}
