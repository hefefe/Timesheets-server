package com.mw.timesheets.domain.task;

import com.mw.timesheets.commons.CommonEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TaskTypeEntity extends CommonEntity {

    private String name;

    private byte[] photo;
}
