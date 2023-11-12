package com.mw.timesheets.domain.statistcs.model.project;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatisticsDTO {

    private Long timeTracked;

    private Double velocity;

    private BigDecimal MoneySpent;

    private Integer numberOfEmployees;

    private Integer tasksDone;

    private List<BurnDownDTO> data;

    private List<TasksDoneDTO> taskDoneByType;

    private List<SprintCompletionDTO> sprintCompletion;
}
