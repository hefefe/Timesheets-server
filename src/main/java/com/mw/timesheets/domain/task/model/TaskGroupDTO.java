package com.mw.timesheets.domain.task.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class TaskGroupDTO {

    private WorkflowDTO workflowDTO;

    private List<TaskDTO> tasks;
}
