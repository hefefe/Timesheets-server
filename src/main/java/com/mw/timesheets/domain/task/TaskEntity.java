package com.mw.timesheets.domain.task;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.project.ProjectEntity;
import com.mw.timesheets.domain.project.ProjectStatisticsEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
}
