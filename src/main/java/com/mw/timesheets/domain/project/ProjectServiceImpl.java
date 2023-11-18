package com.mw.timesheets.domain.project;

import com.google.common.collect.Sets;
import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.commons.jwt.SecurityUtils;
import com.mw.timesheets.domain.project.model.ProjectDTO;
import com.mw.timesheets.domain.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {

    private final SecurityUtils securityUtils;
    private final TeamService teamService;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    //TODO: sprint number tak, aby brał pod uwage za lub przed LocalDateTime.now()
    @Override
    public ProjectDTO saveProject(ProjectDTO projectDTO) {
        var project = ProjectEntity.builder()
                .name(projectDTO.getName())
                .key(getKeyFromName(projectDTO.getName()))
                .photo(projectDTO.getPhoto())
                .workflow(projectDTO.getWorkflow().stream()
                        .map(workflow -> WorkflowEntity.builder().name(workflow).build())
                        .collect(Collectors.toList()))
                .lead(securityUtils.getPersonByEmail())
                .sprintDuration(projectDTO.getSprintDuration())
                .endOfSprint(projectDTO.getStartOfSprint().plusWeeks(projectDTO.getSprintDuration().getDuration()))
                .team(Sets.newHashSet(teamService.getTeamsByIds(projectDTO.getTeams())))
                .build();
        projectRepository.save(project);
        return null;
    }

    @Override
    public List<ProjectDTO> getProjects(String name) {
        List<ProjectEntity> projects = new ArrayList<>();
        var nameLike = name == null ? "%" : "%" + name + "%";
        switch (securityUtils.getRole()) {
            case ROLE_ADMIN -> projects = projectRepository.findAll();
            case ROLE_LEADER ->
                    projects = projectRepository.findByLeadAndNameLike(securityUtils.getPersonByEmail(), nameLike);
            case ROLE_USER ->
                    projects = projectRepository.findProjectByPersonIdAndName(securityUtils.getPersonByEmail().getId(), nameLike);
        }
        return projectMapper.toDtos(projects);
    }

    @Override
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    public ProjectDTO getProject(Long id) {
        return projectMapper.toDto(projectRepository.findById(id).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.BAD_REQUEST)));
    }

    @Override
    public List<ProjectDTO> getProjectsByName(String name) {
        return null;
    }

    private String getKeyFromName(String name) {
        String[] arr = name.split(" ");
        return Arrays.stream(arr)
                .map(word -> word.substring(0, 0).toUpperCase())
                .collect(Collectors.joining());
    }


}
