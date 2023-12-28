package com.mw.timesheets.domain.timetrack.model;

import com.mw.timesheets.domain.project.model.ProjectDTO;
import com.mw.timesheets.domain.task.model.TaskDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class IsStartedDTO {

    private LocalTime startedTime;

    private TaskDTO task;

    private ProjectDTO project;

    private String description;
}
