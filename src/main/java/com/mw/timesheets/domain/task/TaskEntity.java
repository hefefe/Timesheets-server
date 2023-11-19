package com.mw.timesheets.domain.task;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.project.ProjectEntity;
import com.mw.timesheets.domain.project.WorkflowEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE TASK SET deleted = true, deleted_time = NOW() WHERE id=?")
@Table(name = "TASK")
public class TaskEntity extends CommonEntity {

    private String name;

    @Column(name = "`key`")
    private String key;

    private String description;

    private LocalDate doneDate;

    private Integer storyPoints;

    private boolean deleted;

    private LocalDateTime deletedTime;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "task_type_id", referencedColumnName = "id")
    private TaskTypeEntity taskType;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private ProjectEntity project;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private PersonEntity person;

    @ManyToOne
    @JoinColumn(name = "workflow_id", referencedColumnName = "id")
    private WorkflowEntity workflow;
}
