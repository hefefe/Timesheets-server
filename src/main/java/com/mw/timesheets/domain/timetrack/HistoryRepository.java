package com.mw.timesheets.domain.timetrack;

import com.mw.timesheets.domain.project.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {

    @Query(value = "select SUM(h.ended - h.started)/10000*60 + (SUM(h.ended - h.started) - SUM(h.ended - h.started)/10000*10000)/100 " +
            "from history h, project p " +
            "where h.project_key = :projectKey " +
            "and h.activityDate > :before " +
            "and h.activityDate < :after", nativeQuery = true)
    Long getTimeSpentBetweenDates(String projectKey, LocalDate before, LocalDate after);
}
