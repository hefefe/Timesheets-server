package com.mw.timesheets.domain.task;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskTypeRepository extends JpaRepository<TaskTypeEntity, Long> {
}
