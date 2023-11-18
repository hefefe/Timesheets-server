package com.mw.timesheets.domain.statistcs.model.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder(toBuilder = true)
public class CommittedAndUncommittedDTO {

    private Double Committed;

    private Double uncommitted;
}
