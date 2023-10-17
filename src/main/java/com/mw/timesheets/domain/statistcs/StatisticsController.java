package com.mw.timesheets.domain.statistcs;

import com.mw.timesheets.commons.IdFromToRequestDTO;
import com.mw.timesheets.domain.statistcs.model.employee.PersonStatisticsDTO;
import com.mw.timesheets.domain.statistcs.model.project.ProjectStatisticsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @PostMapping("person")
    public ResponseEntity<PersonStatisticsDTO> generateStatisticsForPerson(@RequestBody IdFromToRequestDTO statisticsRequestDTO) {
        return ResponseEntity.ok(statisticsService.generateStatisticsForPerson(statisticsRequestDTO));
    }

    @PostMapping("project")
    public ResponseEntity<ProjectStatisticsDTO> generateStatisticsForProject(@RequestBody IdFromToRequestDTO statisticsRequestDTO) {
        return ResponseEntity.ok(statisticsService.generateStatisticsForProject(statisticsRequestDTO));
    }
}
