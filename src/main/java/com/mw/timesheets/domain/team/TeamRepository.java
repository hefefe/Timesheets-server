package com.mw.timesheets.domain.team;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {

    List<TeamEntity> findByDeletedFalse();

    List<TeamEntity> findByNameContainingAndDeletedFalse(String name);
}
