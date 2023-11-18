package com.mw.timesheets.commons.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DateUtils {

    public static List<LocalDate> getRangeOfDays(LocalDate from, LocalDate to, boolean withBusinessDays, boolean withWeekends, boolean withHolidays) {

        Predicate<LocalDate> isHoliday = date -> Arrays.stream(HolidayType.values())
                .map(holidayType -> holidayType.apply(date.getYear()))
                .toList().contains(date) && withHolidays;

        Predicate<LocalDate> isWeekend = date -> (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) && withWeekends;

        Predicate<LocalDate> isBusinessDay = isHoliday.and(isWeekend).negate().and(localDate -> withBusinessDays);

        long daysBetween = ChronoUnit.DAYS.between(from, to);

        return Stream.iterate(from, date -> date.plusDays(1))
                .limit(daysBetween)
                .filter(isBusinessDay.or(isWeekend).or(isBusinessDay))
                .collect(Collectors.toList());
    }

    public static Integer getNormalWorkingDaysCount(LocalDate from, LocalDate to) {
        return getRangeOfDays(from, to, true, false, false).size();
    }


}
