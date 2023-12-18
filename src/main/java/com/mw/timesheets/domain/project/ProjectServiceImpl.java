package com.mw.timesheets.domain.project;

import com.google.common.collect.Sets;
import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.commons.jwt.SecurityUtils;
import com.mw.timesheets.domain.person.PersonRepository;
import com.mw.timesheets.domain.person.model.PersonDTO;
import com.mw.timesheets.domain.project.model.ProjectDTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mw.timesheets.commons.util.DateUtils.getSystemTime;

@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {

    private final SecurityUtils securityUtils;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final WorkflowRepository workflowRepository;
    private final PersonRepository personRepository;

    @Override
    public ProjectDTO saveProject(ProjectDTO projectDTO) {

        var project = ProjectEntity.builder()
                .name(projectDTO.getName())
                .key(getKeyFromName(projectDTO.getName()))
                .photo(projectDTO.getPhoto())
                .person(securityUtils.getPersonByEmail())
                .sprintDuration(projectDTO.getSprintDuration())
                .endOfSprint(projectDTO.getEndOfSprint())
                .personsInProject(Sets.newHashSet(personRepository.findAllById(projectDTO.getPersons())))
                .sprintNumber(0)
                .taskNumber(0)
                .build();
        var savedProject = projectRepository.save(project);
        List<WorkflowEntity> workflowElements = projectDTO.getWorkflow().stream()
                .map(workflow -> WorkflowEntity.builder().name(workflow).project(savedProject).build())
                .collect(Collectors.toList());
        workflowRepository.saveAll(workflowElements);
        project.setWorkflow(workflowElements);
        var savedProjectAgain = projectRepository.save(savedProject);
        return projectMapper.toDto(projectRepository.findById(savedProjectAgain.getId()).orElse(null));
    }

    @Override
    public List<ProjectDTO> getProjects(String name) {
        List<ProjectEntity> projects = new ArrayList<>();
        var nameLike = name == null ? "%" : "%" + name + "%";
        switch (securityUtils.getRole()) {
            case ROLE_ADMIN -> projects = projectRepository.findAll();
            case ROLE_LEADER ->
                    projects = projectRepository.findByPersonAndNameLike(securityUtils.getPersonByEmail(), nameLike);
            case ROLE_USER ->
                    projects = projectRepository.findProjectByPersonIdAndName(securityUtils.getPersonByEmail().getId(), nameLike);
        }
        return projectMapper.toDtos(projects);
    }

    @Override
    public void deleteProject(Long id) {
        var project = projectRepository.findById(id).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.BAD_REQUEST));
        project.setDeleted(true);
        project.setDeletedTime(getSystemTime());
        projectRepository.save(project);
    }

    @Override
    public ProjectDTO getProject(Long id) {
        return projectMapper.toDto(projectRepository.findById(id).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.BAD_REQUEST)));
    }

    @Override
    public List<ProjectDTO> getProjectsByName(String name) {
        return null;
    }

    @Override
    @SneakyThrows
    public ProjectDTO savePersonPhoto(Long projectId, MultipartFile photo) {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.BAD_REQUEST));
        project.setPhoto(photo.getBytes());
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    private String getKeyFromName(String name) {
        String[] arr = name.split(" ");
        return Arrays.stream(arr)
                .map(word -> word.substring(0, 1).toUpperCase())
                .collect(Collectors.joining());
    }


}
