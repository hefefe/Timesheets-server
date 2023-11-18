package com.mw.timesheets.domain.team;

import com.mw.timesheets.domain.team.model.TeamDTO;

import java.util.List;

public interface TeamService {

    List<TeamDTO> getTeams();

    List<TeamDTO> getTeamsLike(String name);

    void deleteTeam(Long id);

    List<TeamDTO> saveTeam(List<TeamDTO> teamDTO);

    List<TeamEntity> getTeamsByIds(List<Long> ids);

}
