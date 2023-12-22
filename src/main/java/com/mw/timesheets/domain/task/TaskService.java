package com.mw.timesheets.domain.task;

import com.mw.timesheets.domain.task.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaskService {

    TaskDTO saveTask(TaskDTO taskDTO, Long projectId);

    List<TaskGroupDTO> getTasksForProject(Long projectId);

    TaskDTO getTask(Long id);

    void rejectTask(List<Long> ids);

    List<WorkflowDTO> getWorkflowForProject(Long projectId);

    List<TaskTypeDTO> getTaskTypes();

    List<TaskDTO> getTasksByProjectAndUser(Long projectId);

    TaskDTO changeWorkFlow(Long taskId, Long workFlowId);

    CommentDTO saveComment(CommentDTO commentDTO, Long taskId);

    CommentDTO saveCommentResources(List<MultipartFile> multipartFiles, Long CommentId);

    List<CommentDTO> getCommentsForTask(Long taskId);
}
