package com.mw.timesheets.domain.timetrack;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimeTrackRepository extends JpaRepository<TimeTrackEntity, Long> {

    boolean existsByPersonId(Long personId);

    Optional<TimeTrackEntity> findByPersonUserEmail(String email);
}
