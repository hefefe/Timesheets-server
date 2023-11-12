package com.mw.timesheets.domain.statistcs.model.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder(toBuilder = true)
public class BurnDownDTO extends CommittedAndUncommittedDTO{

    private LocalDate date;
}
