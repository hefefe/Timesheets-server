package com.mw.timesheets.commons;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdFromToRequestDTO {
    private Long id;

    private LocalDate from;

    private LocalDate to;
}
