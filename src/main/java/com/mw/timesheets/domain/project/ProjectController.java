package com.mw.timesheets.domain.project;

import com.mw.timesheets.domain.person.model.PersonDTO;
import com.mw.timesheets.domain.project.model.ProjectDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/project")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDTO> saveProject(@RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectService.saveProject(projectDTO));
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getProjects(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(projectService.getProjects(name));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProject(@RequestParam Long id) {
        projectService.deleteProject(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProject(id));
    }

    @PostMapping("photo")
    public ResponseEntity<ProjectDTO> saveProjectPhoto(@RequestParam Long projectId, @RequestParam MultipartFile photo) {
        return ResponseEntity.ok(projectService.savePersonPhoto(projectId, photo));
    }
}
