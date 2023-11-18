package com.mw.timesheets.domain.team;

import com.mw.timesheets.commons.mapper.CommonMapper;
import com.mw.timesheets.commons.mapper.MapperConfiguration;
import com.mw.timesheets.domain.person.PersonMapper;
import com.mw.timesheets.domain.team.model.TeamDTO;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class, uses = {PersonMapper.class})
public interface TeamMapper extends CommonMapper<TeamEntity, TeamDTO> {

    @Override
    TeamEntity toEntity(TeamDTO dto);

    @Override
    TeamDTO toDto(TeamEntity entity);
}
