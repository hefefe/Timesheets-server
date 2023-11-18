package com.mw.timesheets.domain.team.model;

import com.mw.timesheets.domain.person.model.PersonDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamDTO {

    private Long id;

    private String name;

    private Set<PersonDTO> persons;
}
