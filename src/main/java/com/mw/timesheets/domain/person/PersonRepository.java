package com.mw.timesheets.domain.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    List<PersonEntity> findByDeletedFalse();

    Optional<PersonEntity> findByUser_Email(String email);

    boolean existsByUserEmail(String email);
}
