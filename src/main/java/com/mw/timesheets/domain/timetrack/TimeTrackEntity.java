package com.mw.timesheets.domain.timetrack;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.person.PersonEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TIME_TRACK")
public class TimeTrackEntity extends CommonEntity {

    private String description;

    private String projectKey;

    private String taskName;

    private LocalDate activityDate;

    private LocalTime started;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private PersonEntity person;
}
