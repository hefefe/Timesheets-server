package com.mw.timesheets.domain.person;

import com.mw.timesheets.commons.CommonEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "COUNTRY")
public class CountryEntity extends CommonEntity {

    private String name;
}
