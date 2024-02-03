package com.mw.timesheets.commons.util;

import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DateUtils {

    public static List<LocalDate> getRangeOfDays(LocalDate from, LocalDate to, boolean withBusinessDays, boolean withWeekends, boolean withHolidays) {
        Predicate<LocalDate> isHoliday = date -> (Arrays.stream(HolidayType.values()).map(holidayType -> holidayType.apply(date.getYear())).toList().contains(date)) && withHolidays;

        Predicate<LocalDate> isWeekend = date -> (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) && withWeekends;

        Predicate<LocalDate> isBusinessDay = date -> from.datesUntil(to.plusDays(1))
                .filter(datee -> !Arrays.stream(HolidayType.values()).map(holidayType -> holidayType.apply(date.getYear())).toList().contains(date))
                .filter(datee -> !(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY))
                .filter(datee -> withBusinessDays).toList().contains(date);

        return from.datesUntil(to.plusDays(1))
                .filter(isHoliday.or(isWeekend).or(isBusinessDay))
                .collect(Collectors.toList());
    }

    public static Integer getNormalWorkingDaysCount(LocalDate from, LocalDate to) {
        return getRangeOfDays(from, to, true, false, false).size();
    }

    public static LocalDateTime getSystemTime() {
        return LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
    }
}
