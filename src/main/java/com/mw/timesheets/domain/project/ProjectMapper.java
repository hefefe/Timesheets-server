package com.mw.timesheets.domain.project;

import com.mw.timesheets.commons.mapper.CommonMapper;
import com.mw.timesheets.commons.mapper.MapperConfiguration;
import com.mw.timesheets.domain.person.PersonMapper;
import com.mw.timesheets.domain.project.model.ProjectDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(config = MapperConfiguration.class, uses = {PersonMapper.class, MapWorkflowElement.class})
public interface ProjectMapper extends CommonMapper<ProjectEntity, ProjectDTO> {


    @Override
    @Mapping(target = "lead", ignore = true)
    @Mapping(target = "workflow", source = "workflow", qualifiedByName = "workflowStringToWorkflow")
    ProjectEntity toEntity(ProjectDTO dto);

    @Override
    @Mapping(target = "workflow", source = "workflow", qualifiedByName = "workflowToString")
    ProjectDTO toDto(ProjectEntity entity);

    @Named("workflowStringToWorkflow")
    default List<WorkflowEntity> workflowToString(List<String> workflowElements) {
        return workflowElements.stream()
                .map(workflow -> WorkflowEntity.builder()
                        .name(workflow)
                        .build())
                .collect(Collectors.toList());
    }
}
