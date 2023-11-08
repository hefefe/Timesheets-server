package com.mw.timesheets.domain.statistcs.model.employee;

import java.util.List;

public class PersonStatisticsDTO {

    private Double completionRate;

    private Double yearsOfEmployment;

    private Double overtimeRatio;

    private Double Pay;

    private Integer sumOfStoryPointsDone;

    private List<TasksAndHoursDTO> tasksAndHours;

    private List<StoryPointsDoneDTO> storyPointsDone;

}
