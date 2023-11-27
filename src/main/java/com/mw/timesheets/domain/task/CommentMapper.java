package com.mw.timesheets.domain.task;

import com.mw.timesheets.commons.mapper.CommonMapper;
import com.mw.timesheets.commons.mapper.MapperConfiguration;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.person.model.BasicPersonDataDTO;
import com.mw.timesheets.domain.task.model.CommentDTO;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class, uses = {CommentResourceMapper.class})
public interface CommentMapper extends CommonMapper<CommentEntity, CommentDTO> {

    BasicPersonDataDTO toBasicData(PersonEntity person);
}
