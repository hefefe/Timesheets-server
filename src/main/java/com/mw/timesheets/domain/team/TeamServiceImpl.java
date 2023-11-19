package com.mw.timesheets.domain.team;

import com.google.common.collect.Sets;
import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.domain.person.PersonService;
import com.mw.timesheets.domain.team.model.TeamDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final PersonService personService;

    @Override
    public List<TeamDTO> getTeams() {
        return teamMapper.toDtos(teamRepository.findByDeletedFalse());
    }

    @Override
    public List<TeamDTO> getTeamsLike(String name) {
        return teamMapper.toDtos(teamRepository.findByNameContainingAndDeletedFalse(name));
    }

    @Override
    public void deleteTeam(Long id) {
        var team = teamRepository.findById(id).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.BAD_REQUEST));
        team.setDeleted(true);
        team.setDeletedTime(LocalDateTime.now());
        teamRepository.save(team);
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
    public Set<TeamEntity> getTeamsByIds(List<Long> ids) {
        return teamRepository.findAllById(ids).stream().collect(Collectors.toSet());
    }
}
