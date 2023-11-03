package com.mw.timesheets.domain.project;

import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

public class MapWorkflowElement {

    @Named("workflowToString")
    public Set<String> workflowToString(Set<WorkflowEntity> workflowElements){
        return workflowElements.stream()
                .map(WorkflowEntity::getName)
                .collect(Collectors.toSet());
    }
}
