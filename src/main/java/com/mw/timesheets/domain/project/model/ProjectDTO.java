package com.mw.timesheets.domain.project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mw.timesheets.domain.person.model.PersonDTO;
import com.mw.timesheets.domain.project.type.SprintDurationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDTO {

    private Long id;

    private String name;

    private Integer sprintNumber;

    private LocalDateTime endOfSprint;

    private PersonDTO person;

    private String key;

    private SprintDurationType sprintDuration;

    private byte[] photo;

    private List<String> workflow;

    private List<Long> persons;
}
