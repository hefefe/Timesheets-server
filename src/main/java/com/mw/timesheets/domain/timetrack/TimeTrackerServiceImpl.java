package com.mw.timesheets.domain.timetrack;

import com.google.common.collect.Lists;
import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.commons.jwt.SecurityUtils;
import com.mw.timesheets.commons.util.DateUtils;
import com.mw.timesheets.domain.person.PersonRepository;
import com.mw.timesheets.domain.timetrack.model.BasicTimerDataDTO;
import com.mw.timesheets.domain.timetrack.model.HistoryWithTotalTimeDTO;
import com.mw.timesheets.domain.timetrack.model.TimeTrackerHistoryDTO;
import com.mw.timesheets.domain.timetrack.model.TrackedDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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

    @Override
    public void startTracking(BasicTimerDataDTO timeTrackerData) {
        if (timeTrackRepository.existsByPersonId(securityUtils.getPersonByEmail().getId())) {
            throw new CustomErrorException("stop current time tracker", HttpStatus.BAD_REQUEST);
        }

        var startTimer = timeTrackerMapper.toEntity(timeTrackerData);
        startTimer.setPerson(securityUtils.getPersonByEmail());
        startTimer.setStarted(getSystemTime().toLocalTime());
        startTimer.setActivityDate(getSystemTime().toLocalDate().plusDays(1));
        timeTrackRepository.save(startTimer);
    }

    @Override
    public void stopTrackingTime() {
        stopTrackingTime(securityUtils.getPersonByEmail().getId());
    }

    @Override
    public boolean isStarted() {
        return timeTrackRepository.existsByPersonId(securityUtils.getPersonByEmail().getId());
    }

    @Override
    public void stopTrackingTime(Long id) {
        if (!timeTrackRepository.existsByPersonId(id)) {
            throw new CustomErrorException("no tracker to stop", HttpStatus.BAD_REQUEST);
        }
        var tracker = timeTrackRepository.findByPersonUserEmail(securityUtils.getEmail());
        var person = securityUtils.getPersonByEmail();
        var history = timeTrackerMapper.timeTrackerToHistoryEntity(tracker);
        history.setPerson(person);
        history.setEnded(getSystemTime().toLocalTime().plusHours(1));
        history.setActivityDate(history.getActivityDate().plusDays(1));
        historyRepository.save(history);
        timeTrackRepository.deleteById(tracker.getId());
    }

    @Override
    public HistoryWithTotalTimeDTO getHistoryOfGivenUser(Long personId, LocalDate from, LocalDate to, Predicate<HistoryEntity> predicate) {
        var person = personRepository.findById(personId).orElseThrow(() -> new CustomErrorException("person does not exist", HttpStatus.BAD_REQUEST));
        var timer = timeTrackRepository.findByPersonUserEmail(person.getUser().getEmail());
        var timerToHistory = timeTrackerMapper.timeTrackerToHistoryEntity(timer);
        var historyFromPerson = person.getHistory();

        if (timerToHistory != null) {
            timerToHistory.setEnded(DateUtils.getSystemTime().toLocalTime());
            historyFromPerson.add(timerToHistory);
        }

        var historyMap = historyFromPerson.stream()
                .filter(history -> history.getActivityDate().isBefore(to.plusDays(1)) && history.getActivityDate().isAfter(from.minusDays(1)))
                .filter(predicate)
                .map(historyMapper::toDto)
                .peek(history -> history.setTime(getTimeDiffInMinutes(history.getWorkFrom(), history.getWorkTo())))
                .collect(Collectors.groupingBy(TrackedDataDTO::getActivityDate));

        var historyList = mapToTimeTrackerHistoryDto(historyMap);
        return HistoryWithTotalTimeDTO.builder()
                .historyDTOs(historyList)
                .time(historyList.stream()
                        .map(TimeTrackerHistoryDTO::getTime)
                        .reduce(0L, Long::sum))
                .build();
    }

    @Override
    public HistoryWithTotalTimeDTO getHistoryOfUser() {
        var monday = getSystemTime().toLocalDate().with(DayOfWeek.MONDAY);
        var sunday = getSystemTime().toLocalDate().with(DayOfWeek.SUNDAY);
        return getHistoryOfGivenUser(securityUtils.getPersonByEmail().getId(), monday, sunday, historyEntity -> true);
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
