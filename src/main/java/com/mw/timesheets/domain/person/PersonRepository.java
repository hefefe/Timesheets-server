package com.mw.timesheets.domain.person;

import com.mw.timesheets.domain.person.type.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    List<PersonEntity> findByDeletedFalse();

    Optional<PersonEntity> findByUser_Email(String email);

    boolean existsByUserEmailAndIdNotLike(String email, Long id);

    @Query(value = "select DISTINCT p.* " +
            "from person p " +
            "inner join user u on p.user_id = u.id " +
            "left join person_project pp on p.id = pp.person_id " +
            "right join (SELECT proj.* from project proj where proj.person_id = :personId) pr on pp.project_id = pr.id " +
            "where u.role like('ROLE_USER') and p.deleted = false;", nativeQuery = true)
    List<PersonEntity> findByLeader(@Param("personId") Long id);
}
