package com.mw.timesheets.domain.timetrack;

import com.google.common.collect.Lists;
import com.mw.timesheets.commons.CustomErrorException;
import com.mw.timesheets.commons.jwt.SecurityUtils;
import com.mw.timesheets.domain.person.PersonRepository;
import com.mw.timesheets.domain.timetrack.model.BasicTimerDataDTO;
import com.mw.timesheets.domain.timetrack.model.HistoryWithTotalTimeDTO;
import com.mw.timesheets.domain.timetrack.model.TimeTrackerHistoryDTO;
import com.mw.timesheets.domain.timetrack.model.TrackedDataDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeTrackerServiceImpl implements TimeTrackService{

    private final TimeTrackRepository timeTrackRepository;
    private final TimeTrackerMapper timeTrackerMapper;
    private final HistoryRepository historyRepository;
    private final TimerHistoryMapper historyMapper;
    private final SecurityUtils securityUtils;
    private final PersonRepository personRepository;

    @Override
    public void startTracking(BasicTimerDataDTO timeTrackerData) {
        if(timeTrackRepository.existsByPersonId(securityUtils.getPersonByEmail().getId())){
            throw new CustomErrorException("stop current time tracker", HttpStatus.BAD_REQUEST);
        }

        var startTimer = timeTrackerMapper.toEntity(timeTrackerData);
        startTimer.setPerson(securityUtils.getPersonByEmail());
        startTimer.setStarted(LocalTime.now());
        startTimer.setActivityDate(LocalDate.now());
        timeTrackRepository.save(startTimer);
    }

    @Override
    @Transactional
    public void stopTrackingTime() {
        if(!timeTrackRepository.existsByPersonId(securityUtils.getPersonByEmail().getId())){
            throw new CustomErrorException("no tracker to stop", HttpStatus.BAD_REQUEST);
        }
        var tracker = timeTrackRepository.findByPersonUserEmail(securityUtils.getPersonByEmail().getUser().getEmail());
        var history = timeTrackerMapper.timeTrackerToHistoryEntity(tracker);
        history.setEnded(LocalTime.now());
        historyRepository.save(history);
        timeTrackRepository.deleteById(tracker.getId());
    }

    @Override
    public HistoryWithTotalTimeDTO getHistoryOfGivenUser(Long personId, LocalDate from, LocalDate to) {
        var person = personRepository.findById(personId).orElseThrow(() -> new CustomErrorException("person does not exist", HttpStatus.BAD_REQUEST));

        var historyMap = person.getHistory().stream()
                .filter(history -> history.getActivityDate().isAfter(to) && history.getActivityDate().isBefore(from))
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
        //TODO: w przypadku problemu zmienić implementacje
        var monday = LocalDate.now().with(DayOfWeek.MONDAY);
        var sunday = LocalDate.now().with(DayOfWeek.SUNDAY);
        return getHistoryOfGivenUser(securityUtils.getPersonByEmail().getId(), monday, sunday);
    }

    private List<TimeTrackerHistoryDTO> mapToTimeTrackerHistoryDto(Map<LocalDate, List<TrackedDataDTO>> groupedHistory){
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

    private Long getTimeDiffInMinutes(LocalTime from, LocalTime to){
        return ChronoUnit.MINUTES.between(from, to);
    }
}
