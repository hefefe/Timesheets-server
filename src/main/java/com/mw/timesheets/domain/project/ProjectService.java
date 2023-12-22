package com.mw.timesheets.domain.project;

import com.mw.timesheets.domain.project.model.ProjectDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {

    ProjectDTO saveProject(ProjectDTO projectDTO);

    List<ProjectDTO> getProjects(String name);

    void deleteProject(Long id);

    ProjectDTO getProject(Long id);

    ProjectDTO savePersonPhoto(Long projectId, MultipartFile photo);
}
