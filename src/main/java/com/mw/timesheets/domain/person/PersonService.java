package com.mw.timesheets.domain.person;

import com.mw.timesheets.domain.person.model.PersonDTO;
import com.mw.timesheets.domain.person.type.Experience;
import com.mw.timesheets.domain.person.type.Position;

import java.util.List;

public interface PersonService {

    List<PersonDTO> getAllUsers();

    List<PersonDTO> saveUsers(List<PersonDTO> persons);

    void deleteUsers(List<Long> ids);

    List<PersonEntity> getUsersByIds(List<Long> ids);

    List<Experience> getExperience();

    List<Position> getJobPosition();

    List<String> getGenders();
}
