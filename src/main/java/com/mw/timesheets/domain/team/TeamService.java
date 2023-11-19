package com.mw.timesheets.domain.team;

import com.mw.timesheets.domain.team.model.TeamDTO;

import java.util.List;
import java.util.Set;

public interface TeamService {

    List<TeamDTO> getTeams();

    List<TeamDTO> getTeamsLike(String name);

    void deleteTeam(Long id);

    List<TeamDTO> saveTeam(List<TeamDTO> teamDTO);

    Set<TeamEntity> getTeamsByIds(List<Long> ids);

}
