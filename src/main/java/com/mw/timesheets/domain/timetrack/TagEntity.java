package com.mw.timesheets.domain.timetrack;

import com.mw.timesheets.commons.CommonEntity;
import jakarta.persistence.Column;
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
public class TagEntity extends CommonEntity {

    private String name;

    @Column(columnDefinition="BLOB")
    private byte[] photo;
}
