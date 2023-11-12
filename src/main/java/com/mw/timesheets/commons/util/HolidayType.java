package com.mw.timesheets.commons.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.function.Function;

@AllArgsConstructor
@Getter
public enum HolidayType implements Function<Integer, LocalDate> {
    NEW_YEAR(year -> LocalDate.of(year, 1, 1)),
    EPIPHANY(year -> LocalDate.of(year, 1, 6)),
    EASTER(HolidayType::easterDate),
    EASTER_MONDAY(year -> easterDate(year).plusDays(1)),
    PENTECOST(year -> easterDate(year).plusWeeks(7)),
    LABOR_DAY(year -> LocalDate.of(year, 5, 1)),
    CONSTITUTION_DAY(year -> easterDate(year).plusDays(60)),
    CORPUS_CHRISTI(year -> LocalDate.of(year, 6, 8)),
    ASSUMPTION_OF_THE_BLESSED_VIRGIN_MARY(year -> LocalDate.of(year, 8, 15)),
    ALL_SAINTS_DAY(year -> LocalDate.of(year, 11, 1)),
    INDEPENDENCE_DAY(year -> LocalDate.of(year, 11, 11)),
    FIRST_DAY_OF_CHRISTMAS(year -> LocalDate.of(year, 12, 25)),
    SECOND_DAY_OF_CHRISTMAS(year -> LocalDate.of(year, 12, 26));

    private final Function<Integer, LocalDate> date;

    @Override
    public LocalDate apply(Integer year) {
        return date.apply(year);
    }

    private static LocalDate easterDate(int year)
    {
        int a = year % 19,
                b = year / 100,
                c = year % 100,
                d = b / 4,
                e = b % 4,
                g = (8 * b + 13) / 25,
                h = (19 * a + b - d - g + 15) % 30,
                j = c / 4,
                k = c % 4,
                m = (a + 11 * h) / 319,
                r = (2 * e + 2 * j - k - h + m + 32) % 7,
                n = (h - m + r + 90) / 25,
                p = (h - m + r + n + 19) % 32;
        return LocalDate.of(year, n, p);
    }
}
