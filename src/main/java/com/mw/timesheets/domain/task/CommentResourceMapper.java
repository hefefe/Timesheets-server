package com.mw.timesheets.domain.task;

import com.mw.timesheets.commons.mapper.CommonMapper;
import com.mw.timesheets.commons.mapper.MapperConfiguration;
import com.mw.timesheets.domain.person.PersonMapper;
import com.mw.timesheets.domain.project.MapWorkflowElement;
import com.mw.timesheets.domain.task.model.CommentResourcesDTO;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class)
public interface CommentResourceMapper extends CommonMapper<CommentResourceEntity, CommentResourcesDTO> {
}
