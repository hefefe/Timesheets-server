package com.mw.timesheets.domain.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {

    List<TeamEntity> findByNameContaining(String name);
}
