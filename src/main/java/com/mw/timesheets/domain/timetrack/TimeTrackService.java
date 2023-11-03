package com.mw.timesheets.domain.timetrack;

import com.mw.timesheets.domain.timetrack.model.BasicTimerDataDTO;
import com.mw.timesheets.domain.timetrack.model.HistoryWithTotalTimeDTO;
import com.mw.timesheets.domain.timetrack.model.TimeTrackerHistoryDTO;

import java.time.LocalDate;
import java.util.List;

public interface TimeTrackService {

    void startTracking(BasicTimerDataDTO timeTrackerData);

    void stopTrackingTime();

    HistoryWithTotalTimeDTO getHistoryOfGivenUser(Long PersonId, LocalDate from, LocalDate to);

    HistoryWithTotalTimeDTO getHistoryOfUser();
}
