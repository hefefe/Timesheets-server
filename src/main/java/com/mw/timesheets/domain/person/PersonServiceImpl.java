package com.mw.timesheets.domain.person;

import com.google.common.collect.Lists;
import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.commons.jwt.SecurityUtils;
import com.mw.timesheets.commons.util.PasswordUtil;
import com.mw.timesheets.domain.person.model.PersonDTO;
import com.mw.timesheets.domain.person.type.Experience;
import com.mw.timesheets.domain.person.type.Position;
import com.mw.timesheets.domain.person.type.Roles;
import com.mw.timesheets.domain.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mw.timesheets.commons.util.DateUtils.getSystemTime;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final PersonMapper personMapper;
    private final SecurityUtils securityUtils;
    private final ProjectRepository projectRepository;

    @Override
    public List<PersonDTO> getAllUsers() {
        var personList = personRepository.findByDeletedFalse();
        return personMapper.toDtos(personList);
    }

    @Override
    public PersonDTO saveUser(PersonDTO singularPerson) {
        if (singularPerson.getId() != null) {
            var personFromDatabase = personRepository.findById(singularPerson.getId());
            if (personFromDatabase.isPresent()) {
                if (personRepository.existsByUserEmailAndIdNotLike(singularPerson.getUser().getEmail(), singularPerson.getId()))
                    throw new CustomErrorException("email should be unique", HttpStatus.BAD_REQUEST);
                var unwrappedPerson = personFromDatabase.get();
                personMapper.updateEntity(singularPerson, unwrappedPerson);
                unwrappedPerson.getUser().setEmail(singularPerson.getUser().getEmail());
                unwrappedPerson.getUser().setRole(singularPerson.getPosition().getRole());
                PersonEntity savedPerson = personRepository.save(unwrappedPerson);

                var user = savedPerson.getUser();
                user.setPerson(savedPerson);
                userRepository.save(user);

                return personMapper.toDto(savedPerson);
            }
        }
        if (personRepository.existsByUserEmailAndIdNotLike(singularPerson.getUser().getEmail(), 0L))
            throw new CustomErrorException("email should be unique", HttpStatus.BAD_REQUEST);
        var personEntity = personMapper.toEntity(singularPerson);
        personEntity.getUser().setTempPassword(PasswordUtil.generateTempPassword());
        personEntity.getUser().setRole(personEntity.getPosition().getRole());
        PersonEntity savedPerson = personRepository.save(personEntity);

        var user = savedPerson.getUser();
        user.setPerson(savedPerson);
        userRepository.save(user);

        return personMapper.toDto(savedPerson);
    }

    @Override
    public void deleteUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty() || ids.contains(0L)) {
            throw new CustomErrorException("no given id", HttpStatus.BAD_REQUEST);
        }
        var persons = personRepository.findAllById(ids).stream()
                .peek(person -> person.setDeleted(true))
                .peek(person -> person.setDeletedTime(getSystemTime()))
                .peek(person -> person.setUser(null))
                .collect(Collectors.toList());
        personRepository.saveAll(persons);

    }

    @Override
    public List<PersonEntity> getUsersByIds(List<Long> ids) {
        return personRepository.findAllById(ids);
    }

    @Override
    public List<Experience> getExperience() {
        return Arrays.stream(Experience.values()).collect(Collectors.toList());
    }

    @Override
    public List<Position> getJobPosition() {
        return Arrays.stream(Position.values()).collect(Collectors.toList());
    }

    @Override
    @SneakyThrows
    public PersonDTO savePersonPhoto(Long personId, MultipartFile photo) {
        var person = personRepository.findById(personId).orElseThrow(() -> new CustomErrorException("person does not exist", HttpStatus.BAD_REQUEST));
        person.setPhoto(photo.getBytes());
        personRepository.save(person);
        return personMapper.toDto(person);
    }

    @Override
    public PersonDTO getLoggedInUSer() {
        return personMapper.toDto(securityUtils.getPersonByEmail());
    }

    @Override
    public List<PersonDTO> resetPassword(List<Long> ids) {
        var users = personRepository.findAllById(ids).stream()
                .map(PersonEntity::getUser)
                .peek(user -> user.setPassword(null))
                .peek(user -> user.setTempPassword(PasswordUtil.generateTempPassword()))
                .collect(Collectors.toList());
        userRepository.saveAll(users);
        return personMapper.toDtos(personRepository.findAllById(ids));
    }

    @Override
    public List<PersonDTO> getTeamLeadersAndManagers() {
        return personRepository.findAll().stream()
                .filter(person -> person.getPosition().getRole() != Roles.ROLE_USER && !person.isDeleted())
                .map(personMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonDTO> getEmployeesInProject(Long projectId) {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new CustomErrorException("Project does not exist", HttpStatus.BAD_REQUEST));
        return personMapper.toDtos(Lists.newArrayList(project.getPersonsInProject()));
    }


}
