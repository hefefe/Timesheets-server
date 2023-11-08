package com.mw.timesheets.domain.statistcs.model.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
public class SprintCompletionDTO extends CommittedAndUncommittedDTO{

    private Integer sprintNumber;
}
