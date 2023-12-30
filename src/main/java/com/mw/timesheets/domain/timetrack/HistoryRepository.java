package com.mw.timesheets.domain.timetrack;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {

    @Query(value = "select SUM(h.ended - h.started)/10000*60 + (SUM(h.ended - h.started) - SUM(h.ended - h.started)/10000*10000)/100 as timeSpent " +
            "from history h " +
            "left join task t on h.task_id = t.id " +
            "where t.project_id = :projectId " +
            "and h.activity_date >= :from " +
            "and h.activity_date <= :to", nativeQuery = true)
    Long getTimeSpentBetweenDates(Long projectId, LocalDate from, LocalDate to);
}
