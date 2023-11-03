package com.mw.timesheets.domain.task;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.project.ProjectStatisticsEntity;
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
public class CommentsEntity extends CommonEntity {

    private LocalDateTime postTime;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private TaskEntity task;

    @OneToMany(mappedBy = "comment")
    private List<CommentResourceEntity> resources;

}
