package com.mw.timesheets.domain.timetrack;

import com.mw.timesheets.domain.timetrack.model.BasicTimerDataDTO;
import com.mw.timesheets.domain.timetrack.model.HistoryWithTotalTimeDTO;
import com.mw.timesheets.domain.timetrack.model.IsStartedDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Predicate;

public interface TimeTrackService {

    void startTracking(BasicTimerDataDTO timeTrackerData);

    void stopTrackingTime();

    IsStartedDTO isStarted();

    void stopTrackingTime(Long id);

    HistoryWithTotalTimeDTO getHistoryOfGivenUser(Long PersonId, LocalDate from, LocalDate to, Predicate<HistoryEntity> predicate, boolean withStartedTime);

    HistoryWithTotalTimeDTO getHistoryOfUser();
}
