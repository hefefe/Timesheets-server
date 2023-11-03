package com.mw.timesheets.domain.project;

import com.mw.timesheets.domain.person.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    List<ProjectEntity> findByLead(PersonEntity person);
    @Query(value = "SELECT DISTINCT p.* " +
            "FROM project p " +
            "JOIN team_project tp ON p.id = tp.project_id " +
            "JOIN team t ON t.id = tp.team_id " +
            "JOIN person_team pt ON pt.team_id = t.id " +
            "JOIN person u ON pt.person_id = u.id  " +
            "WHERE u.id=:personId", nativeQuery = true)
    List<ProjectEntity> findProjectByPersonId(@Param("personId")Long id);

    List<ProjectEntity> findByEndOfSprintBefore(LocalDateTime dateTime);
}
