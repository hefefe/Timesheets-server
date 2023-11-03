package com.mw.timesheets.domain.person.model;

import com.mw.timesheets.domain.person.type.Experience;
import com.mw.timesheets.domain.person.type.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchPersonDTO {
    private String firstName;

    private String middleName;

    private String lastName;

    private List<String> sex;

    private String peselPassportNumber;

    private String phoneNumber;

    private List<Experience> experience;

    private List<Position> position;

    private String streetName;

    private String city;

    private String homeNumber;

    private String zipCode;

    private List<String> countries;

    private String email;
}
