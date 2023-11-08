package com.mw.timesheets.domain.statistcs;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.project.ProjectEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "PROJECT_STATISTICS")
public class ProjectStatisticsEntity extends CommonEntity {

    private Integer sprintNumber;

    private LocalDate day;

    private Integer storyPointsDone;

    private Integer storyPointsCommitted;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private ProjectEntity project;
}
