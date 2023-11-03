package com.mw.timesheets.domain.person.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDTO {

    private Long id;

    private String streetName;

    private String city;

    private String homeNumber;

    private String zipCode;

    private CountryDTO country;
}
