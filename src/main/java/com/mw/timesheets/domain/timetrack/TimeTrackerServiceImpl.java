package com.mw.timesheets.domain.timetrack;

import com.google.common.collect.Lists;
import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.commons.jwt.SecurityUtils;
import com.mw.timesheets.commons.properties.StatisticsProperties;
import com.mw.timesheets.commons.util.DateUtils;
import com.mw.timesheets.commons.util.HolidayType;
import com.mw.timesheets.domain.person.PersonRepository;
import com.mw.timesheets.domain.project.ProjectMapper;
import com.mw.timesheets.domain.task.TaskMapper;
import com.mw.timesheets.domain.task.TaskRepository;
import com.mw.timesheets.domain.timetrack.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.mw.timesheets.commons.util.DateUtils.getSystemTime;

@Service
@RequiredArgsConstructor
public class TimeTrackerServiceImpl implements TimeTrackService {

    private final TimeTrackRepository timeTrackRepository;
    private final TimeTrackerMapper timeTrackerMapper;
    private final HistoryRepository historyRepository;
    private final TimerHistoryMapper historyMapper;
    private final SecurityUtils securityUtils;
    private final PersonRepository personRepository;
    private final TaskRepository taskRepository;
    private final StatisticsProperties statisticsProperties;
    private final TaskMapper taskMapper;
    private final ProjectMapper projectMapper;

    @Override
    public IsStartedDTO startTracking(BasicTimerDataDTO timeTrackerData) {
        if (timeTrackRepository.existsByPersonId(securityUtils.getPersonByEmail().getId())) {
            throw new CustomErrorException("stop current time tracker", HttpStatus.BAD_REQUEST);
        }
        var loggedEmployee = securityUtils.getPersonByEmail();
        var startTimer = timeTrackerMapper.toEntity(timeTrackerData);
        startTimer.setPerson(loggedEmployee);
        startTimer.setStarted(getSystemTime().toLocalTime().plusHours(1));
        startTimer.setActivityDate(getSystemTime().toLocalDate().plusDays(1));
        if(timeTrackerData.getTaskId() != null)
            startTimer.setTask(taskRepository.findById(timeTrackerData.getTaskId()).orElseThrow(() -> new CustomErrorException("task does not exist", HttpStatus.BAD_REQUEST)));
        startTimer.setHourlyPay(loggedEmployee.getHourlyPay());
        startTimer.setWorkToDoInHours(isNowDateWeekendOrHoliday() ? 0 : loggedEmployee.getWorkDuringWeekInHours() / statisticsProperties.getWorkingDays());
        timeTrackRepository.save(startTimer);

        return isStarted();
    }

    private boolean isNowDateWeekendOrHoliday() {
        var date = LocalDate.now();
        var isWeekend = date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
        var isHoliday = Arrays.stream(HolidayType.values()).map(holidayType -> holidayType.apply(date.getYear())).toList().contains(date);
        return isWeekend || isHoliday;
    }

    @Override
    public void stopTrackingTime() {
        stopTrackingTime(securityUtils.getPersonByEmail().getId());
    }

    @Override
    public IsStartedDTO isStarted() {
        var tracker =  timeTrackRepository.findByPersonUserEmail(securityUtils.getEmail());
        if(tracker.isPresent()){
            return IsStartedDTO.builder()
                    .startedTime(tracker.get().getStarted().minusHours(1))
                    .description(tracker.get().getDescription())
                    .task(taskMapper.toDto(tracker.get().getTask()))
                    .project(tracker.get().getTask() != null?projectMapper.toDto(tracker.get().getTask().getProject()):null)
                    .build();
        }
        return IsStartedDTO.builder().build();
    }

    @Override
    public void stopTrackingTime(Long id) {
        if (!timeTrackRepository.existsByPersonId(id)) {
            throw new CustomErrorException("no tracker to stop", HttpStatus.BAD_REQUEST);
        }
        var tracker = timeTrackRepository.findByPersonUserEmail(securityUtils.getEmail()).orElseThrow(() -> new CustomErrorException("User not found", HttpStatus.BAD_REQUEST));
        var person = personRepository.findById(id).orElseThrow(() -> new CustomErrorException("person does not exist", HttpStatus.BAD_REQUEST));
        var history = timeTrackerMapper.timeTrackerToHistoryEntity(tracker);
        history.setId(null);
        history.setPerson(person);
        history.setEnded(getSystemTime().toLocalTime().plusHours(1));
        if(!DateUtils.getSystemTime().toLocalDate().isEqual(history.getActivityDate())){
            history.setEnded(LocalTime.of(23,59,59));
        }
        history.setActivityDate(history.getActivityDate().plusDays(1));
        historyRepository.save(history);
        timeTrackRepository.deleteById(tracker.getId());
    }

    @Override
    public HistoryWithTotalTimeDTO getHistoryOfGivenUser(Long personId, LocalDate from, LocalDate to, Predicate<HistoryEntity> predicate, boolean withStartedTime) {
        var person = personRepository.findById(personId).orElseThrow(() -> new CustomErrorException("person does not exist", HttpStatus.BAD_REQUEST));
        var historyFromPerson = person.getHistory();

        if(withStartedTime) {
            var timer = timeTrackRepository.findByPersonUserEmail(person.getUser().getEmail());
            if(timer.isPresent()) {
                var timerToHistory = timeTrackerMapper.timeTrackerToHistoryEntity(timer.get());
                if (timerToHistory != null) {
                    timerToHistory.setEnded(DateUtils.getSystemTime().toLocalTime());
                    historyFromPerson.add(timerToHistory);
                }
            }
        }

        var historyMap = historyFromPerson.stream()
                .filter(history -> history.getActivityDate().isBefore(to.plusDays(1)) && history.getActivityDate().isAfter(from.minusDays(1)))
                .filter(predicate)
                .map(historyMapper::toDto)
                .peek(time -> time.setWorkFrom(time.getWorkFrom().minusHours(1)))
                .peek(time -> time.setWorkTo(time.getWorkTo().minusHours(1)))
                .peek(history -> history.setTime(getTimeDiffInMinutes(history.getWorkFrom(), history.getWorkTo())))
                .collect(Collectors.groupingBy(TrackedDataDTO::getActivityDate));

        var historyList = mapToTimeTrackerHistoryDto(historyMap);
        return HistoryWithTotalTimeDTO.builder()
                .historyDTOs(historyList)
                .time(historyList.stream()
                        .map(TimeTrackerHistoryDTO::getTime)
                        .reduce(0L, Long::sum))
                .from(from)
                .to(to)
                .build();
    }

    @Override
    public HistoryWithTotalTimeDTO getHistoryOfUser() {
        var monday = getSystemTime().toLocalDate().with(DayOfWeek.MONDAY);
        var sunday = getSystemTime().toLocalDate().with(DayOfWeek.SUNDAY);
        return getHistoryOfGivenUser(securityUtils.getPersonByEmail().getId(), monday, sunday, historyEntity -> true, false);
    }

    private List<TimeTrackerHistoryDTO> mapToTimeTrackerHistoryDto(Map<LocalDate, List<TrackedDataDTO>> groupedHistory) {
        List<TimeTrackerHistoryDTO> history = Lists.newArrayList();

        groupedHistory.forEach((key, value) -> history.add(TimeTrackerHistoryDTO.builder()
                .dateOfActivity(key)
                .trackedData(value)
                .time(value.stream()
                        .map(TrackedDataDTO::getTime)
                        .reduce(0L, Long::sum))
                .build()));
        return history;
    }

    private Long getTimeDiffInMinutes(LocalTime from, LocalTime to) {
        return ChronoUnit.MINUTES.between(from, to);
    }
}
