package com.mw.timesheets.domain.person.model;

import com.mw.timesheets.domain.person.type.Experience;
import com.mw.timesheets.domain.person.type.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PersonDTO extends BasicPersonDataDTO {

    private String middleName;

    private String sex;

    private LocalDate dateOfBirth;

    private LocalDate dateOfEmployment;

    private Experience experience;

    private Position position;

    private Integer workDuringWeekInHours;

    private Double hourlyPay;

    private AddressDTO address;

    private ContactDTO contact;

    private UserDTO user;

}
