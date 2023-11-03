package com.mw.timesheets.domain.timetrack.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TrackedDataDTO extends BasicTimerDataDTO {

    private LocalDate activityDate;

    private LocalTime workFrom;

    private LocalTime workTo;

    private Long time;
}
