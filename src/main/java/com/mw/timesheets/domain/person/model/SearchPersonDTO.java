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

    private String lastName;

    private List<Experience> experience;

    private List<Position> position;
}
