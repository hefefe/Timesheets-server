package com.mw.timesheets.domain.project;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowRepository extends JpaRepository<WorkflowEntity, Long> {
}
