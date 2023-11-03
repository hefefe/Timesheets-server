package com.mw.timesheets.domain.project;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MapWorkflowElement {

    @Named("workflowToString")
    public Set<String> workflowToString(Set<WorkflowEntity> workflowElements){
        return workflowElements.stream()
                .map(WorkflowEntity::getName)
                .collect(Collectors.toSet());
    }
}
