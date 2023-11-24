package com.mw.timesheets.domain.timetrack;

import com.mw.timesheets.commons.IdFromToRequestDTO;
import com.mw.timesheets.domain.timetrack.model.BasicTimerDataDTO;
import com.mw.timesheets.domain.timetrack.model.HistoryWithTotalTimeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/timetrack")
public class TimeTrackController {

    private final TimeTrackService timeTrackService;

    @PostMapping
    public ResponseEntity<Void> startTracking(@RequestBody BasicTimerDataDTO timeTrackerData) {
        timeTrackService.startTracking(timeTrackerData);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Void> stopTrackingTime() {
        timeTrackService.stopTrackingTime();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("history")
    public ResponseEntity<HistoryWithTotalTimeDTO> getHistoryOfGivenUser(@RequestBody IdFromToRequestDTO idFromToRequestDTO) {
        return ResponseEntity.ok(timeTrackService.getHistoryOfGivenUser(idFromToRequestDTO.getId(), idFromToRequestDTO.getFrom(), idFromToRequestDTO.getTo(), historyEntity -> true));
    }

    @GetMapping("history")
    public ResponseEntity<HistoryWithTotalTimeDTO> getHistoryOfUser() {
        return ResponseEntity.ok(timeTrackService.getHistoryOfUser());
    }

    @GetMapping("is/started")
    public ResponseEntity<Boolean> getIsStarted() {
        return ResponseEntity.ok(timeTrackService.isStarted());
    }
}
