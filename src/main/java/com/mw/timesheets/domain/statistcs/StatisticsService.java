package com.mw.timesheets.domain.statistcs;

import com.mw.timesheets.domain.statistcs.model.employee.PersonStatisticsDTO;
import com.mw.timesheets.domain.statistcs.model.project.DataListDTO;

import java.time.LocalDate;

public interface StatisticsService {

    PersonStatisticsDTO generateStatisticsForPerson(Long personId, LocalDate from, LocalDate to);

    PersonStatisticsDTO generateStatisticsForTeam(Long teamId, LocalDate from, LocalDate to);

    DataListDTO generateBurnDownChart(Long projectId);

    DataListDTO generateCompletionStatistics(Long projectId);
}
