package com.mw.timesheets.domain.task;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.project.ProjectEntity;
import com.mw.timesheets.domain.project.WorkflowEntity;
import com.mw.timesheets.domain.statistcs.ProjectStatisticsEntity;
import com.mw.timesheets.domain.timetrack.HistoryEntity;
import com.mw.timesheets.domain.timetrack.TimeTrackEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE TASK SET deleted = true, deleted_time = NOW() WHERE id=?; DELETE FROM COMMENT_RESOURCES WHERE comment_id=?;")
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

    @OneToMany(mappedBy = "task")
    private List<CommentEntity> comments;

    @OneToMany(mappedBy = "task")
    private List<TimeTrackEntity> timeTrack;

    @OneToMany(mappedBy = "task")
    private List<HistoryEntity> history;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskEntity that)) return false;
        return isDeleted() == that.isDeleted() && Objects.equals(getName(), that.getName()) && Objects.equals(getKey(), that.getKey()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getDoneDate(), that.getDoneDate()) && Objects.equals(getStoryPoints(), that.getStoryPoints()) && Objects.equals(getDeletedTime(), that.getDeletedTime()) && Objects.equals(getTaskType(), that.getTaskType()) && Objects.equals(getProject(), that.getProject()) && Objects.equals(getPerson(), that.getPerson()) && Objects.equals(getWorkflow(), that.getWorkflow()) && Objects.equals(getComments(), that.getComments()) && Objects.equals(getTimeTrack(), that.getTimeTrack()) && Objects.equals(getHistory(), that.getHistory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getKey(), getDescription(), getDoneDate(), getStoryPoints(), isDeleted(), getDeletedTime(), getTaskType(), getProject(), getPerson(), getWorkflow(), getComments(), getTimeTrack(), getHistory());
    }
}
