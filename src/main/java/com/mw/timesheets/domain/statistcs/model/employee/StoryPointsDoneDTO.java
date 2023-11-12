package com.mw.timesheets.domain.statistcs.model.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryPointsDoneDTO {

    private LocalDate date;

    private Integer storyPoints;
}
