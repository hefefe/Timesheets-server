package com.mw.timesheets.domain.project;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MapWorkflowElement {

    @Named("workflowToString")
    public List<String> workflowToString(List<WorkflowEntity> workflowElements){
        return workflowElements.stream()
                .map(WorkflowEntity::getName)
                .collect(Collectors.toList());
    }
}
