package com.mw.timesheets.domain.project;

import com.mw.timesheets.commons.mapper.CommonMapper;
import com.mw.timesheets.commons.mapper.MapperConfiguration;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.person.PersonMapper;
import com.mw.timesheets.domain.person.model.PersonDTO;
import com.mw.timesheets.domain.project.model.ProjectDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(config = MapperConfiguration.class, uses = {PersonMapper.class})
public interface ProjectMapper extends CommonMapper<ProjectEntity, ProjectDTO> {


    @Override
    @Mapping(target = "workflow", source = "workflow", qualifiedByName = "workflowStringToWorkflow")
    @Mapping(target = "photo", ignore = true)
    ProjectEntity toEntity(ProjectDTO dto);

    @Override
    @Mapping(target = "workflow", source = "workflow", qualifiedByName = "workflowToString")
    ProjectDTO toDto(ProjectEntity entity);

    @Mapping(target = "key", ignore = true)
    @Mapping(target = "taskNumber", ignore = true)
    @Mapping(target = "workflow", ignore = true)
    @Mapping(target = "endOfSprint", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "photo", ignore = true)
    ProjectEntity updateEntity(ProjectDTO dto, @MappingTarget ProjectEntity project);

    @Named("workflowStringToWorkflow")
    default List<WorkflowEntity> workflowStringToEntity(List<String> workflowElements) {
        return workflowElements.stream()
                .map(workflow -> WorkflowEntity.builder()
                        .name(workflow)
                        .build())
                .collect(Collectors.toList());
    }

    @Named("workflowToString")
    default List<String> workflowToString(List<WorkflowEntity> workflowElements) {
        return workflowElements.stream()
                .map(WorkflowEntity::getName)
                .collect(Collectors.toList());
    }
}
