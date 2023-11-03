package com.mw.timesheets.domain.person;

import com.mw.timesheets.domain.person.model.PersonDTO;

import java.util.List;

public interface PersonService {

    List<PersonDTO> getAllUsers();

    List<PersonDTO> saveUsers(List<PersonDTO> persons);

    void deleteUsers(List<Long> ids);

    List<PersonEntity> getUsersByIds(List<Long> ids);

    List<String> getExperience();

    List<String> getJobPosition();

    List<String> getGenders();
}
