package com.mw.timesheets.domain.task;

import com.mw.timesheets.domain.person.model.BasicPersonDataDTO;
import com.mw.timesheets.domain.task.model.TaskDTO;
import com.mw.timesheets.domain.task.model.TaskTypeDTO;
import com.mw.timesheets.domain.task.model.WorkflowDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/task")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDTO> saveTask(@RequestBody TaskDTO taskDTO, @RequestParam Long projectId) {
        return ResponseEntity.ok(taskService.saveTask(taskDTO, projectId));
    }

    @GetMapping("project")
    public ResponseEntity<List<TaskDTO>> getTasksFroProject(@RequestParam Long projectId) {
        return ResponseEntity.ok(taskService.getTasksFroProject(projectId));
    }

    @GetMapping
    public ResponseEntity<TaskDTO> getTask(@RequestParam Long id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @DeleteMapping
    public ResponseEntity<Void> rejectTask(@RequestParam List<Long> ids) {
        taskService.rejectTask(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("workflow")
    public ResponseEntity<List<WorkflowDTO>> getWorkflowForProject(@RequestParam Long projectId) {
        return ResponseEntity.ok(taskService.getWorkflowForProject(projectId));
    }

    @GetMapping("hr")
    public ResponseEntity<List<BasicPersonDataDTO>> getPeopleWorkingOnProject(@RequestParam Long projectId) {
        return ResponseEntity.ok(taskService.getPeopleWorkingOnProject(projectId));
    }

    @GetMapping("types")
    public ResponseEntity<List<TaskTypeDTO>> getTaskTypes() {
        return ResponseEntity.ok(taskService.getTaskTypes());
    }

    @GetMapping("user")
    public ResponseEntity<List<TaskDTO>> getTasksByProjectAndUser(@RequestParam Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProjectAndUser(projectId));
    }

    @PutMapping
    public ResponseEntity<TaskDTO> changeWorkflowForTask(@RequestParam Long taskId, @RequestParam Long workFlowId) {
        return ResponseEntity.ok(taskService.changeWorkFlow(taskId, workFlowId));
    }
}
