package com.mw.timesheets.domain.statistcs.model.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonStatisticsDTO {

    private Double completionRate;

    private Integer yearsOfEmployment;

    private Double overtimeRatio;

    private BigDecimal Pay;

    private Integer sumOfStoryPointsDone;

    private List<TasksAndHoursDTO> tasksAndHours;

    private List<StoryPointsDoneDTO> storyPointsDone;

}
