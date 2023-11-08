package com.mw.timesheets.domain.statistcs;

import com.mw.timesheets.commons.CustomErrorException;
import com.mw.timesheets.commons.StatisticsProperties;
import com.mw.timesheets.commons.util.DateUtils;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.statistcs.model.employee.PersonStatisticsDTO;
import com.mw.timesheets.domain.statistcs.model.project.DataListDTO;
import com.mw.timesheets.domain.task.TaskEntity;
import com.mw.timesheets.domain.timetrack.TimeTrackService;
import com.mw.timesheets.domain.timetrack.model.HistoryWithTotalTimeDTO;
import com.mw.timesheets.domain.timetrack.model.TimeTrackerHistoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final TimeTrackService timeTrackService;
    private final StatisticsProperties statisticsProperties;

    @Override
    public PersonStatisticsDTO generateStatisticsForPerson(Long personId, LocalDate from, LocalDate to) {
        return null;
    }

    @Override
    public PersonStatisticsDTO generateStatisticsForTeam(Long teamId, LocalDate from, LocalDate to) {
        return null;
    }

    @Override
    public DataListDTO generateBurnDownChart(Long projectId) {
        return null;
    }

    @Override
    public DataListDTO generateCompletionStatistics(Long projectId) {
        return null;
    }

    private PersonStatisticsDTO generateStatisticsForPerson(List<PersonEntity> persons, LocalDate from, LocalDate to) {
        return null;
    }

    private Double calculatePayment(List<PersonEntity> persons, LocalDate from, LocalDate to){
        return persons.stream()
                .map(person -> calculatePayForUser(person, from, to))
                .reduce(0.0, Double::sum);
    }
    private Double calculatePayForUser(PersonEntity person, LocalDate from, LocalDate to){

        var history = timeTrackService.getHistoryOfGivenUser(person.getId(), from, to);
        var holidaysDaysRange = DateUtils.getHolidays(from, to);
        var countOfNormalWorkingDays = DateUtils.getNormalWorkingDaysCount(from, to);

        var work = getWorkingHours(history, timeTrackerHistoryDTO -> !holidaysDaysRange.contains(timeTrackerHistoryDTO.getDateOfActivity()));
        var holidayWork = getWorkingHours(history, timeTrackerHistoryDTO -> holidaysDaysRange.contains(timeTrackerHistoryDTO.getDateOfActivity()));

        var overtime =(double) calculateOvertime(work, person.getWorkDuringWeekInHours(), countOfNormalWorkingDays)/statisticsProperties.getTimeInterval();
        var normalWork =(work - overtime)/statisticsProperties.getTimeInterval();
        var holidayHours = (double) holidayWork/statisticsProperties.getTimeInterval();

        return normalWork * person.getHourlyPay() + overtime * person.getHourlyPay() * statisticsProperties.getOvertimePayRatio() + holidayHours * person.getHourlyPay() * statisticsProperties.getHolidayPayRatio();

    }

    private Long calculateOvertime(Long work, Integer workDuringWeekInHours, Integer numberOfDays){
        var expectedWorkTime = calculateExpectedWorkingHours(workDuringWeekInHours,numberOfDays);
        return work - expectedWorkTime < 0 ? 0 : work - expectedWorkTime;
    }

    private Long calculateExpectedWorkingHours(Integer workDuringWeekInHours, Integer numberOfDays){

        return workDuringWeekInHours.longValue() / statisticsProperties.getWorkingDays() * statisticsProperties.getTimeInterval() * numberOfDays;
    }

    private Double getAverageYearsOfEmployment(List<PersonEntity> persons){
        return persons.stream()
                .mapToDouble(person -> Period.between(person.getDateOfEmployment(), LocalDate.now()).getYears())
                .average()
                .orElseThrow(() -> new CustomErrorException("something went wrong :(", HttpStatus.I_AM_A_TEAPOT));
    }

    private Double averageOverTimeRatio(List<PersonEntity> persons, LocalDate from, LocalDate to) {
        return persons.stream()
                .mapToDouble(person -> overTimeRatioForPerson(person, from, to))
                .average()
                .orElseThrow(() -> new CustomErrorException("something went wrong :(", HttpStatus.I_AM_A_TEAPOT));
    }

    private Double overTimeRatioForPerson(PersonEntity person, LocalDate from, LocalDate to) {
        var history = timeTrackService.getHistoryOfGivenUser(person.getId(), from, to);
        var holidaysDaysRange = DateUtils.getHolidays(from, to);
        var countOfNormalWorkingDays = DateUtils.getNormalWorkingDaysCount(from, to);

        var work = getWorkingHours(history, timeTrackerHistoryDTO -> !holidaysDaysRange.contains(timeTrackerHistoryDTO.getDateOfActivity()));
        var holidayWork = getWorkingHours(history, timeTrackerHistoryDTO -> holidaysDaysRange.contains(timeTrackerHistoryDTO.getDateOfActivity()));

        var overtime = calculateOvertime(work+holidayWork, person.getWorkDuringWeekInHours(), countOfNormalWorkingDays)/statisticsProperties.getTimeInterval();
        var normalHours = calculateExpectedWorkingHours(person.getWorkDuringWeekInHours(), countOfNormalWorkingDays).doubleValue();
        return normalHours==0 ? 0 : overtime/normalHours;
    }

    private Long getWorkingHours(HistoryWithTotalTimeDTO history, Predicate<TimeTrackerHistoryDTO> holidayPredicate){
        return history.getHistoryDTOs().stream()
                .filter(holidayPredicate)
                .map(TimeTrackerHistoryDTO::getTime)
                .reduce(0L, Long::sum);
    }

}
