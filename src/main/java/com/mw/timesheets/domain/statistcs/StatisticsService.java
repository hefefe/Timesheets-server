package com.mw.timesheets.domain.statistcs;

import com.mw.timesheets.commons.IdFromToRequestDTO;
import com.mw.timesheets.domain.statistcs.model.employee.PersonStatisticsDTO;
import com.mw.timesheets.domain.statistcs.model.project.ProjectStatisticsDTO;

public interface StatisticsService {

    PersonStatisticsDTO generateStatisticsForPerson(IdFromToRequestDTO statisticsRequestDTO);

    ProjectStatisticsDTO generateStatisticsForProject(IdFromToRequestDTO statisticsRequestDTO);

}
