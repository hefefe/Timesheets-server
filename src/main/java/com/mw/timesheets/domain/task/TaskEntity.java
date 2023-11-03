package com.mw.timesheets.domain.task;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.person.AddressEntity;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.project.ProjectEntity;
import com.mw.timesheets.domain.project.ProjectStatisticsEntity;
import com.mw.timesheets.domain.project.WorkflowEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity extends CommonEntity {

    private String name;

    private String key;

    private String description;

    private Integer storyPoints;

    private boolean deleted;

    private LocalDateTime deletedTime;

    @ManyToOne
    @JoinColumn(name = "task_type_id", referencedColumnName = "id")
    private TaskTypeEntity taskType;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private ProjectEntity project;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private PersonEntity person;

    @OneToMany(mappedBy = "task")
    private List<CommentsEntity> comments;

    @ManyToOne
    @JoinColumn(name = "workflow_id", referencedColumnName = "id")
    private WorkflowEntity workflow;
}
