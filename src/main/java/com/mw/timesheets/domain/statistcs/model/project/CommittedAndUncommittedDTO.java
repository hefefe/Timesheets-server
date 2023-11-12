package com.mw.timesheets.domain.statistcs.model.project;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder(toBuilder = true)
public class CommittedAndUncommittedDTO {

    private Double Committed;

    private Double uncommitted;
}
