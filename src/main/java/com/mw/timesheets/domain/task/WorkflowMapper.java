package com.mw.timesheets.domain.task;

import com.mw.timesheets.commons.mapper.CommonMapper;
import com.mw.timesheets.commons.mapper.MapperConfiguration;
import com.mw.timesheets.domain.project.WorkflowEntity;
import com.mw.timesheets.domain.task.model.WorkflowDTO;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class)
public interface WorkflowMapper extends CommonMapper<WorkflowEntity, WorkflowDTO> {
}
