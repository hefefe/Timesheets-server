package com.mw.timesheets.domain.task;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.commons.jwt.SecurityUtils;
import com.mw.timesheets.domain.person.PersonMapper;
import com.mw.timesheets.domain.person.PersonRepository;
import com.mw.timesheets.domain.person.model.BasicPersonDataDTO;
import com.mw.timesheets.domain.person.type.Roles;
import com.mw.timesheets.domain.project.ProjectEntity;
import com.mw.timesheets.domain.project.ProjectRepository;
import com.mw.timesheets.domain.project.WorkflowRepository;
import com.mw.timesheets.domain.task.model.TaskDTO;
import com.mw.timesheets.domain.task.model.TaskTypeDTO;
import com.mw.timesheets.domain.task.model.WorkflowDTO;
import com.mw.timesheets.domain.team.TeamEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.mw.timesheets.commons.util.DateUtils.getSystemTime;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskTypeRepository taskTypeRepository;
    private final TaskMapper taskMapper;
    private final WorkflowRepository workflowRepository;
    private final WorkflowMapper workflowMapper;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final TaskTypeMapper taskTypeMapper;
    private final SecurityUtils securityUtils;

    @Override
    public TaskDTO saveTask(TaskDTO taskDTO, Long projectId) {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.BAD_REQUEST));
        var isTaskDone = Iterables.getLast(project.getWorkflow()).getName().equals(taskDTO.getWorkflow().getName());
        if (securityUtils.getRole() == Roles.ROLE_USER && isTaskDone)
            throw new CustomErrorException("cannot set to done by user", HttpStatus.BAD_REQUEST);

        var task = taskMapper.toEntity(taskDTO);
        task.setTaskType(taskTypeRepository.findById(taskDTO.getTaskType().getId()).orElseThrow(() -> new CustomErrorException("task type does not exist", HttpStatus.BAD_REQUEST)));
        task.setWorkflow(workflowRepository.findById(taskDTO.getWorkflow().getId()).orElseThrow(() -> new CustomErrorException("workflow element does not exist", HttpStatus.BAD_REQUEST)));
        task.setPerson(personRepository.findById(taskDTO.getPerson().getId()).orElseThrow(() -> new CustomErrorException("person does not exist", HttpStatus.BAD_REQUEST)));

        task.setProject(project);
        task.setKey(generateKeyForTask(project, task.getKey()));
        if (isTaskDone && task.getDoneDate() == null) {
            task.setDoneDate(getSystemTime().toLocalDate());
        } else if (!isTaskDone) {
            task.setDoneDate(null);
        }
        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
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

    @Override
    public List<TaskDTO> getTasksByProjectAndUser(Long projectId) {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.BAD_REQUEST));
        var tasks = project.getTasks().stream()
                .filter(task -> securityUtils.getPersonByEmail().getId().equals(task.getPerson().getId()))
                .collect(Collectors.toList());
        return taskMapper.toDtos(tasks);
    }

    @Override
    public TaskDTO changeWorkFlow(Long taskId, Long workFlowId) {
        var workFlow = workflowRepository.findById(workFlowId).orElseThrow(() -> new CustomErrorException("workflow does not exist", HttpStatus.BAD_REQUEST));
        var task = taskRepository.findById(taskId).orElseThrow(() -> new CustomErrorException("task does not exist", HttpStatus.BAD_REQUEST));
        var project = task.getProject();
        var isTaskDone = Iterables.getLast(project.getWorkflow()).getName().equals(workFlow.getName());
        if (securityUtils.getRole() == Roles.ROLE_USER && isTaskDone)
            throw new CustomErrorException("cannot set to done by user", HttpStatus.BAD_REQUEST);

        task.setWorkflow(workFlow);
        if (isTaskDone && task.getDoneDate() == null) {
            task.setDoneDate(getSystemTime().toLocalDate());
        } else if (!isTaskDone) {
            task.setDoneDate(null);
        }
        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    private String generateKeyForTask(ProjectEntity project, String taskKey) {
        if (taskKey != null) {
            return taskKey;
        }
        project.setTaskNumber(project.getTaskNumber() + 1);
        projectRepository.save(project);
        return String.format("%s-%d", project.getKey(), project.getTaskNumber());
    }
}
