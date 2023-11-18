package com.mw.timesheets.domain.project;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.project.type.SprintDurationType;
import com.mw.timesheets.domain.statistcs.ProjectStatisticsEntity;
import com.mw.timesheets.domain.task.TaskEntity;
import com.mw.timesheets.domain.team.TeamEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

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
@SQLDelete(sql = "UPDATE PROJECT SET deleted = true, deleted_time = NOW() WHERE id=?")
@Table(name = "PROJECT")
public class ProjectEntity extends CommonEntity {

    private String name;

    private String key;

    @OneToOne
    @JoinColumn(name = "lead", referencedColumnName = "id")
    private PersonEntity lead;

    @Enumerated(EnumType.STRING)
    private SprintDurationType sprintDuration;

    private LocalDateTime endOfSprint;

    private Integer taskNumber;

    private Integer sprintNumber;

    private String sprintGoal;

    private boolean deleted;

    private LocalDateTime deletedTime;

    @Column(columnDefinition = "BLOB")
    private byte[] photo;

    @ManyToMany
    @JoinTable(
            name = "team_project",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id"))
    private Set<TeamEntity> team = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private List<WorkflowEntity> workflow;

    @OneToMany(mappedBy = "project")
    private List<ProjectStatisticsEntity> statistics;

    @OneToMany(mappedBy = "project", cascade = {CascadeType.REMOVE})
    private List<TaskEntity> tasks;
}
