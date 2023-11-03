package com.mw.timesheets.domain.project;

import com.google.common.collect.Sets;
import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.person.PersonMapper;
import com.mw.timesheets.domain.person.PersonService;
import com.mw.timesheets.domain.project.model.TeamDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService{

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final PersonService personService;
    @Override
    public List<TeamDTO> getTeams() {
        return teamMapper.toDtos(teamRepository.findAll());
    }

    @Override
    public List<TeamDTO> getTeamsLike(String name) {
        return teamMapper.toDtos(teamRepository.findByNameContaining(name));
    }

    @Override
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    @Override
    public List<TeamDTO> saveTeam(List<TeamDTO> teamDTO) {
        var entities = teamMapper.toEntities(teamDTO);
        var modifiedEntities = entities.stream()
                .peek(teamEntity -> teamEntity.setPersons(Sets.newHashSet(personService.getUsersByIds(teamEntity.getPersons().stream().map(CommonEntity::getId).collect(Collectors.toList())))))
                .collect(Collectors.toList());
        var saved = teamRepository.saveAll(modifiedEntities);
        return teamMapper.toDtos(saved);
    }

    @Override
    public List<TeamEntity> getTeamsByIds(List<Long> ids) {
        return teamRepository.findAllById(ids);
    }
}
