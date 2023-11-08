package com.mw.timesheets.domain.project;

import com.google.common.collect.Sets;
import com.mw.timesheets.commons.CustomErrorException;
import com.mw.timesheets.commons.jwt.SecurityUtils;
import com.mw.timesheets.domain.person.type.Roles;
import com.mw.timesheets.domain.project.model.ProjectDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService{

    private final SecurityUtils securityUtils;
    private final TeamService teamService;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

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
                .taskNumber(0)
                .build();
        projectRepository.save(project);
        return null;
    }

    @Override
    public List<ProjectDTO> getProjects() {
        List<ProjectEntity> projects = new ArrayList<>();
        switch(securityUtils.getRole()){
            case ROLE_ADMIN -> projects = projectRepository.findAll();
            case ROLE_LEADER -> projects = projectRepository.findByLead(securityUtils.getPersonByEmail());
            case ROLE_USER -> projects = projectRepository.findProjectByPersonId(securityUtils.getPersonByEmail().getId());
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

    private String getKeyFromName(String name){
        String[] arr = name.split(" ");
        return Arrays.stream(arr)
                .map(word -> word.substring(0,0).toUpperCase())
                .collect(Collectors.joining());
    }


}
