package com.mw.timesheets.domain.task;

import com.mw.timesheets.commons.mapper.CommonMapper;
import com.mw.timesheets.commons.mapper.MapperConfiguration;
import com.mw.timesheets.domain.task.model.TaskDTO;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class, uses = {TaskTypeMapper.class, WorkflowMapper.class})
public interface TaskMapper extends CommonMapper<TaskEntity, TaskDTO> {

}
