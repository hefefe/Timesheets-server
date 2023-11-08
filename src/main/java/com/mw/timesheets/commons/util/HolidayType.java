package com.mw.timesheets.commons.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.function.Function;

@AllArgsConstructor
@Getter
public enum HolidayType implements Function<Integer, LocalDate> {
    NEW_YEAR(year -> getDate(year, 1, 1)),
    EPIPHANY(year -> getDate(year, 1, 6)),
    EASTER(HolidayType::gaussEaster),
    EASTER_MONDAY(year -> gaussEaster(year).plusDays(1)),
    PENTECOST(year -> gaussEaster(year).plusWeeks(7)),
    LABOR_DAY(year -> getDate(year, 5, 1)),
    CONSTITUTION_DAY(year -> getDate(year, 5, 3)),
    CORPUS_CHRISTI(year -> getDate(year, 6, 8)),
    ASSUMPTION_OF_THE_BLESSED_VIRGIN_MARY(year -> getDate(year, 8, 15)),
    ALL_SAINTS_DAY(year -> getDate(year, 11, 1)),
    INDEPENDENCE_DAY(year -> getDate(year, 11, 11)),
    FIRST_DAY_OF_CHRISTMAS(year -> getDate(year, 12, 25)),
    SECOND_DAY_OF_CHRISTMAS(year -> getDate(year, 12, 26));

    private final Function<Integer, LocalDate> date;

    @Override
    public LocalDate apply(Integer year) {
        return date.apply(year);
    }

    public static LocalDate getDate(Integer year, Integer month, Integer day){
        return LocalDate.of(year, month, day);
    }

    static LocalDate gaussEaster(Integer year) {
        float A, B, C, P, Q, M, N, D, E;

        A = year % 19;
        B = year % 4;
        C = year % 7;
        P = (float) Math.floor(year / 100);
        Q = (float) Math.floor((13 + 8 * P) / 25);
        M = (int) (15 - Q + P - Math.floor(P / 4)) % 30;
        N = (int) (4 + P - Math.floor(P / 4)) % 7;
        D = (19 * A + M) % 30;
        E = (2 * B + 4 * C + 6 * D + N) % 7;
        int days = (int) (22 + D + E);

        if ((D == 29) && (E == 6)) {
            return LocalDate.of(year, 4, 19);
        } else if ((D == 28) && (E == 6)) {
            return LocalDate.of(year, 4, 18);
        } else {
            if (days > 31) {
                return LocalDate.of(year, 4, (days - 31));
            } else {
                return LocalDate.of(year, 4, days);
            }
        }
    }
}
