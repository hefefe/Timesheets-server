package com.mw.timesheets.domain.task.model;

import com.mw.timesheets.domain.person.model.BasicPersonDataDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class TaskDTO {

    private Long id;

    private String name;

    private String description;

    private String key;

    private Integer storyPoints;

    private BasicPersonDataDTO person;

    private TaskTypeDTO taskType;

    private WorkflowDTO workflow;
}
