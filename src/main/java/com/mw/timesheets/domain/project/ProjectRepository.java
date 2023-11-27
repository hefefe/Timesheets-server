package com.mw.timesheets.domain.project;

import com.mw.timesheets.domain.person.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    List<ProjectEntity> findByPersonAndNameLike(PersonEntity person, String name);

    @Query(value = "SELECT DISTINCT p.* " +
            "FROM project p " +
            "JOIN person_project pp ON pp.project_id = p.id " +
            "JOIN person u ON pp.person_id = u.id  " +
            "WHERE u.id=:personId AND p.name LIKE ':name'", nativeQuery = true)
    List<ProjectEntity> findProjectByPersonIdAndName(@Param("personId") Long id, @Param("name") String name);

    List<ProjectEntity> findByEndOfSprintBeforeAndDeletedFalse(LocalDateTime dateTime);

}
