package com.mw.timesheets.domain.timetrack.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class HistoryWithTotalTimeDTO {

    private List<TimeTrackerHistoryDTO> historyDTOs;

    private Long time;
}
