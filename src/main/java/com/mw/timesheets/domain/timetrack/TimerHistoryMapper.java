package com.mw.timesheets.domain.timetrack;

import com.mw.timesheets.commons.mapper.CommonMapper;
import com.mw.timesheets.commons.mapper.MapperConfiguration;
import com.mw.timesheets.domain.timetrack.model.TrackedDataDTO;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class)
public interface TimerHistoryMapper extends CommonMapper<HistoryEntity, TrackedDataDTO> {

}
