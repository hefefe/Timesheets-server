package com.mw.timesheets.domain.project;

import com.google.common.collect.Sets;
import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.commons.jwt.SecurityUtils;
import com.mw.timesheets.domain.person.PersonMapper;
import com.mw.timesheets.domain.person.PersonRepository;
import com.mw.timesheets.domain.project.model.ProjectDTO;
import com.mw.timesheets.domain.task.TaskRepository;
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
    private final PersonMapper personMapper;
    private final TaskRepository taskRepository;

    @Override
    public ProjectDTO saveProject(ProjectDTO projectDTO) {
        if(projectDTO.getId() != null) {
            return editProject(projectDTO);
        }
        var project = projectMapper.toEntity(projectDTO);
        project.setSprintNumber(0);
        project.setTaskNumber(0);
        project.setEndOfSprint(project.getEndOfSprint().plusHours(2));
        project.setPersonsInProject(Sets.newHashSet(personRepository.findAllById(projectDTO.getPersons())));
        var savedProject = projectRepository.save(project);
        project.getWorkflow().forEach(workflow -> workflow.setProject(savedProject));
        workflowRepository.saveAll(savedProject.getWorkflow());
        savedProject.setKey(getKeyFromName(projectDTO.getName(), savedProject.getId()));
        return projectMapper.toDto(savedProject);
    }

    private ProjectDTO editProject(ProjectDTO projectDTO){
        var project = projectRepository.findById(projectDTO.getId()).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.BAD_REQUEST));
        var oldWorkflow = project.getWorkflow();
        var oldDuration = project.getSprintDuration();

        projectMapper.updateEntity(projectDTO, project);
        project.setPerson(personMapper.toEntity(projectDTO.getPerson()));
        project.setPersonsInProject(Sets.newHashSet(personRepository.findAllById(projectDTO.getPersons())));
        project.setKey(getKeyFromName(projectDTO.getName(), project.getId()));
        project.setEndOfSprint(project.getEndOfSprint().minusWeeks(oldDuration.getDuration()).plusWeeks(projectDTO.getSprintDuration().getDuration()));

        var newWorkflow = projectMapper.workflowStringToEntity(projectDTO.getWorkflow());
        if(!oldWorkflow.equals(newWorkflow)) {
            var savedWorkflow = workflowRepository.saveAll(newWorkflow);

            if (oldWorkflow.get(oldWorkflow.size() - 1).getTasks() != null) {
                oldWorkflow.get(oldWorkflow.size() - 1).getTasks().forEach(task -> task.setWorkflow(savedWorkflow.get(savedWorkflow.size() - 1)));
                savedWorkflow.get(savedWorkflow.size() - 1).setTasks(oldWorkflow.get(oldWorkflow.size() - 1).getTasks());
                oldWorkflow.remove(oldWorkflow.size() - 1);
                taskRepository.saveAll(savedWorkflow.get(savedWorkflow.size() - 1).getTasks());
            }
            var size = oldWorkflow.size();
            if (size != 0) {
                for (int i = 0; i < size; i++) {
                    if (oldWorkflow.get(0).getTasks().isEmpty()) continue;
                    var index = Math.min(i, newWorkflow.size() - 2);
                    oldWorkflow.get(0).getTasks().forEach(task -> task.setWorkflow(savedWorkflow.get(index)));
                    savedWorkflow.get(index).setTasks(oldWorkflow.get(0).getTasks());
                    oldWorkflow.remove(0);
                    taskRepository.saveAll(savedWorkflow.get(index).getTasks());
                }
            }
            project.getWorkflow().clear();
            project.getWorkflow().addAll(newWorkflow);
        }
        newWorkflow.forEach(workflowEntity -> workflowEntity.setProject(project));
        var savedProject = projectRepository.save(project);

        return projectMapper.toDto(savedProject);
    }

    @Override
    public List<ProjectDTO> getProjects(String name) {
        List<ProjectEntity> projects = new ArrayList<>();
        var nameLike = name == null ? "%" : "%" + name + "%";
        switch (securityUtils.getRole()) {
            case ROLE_ADMIN -> projects = projectRepository.findByNameLikeAndDeletedFalse(nameLike);
            case ROLE_LEADER ->
                    projects = projectRepository.findByPersonAndNameLikeAndDeletedFalse(securityUtils.getPersonByEmail(), nameLike);
            case ROLE_USER ->
                    projects = projectRepository.findProjectByPersonIdAndNameAndDeletedFalse(securityUtils.getPersonByEmail().getId(), nameLike);
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
    @SneakyThrows
    public ProjectDTO savePersonPhoto(Long projectId, MultipartFile photo) {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.BAD_REQUEST));
        project.setPhoto(photo.getBytes());
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    private String getKeyFromName(String name, Long id) {
        String[] arr = name.split(" ");
        return Arrays.stream(arr)
                .map(word -> word.substring(0, 1).toUpperCase())
                .collect(Collectors.joining()) + id;
    }


}
