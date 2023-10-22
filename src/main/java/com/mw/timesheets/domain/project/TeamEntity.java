package com.mw.timesheets.domain.project;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.project.type.SprintDurationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TeamEntity extends CommonEntity {

    private String name;

    private boolean deleted;

    private LocalDateTime deletedTime;
}
