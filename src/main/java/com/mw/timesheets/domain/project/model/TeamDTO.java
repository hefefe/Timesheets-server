package com.mw.timesheets.domain.project.model;

import com.mw.timesheets.domain.person.model.PersonDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
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
