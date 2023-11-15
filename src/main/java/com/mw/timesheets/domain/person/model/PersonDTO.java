package com.mw.timesheets.domain.person.model;

import com.mw.timesheets.domain.person.type.Experience;
import com.mw.timesheets.domain.person.type.Position;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PersonDTO extends BasicPersonDataDTO{

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
