package com.mw.timesheets.domain.project;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.project.type.SprintDurationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectEntity extends CommonEntity {

    private String name;

    private String key;

    private String lead;

    @Enumerated(EnumType.STRING)
    private SprintDurationType sprintDuration;

    private LocalDateTime deadLine;

    private LocalDateTime endOfSprint;

    private Integer taskNumber;

    private Integer sprintNumber;

    private String sprintGoal;

    private boolean deleted;

    private LocalDateTime deletedTime;

    private byte[] photo;
}
