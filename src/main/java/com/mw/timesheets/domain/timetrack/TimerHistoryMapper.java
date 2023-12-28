package com.mw.timesheets.domain.timetrack;

import com.mw.timesheets.commons.mapper.CommonMapper;
import com.mw.timesheets.commons.mapper.MapperConfiguration;
import com.mw.timesheets.domain.project.ProjectEntity;
import com.mw.timesheets.domain.task.TaskEntity;
import com.mw.timesheets.domain.timetrack.model.TrackedDataDTO;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfiguration.class)
public interface TimerHistoryMapper extends CommonMapper<HistoryEntity, TrackedDataDTO> {

    @Override
    @Mapping(source = "started", target = "workFrom")
    @Mapping(source = "ended", target = "workTo")
    @Mapping(source = "task", target = "taskName", qualifiedByName = "getTaskName")
    @Mapping(source = "task.project", target = "projectKey", qualifiedByName = "getProjectKey")
    TrackedDataDTO toDto(HistoryEntity entity);

    @Named("getProjectKey")
    default String getProjectKey(ProjectEntity project){
        return project.getKey();
    }

    @Named("getTaskName")
    default String getTaskName(TaskEntity task){
        return task.getName();
    }
}
