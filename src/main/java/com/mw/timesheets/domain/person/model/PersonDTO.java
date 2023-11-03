package com.mw.timesheets.domain.person.model;

import com.mw.timesheets.domain.person.type.Experience;
import com.mw.timesheets.domain.person.type.Position;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonDTO {

    private Long id;

    private String firstName;

    private String middleName;

    private String lastName;

    private String sex;

    private String peselPassportNumber;

    private LocalDate dateOfBirth;

    private LocalDate dateOfEmployment;

    private byte[] photo;

    private Experience experience;

    private Position position;

    private boolean deleted;

    private LocalDateTime deletedTime;

    private AddressDTO address;

    private ContactDTO contact;

    private UserDTO user;

}
