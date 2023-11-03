package com.mw.timesheets.domain.project.model;

import com.mw.timesheets.domain.person.model.PersonDTO;
import com.mw.timesheets.domain.project.type.SprintDurationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDTO {

    private Long id;

    private String name;

    private String sprintGoal;

    private Integer sprintNumber;

    private LocalDateTime endOfSprint;

    private PersonDTO lead;

    private String key;

    private SprintDurationType sprintDuration;

    private LocalDateTime startOfSprint;

    private byte[] photo;

    private Set<String> workflow;

    private List<Long> teams;
}
