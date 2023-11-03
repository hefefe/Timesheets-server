package com.mw.timesheets.domain.timetrack.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeTrackerHistoryDTO {

    private LocalDate dateOfActivity;

    private List<TrackedDataDTO> trackedData;

    private Long time;
}
