package com.mw.timesheets.domain.project;

import com.mw.timesheets.commons.CommonEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectStatisticsEntity extends CommonEntity {

    private Integer sprintNumber;

    private LocalDate day;

    private Integer storyPointsDone;

    private Integer storyPointsCommitted;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private ProjectEntity project;
}
