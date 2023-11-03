package com.mw.timesheets.domain.person.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class BasicPersonDataDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private byte[] photo;
}
