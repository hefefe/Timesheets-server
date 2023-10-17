package com.mw.timesheets.domain.person.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
public class BasicPersonDataDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private byte[] photo;
}
