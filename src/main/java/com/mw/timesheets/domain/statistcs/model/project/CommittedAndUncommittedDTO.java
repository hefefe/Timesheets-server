package com.mw.timesheets.domain.statistcs.model.project;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
public class CommittedAndUncommittedDTO {

    private Integer Committed;

    private Integer uncommitted;
}
