package com.mw.timesheets.domain.project;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.project.type.SprintDurationType;
import com.mw.timesheets.domain.statistcs.ProjectStatisticsEntity;
import com.mw.timesheets.domain.task.TaskEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PROJECT")
public class ProjectEntity extends CommonEntity {

    private String name;

    @Column(name = "`key`")
    private String key;

    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private PersonEntity person;

    @Enumerated(EnumType.STRING)
    private SprintDurationType sprintDuration;

    private LocalDateTime endOfSprint;

    private Integer taskNumber;

    private Integer sprintNumber;

    private boolean deleted;

    private LocalDateTime deletedTime;

    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] photo;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "person_project",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private Set<PersonEntity> personsInProject = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private List<WorkflowEntity> workflow;

    @OneToMany(mappedBy = "project")
    private List<ProjectStatisticsEntity> statistics;

    @OneToMany(mappedBy = "project", cascade = {CascadeType.REMOVE})
    private List<TaskEntity> tasks;
}
