package com.mw.timesheets.domain.timetrack;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.person.PersonEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TimeTrackEntity extends CommonEntity {

    private String description;

    private String projectKey;

    private String taskName;

    private LocalDate activityDate;

    private LocalTime started;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private PersonEntity person;

    @ManyToOne
    @JoinColumn(name = "tag_id", referencedColumnName = "id")
    private TagEntity tag;
}
