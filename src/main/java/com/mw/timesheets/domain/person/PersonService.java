package com.mw.timesheets.domain.person;

import com.mw.timesheets.domain.person.model.PersonDTO;
import com.mw.timesheets.domain.person.type.Experience;
import com.mw.timesheets.domain.person.type.Position;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PersonService {

    List<PersonDTO> getAllUsers();

    PersonDTO saveUser(PersonDTO persons);

    void deleteUsers(List<Long> ids);

    List<PersonEntity> getUsersByIds(List<Long> ids);

    List<Experience> getExperience();

    List<Position> getJobPosition();

    PersonDTO savePersonPhoto(Long personId, MultipartFile photo);

    PersonDTO getLoggedInUSer();

    List<PersonDTO> resetPassword(List<Long> ids);

    List<PersonDTO> getTeamLeadersAndManagers();

    List<PersonDTO> getEmployeesInProject(Long projectId);
}
