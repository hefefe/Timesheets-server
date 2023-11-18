package com.mw.timesheets.domain.timetrack;

import com.mw.timesheets.domain.timetrack.model.BasicTimerDataDTO;
import com.mw.timesheets.domain.timetrack.model.HistoryWithTotalTimeDTO;

import java.time.LocalDate;

public interface TimeTrackService {

    void startTracking(BasicTimerDataDTO timeTrackerData);

    void stopTrackingTime();

    void stopTrackingTime(Long id);

    HistoryWithTotalTimeDTO getHistoryOfGivenUser(Long PersonId, LocalDate from, LocalDate to);

    HistoryWithTotalTimeDTO getHistoryOfUser();
}
