package com.mw.timesheets.domain.statistcs.model.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonCompletionRateDTO {

    private String project;

    private Double completionRate;
}
