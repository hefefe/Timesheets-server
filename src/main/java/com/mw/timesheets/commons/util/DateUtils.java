package com.mw.timesheets.commons.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DateUtils {

    public static List<LocalDate> getWorkingDays(LocalDate from, LocalDate to){

        Predicate<LocalDate> isHoliday = date -> Arrays.stream(HolidayType.values())
                .map(holidayType -> holidayType.apply(date.getYear()))
                .toList().contains(date);

        long daysBetween = ChronoUnit.DAYS.between(from, to);

        return Stream.iterate(from, date -> date.plusDays(1))
                .limit(daysBetween)
                .filter(isHoliday.negate())
                .collect(Collectors.toList());
    }

    public static Integer getNormalWorkingDaysCount(LocalDate from, LocalDate to){

        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        Predicate<LocalDate> isHoliday = date -> Arrays.stream(HolidayType.values())
                .map(holidayType -> holidayType.apply(date.getYear()))
                .toList().contains(date);

        long daysBetween = ChronoUnit.DAYS.between(from, to);

        return Stream.iterate(from, date -> date.plusDays(1))
                .limit(daysBetween)
                .filter(isHoliday.or(isWeekend).negate())
                .toList()
                .size();
    }

    public static List<LocalDate> getHolidays(LocalDate from, LocalDate to){
        Predicate<LocalDate> isHoliday = date -> Arrays.stream(HolidayType.values())
                .map(holidayType -> holidayType.apply(date.getYear()))
                .toList().contains(date);

        long daysBetween = ChronoUnit.DAYS.between(from, to);

        return Stream.iterate(from, date -> date.plusDays(1))
                .limit(daysBetween)
                .filter(isHoliday)
                .collect(Collectors.toList());
    }

    public static List<LocalDate> getWeekendsAndHolidays(LocalDate from, LocalDate to){

        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        Predicate<LocalDate> isHoliday = date -> Arrays.stream(HolidayType.values())
                .map(holidayType -> holidayType.apply(date.getYear()))
                .toList().contains(date);

        long daysBetween = ChronoUnit.DAYS.between(from, to);

        return Stream.iterate(from, date -> date.plusDays(1))
                .limit(daysBetween)
                .filter(isHoliday.or(isWeekend))
                .toList();
    }


}
