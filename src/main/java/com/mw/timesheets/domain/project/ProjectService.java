package com.mw.timesheets.domain.project;

import com.mw.timesheets.domain.project.model.ProjectDTO;

import java.util.List;

public interface ProjectService {

    ProjectDTO saveProject(ProjectDTO projectDTO);

    List<ProjectDTO> getProjects();

    void deleteProject(Long id);

    ProjectDTO getProject(Long id);
}
