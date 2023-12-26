package com.mw.timesheets.domain.task;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.commons.jwt.SecurityUtils;
import com.mw.timesheets.commons.util.DateUtils;
import com.mw.timesheets.domain.person.PersonRepository;
import com.mw.timesheets.domain.person.type.Roles;
import com.mw.timesheets.domain.project.ProjectEntity;
import com.mw.timesheets.domain.project.ProjectRepository;
import com.mw.timesheets.domain.project.WorkflowRepository;
import com.mw.timesheets.domain.task.model.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final TaskTypeMapper taskTypeMapper;
    private final SecurityUtils securityUtils;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public TaskDTO saveTask(TaskDTO taskDTO, Long projectId) {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.BAD_REQUEST));
        var task = taskMapper.toEntity(taskDTO);

        task.setProject(project);
        task.setKey(generateKeyForTask(project, task.getKey()));
        task.setWorkflow(task.getWorkflow() != null? task.getWorkflow() : project.getWorkflow().stream().findFirst().orElseThrow(() -> new CustomErrorException("No workflow", HttpStatus.INTERNAL_SERVER_ERROR)));
        if (taskDTO.getWorkflow() != null) {
            var isTaskDone = Iterables.getLast(project.getWorkflow()).getName().equals(taskDTO.getWorkflow().getName());
            if (securityUtils.getRole() == Roles.ROLE_USER && isTaskDone)
                throw new CustomErrorException("cannot set to done by user", HttpStatus.BAD_REQUEST);
            if (isTaskDone && task.getDoneDate() == null) {
                task.setDoneDate(getSystemTime().toLocalDate());
            } else if (!isTaskDone) {
                task.setDoneDate(null);
            }
        }

        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public List<TaskGroupDTO> getTasksForProject(Long projectId) {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.BAD_REQUEST));
        return project.getWorkflow().stream()
                .map(workflow -> TaskGroupDTO.builder()
                        .workflowDTO(workflowMapper.toDto(workflow))
                        .tasks(taskMapper.toDtos(workflow.getTasks().stream().filter(task -> !task.isDeleted()).collect(Collectors.toList())))
                        .build())
                .collect(Collectors.toList());
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

    @Override
    public CommentDTO saveComment(CommentDTO commentDTO, Long taskId) {
        var commentToSave = CommentEntity.builder()
                .task(taskRepository.findById(taskId).orElseThrow(() -> new CustomErrorException("task does not exist", HttpStatus.BAD_REQUEST)))
                .commentContent(commentDTO.getCommentContent())
                .person(securityUtils.getPersonByEmail())
                .postTime(DateUtils.getSystemTime())
                .build();
        commentRepository.save(commentToSave);
        return null;
    }

    @Override
    public CommentDTO saveCommentResources(List<MultipartFile> multipartFiles, Long commentId) {
        var comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomErrorException("comment does not exist", HttpStatus.BAD_REQUEST));
        var resources = multipartFiles.stream()
                .map(file -> mapMultipartFileToEntity(file, comment))
                .collect(Collectors.toList());
        comment.setCommentResources(resources);
        var savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    public List<CommentDTO> getCommentsForTask(Long taskId) {
        var comments = taskRepository.findById(taskId).orElseThrow(() -> new CustomErrorException("task does not exist", HttpStatus.BAD_REQUEST)).getComments();
        return commentMapper.toDtos(comments);
    }

    private String generateKeyForTask(ProjectEntity project, String taskKey) {
        if (taskKey != null) {
            return taskKey;
        }
        project.setTaskNumber(project.getTaskNumber() + 1);
        projectRepository.save(project);
        return project.getTaskNumber().toString();
    }

    private String getExtensionFromFileName(String fileName) {
        String[] arr = fileName.split("\\.");
        return arr[arr.length - 1];
    }

    @SneakyThrows
    private CommentResourceEntity mapMultipartFileToEntity(MultipartFile multipartFile, CommentEntity comment) {
        return CommentResourceEntity.builder()
                .resource(multipartFile.getBytes())
                .extension(getExtensionFromFileName(multipartFile.getName()))
                .comment(comment)
                .build();
    }

}
