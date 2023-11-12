package com.mw.timesheets.domain.statistcs;

import com.mw.timesheets.domain.statistcs.model.employee.PersonStatisticsDTO;
import com.mw.timesheets.domain.statistcs.model.project.ProjectStatisticsDTO;

import java.time.LocalDate;

public interface StatisticsService {

    PersonStatisticsDTO generateStatisticsForPerson(Long personId, LocalDate from, LocalDate to);

    ProjectStatisticsDTO generateStatisticsForProject(Long projectId, LocalDate from, LocalDate to);

}
