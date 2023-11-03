package com.mw.timesheets.domain.task;

import com.google.common.collect.Lists;
import com.mw.timesheets.commons.CustomErrorException;
import com.mw.timesheets.domain.person.PersonMapper;
import com.mw.timesheets.domain.person.model.BasicPersonDataDTO;
import com.mw.timesheets.domain.project.ProjectEntity;
import com.mw.timesheets.domain.project.ProjectRepository;
import com.mw.timesheets.domain.project.TeamEntity;
import com.mw.timesheets.domain.task.model.TaskDTO;
import com.mw.timesheets.domain.task.model.TaskTypeDTO;
import com.mw.timesheets.domain.task.model.WorkflowDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService{

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskTypeRepository taskTypeRepository;
    private final TaskMapper taskMapper;
    private final WorkflowMapper workflowMapper;
    private final PersonMapper personMapper;
    private final TaskTypeMapper taskTypeMapper;
    @Override
    public TaskDTO saveTask(TaskDTO taskDTO, Long projectId) {
        var task = taskMapper.toEntity(taskDTO);
        var project = projectRepository.findById(projectId).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.BAD_REQUEST));
//        task.toBuilder()
//                .key(generateKeyForTask(project, task.getKey()))
//                .taskType(taskTypeRepository.findById(task.getTaskType().getId()).orElseThrow())
        task.setKey(generateKeyForTask(project, task.getKey()));
        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Override
    public List<TaskDTO> getTasksFroProject(Long projectId) {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.BAD_REQUEST));
        var tasks = project.getTasks();
        return taskMapper.toDtos(tasks);
    }

    @Override
    public TaskDTO getTask(Long id) {
        var task = taskRepository.findById(id).orElseThrow(() -> new CustomErrorException("task does not exist", HttpStatus.BAD_REQUEST));
        return taskMapper.toDto(task);
    }

    @Override
    public void rejectTask(List<Long> ids) {
        taskRepository.deleteAllById(ids);
    }

    @Override
    public List<WorkflowDTO> getWorkflowForProject(Long projectId) {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.BAD_REQUEST));
        var workflow = project.getWorkflow();
        return workflowMapper.toDtos(Lists.newArrayList(workflow));
    }

    @Override
    public List<BasicPersonDataDTO> getPeopleWorkingOnProject(Long projectId) {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.BAD_REQUEST));
        return project.getTeam().stream()
                .map(TeamEntity::getPersons)
                .flatMap(Collection::stream)
                .map(personMapper::toBasicData)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskTypeDTO> getTaskTypes() {
        var taskTypes = taskTypeRepository.findAll();
        return taskTypeMapper.toDtos(taskTypes);
    }

    private String generateKeyForTask(ProjectEntity project, String taskKey){
        if (taskKey != null){
            return taskKey;
        }
        project.setTaskNumber(project.getTaskNumber()+1);
        projectRepository.save(project);
        return String.format("%s-%d", project.getKey(), project.getTaskNumber());
    }
}
