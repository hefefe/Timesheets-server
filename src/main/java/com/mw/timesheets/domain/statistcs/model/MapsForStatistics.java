package com.mw.timesheets.domain.statistcs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class MapsForStatistics {

    private Map<LocalDate, Pair<Integer, Double>> userData;
    private Map<LocalDate, Long> userMinutes;
}
