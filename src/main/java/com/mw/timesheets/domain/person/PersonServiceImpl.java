package com.mw.timesheets.domain.person;

import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.commons.jwt.SecurityUtils;
import com.mw.timesheets.commons.util.PasswordUtil;
import com.mw.timesheets.domain.person.model.PersonDTO;
import com.mw.timesheets.domain.person.type.Experience;
import com.mw.timesheets.domain.person.type.Position;
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

    @Override
    public List<PersonDTO> getAllUsers() {
        var personList = personRepository.findByDeletedFalse();
        return personMapper.toDtos(personList);
    }

    @Override
    public List<PersonDTO> saveUsers(List<PersonDTO> persons) {
        if (persons == null) {
            throw new CustomErrorException("no persons to save", HttpStatus.BAD_REQUEST);
        }
        var filterPerson = persons.stream()
                .filter(personDTO -> !personRepository.existsByUserEmail(personDTO.getUser().getEmail()))
                .collect(Collectors.toList());

        var personEntities = personMapper.toEntities(filterPerson).stream()
                .peek(this::setTempPassword)
                .peek(person -> person.getUser().setRole(person.getPosition().getRole()))
                .collect(Collectors.toList());
        List<PersonEntity> personList = personRepository.saveAll(personEntities);

        var users = personList.stream()
                .peek(person -> person.getUser().setPerson(person))
                .map(PersonEntity::getUser)
                .collect(Collectors.toSet());
        userRepository.saveAll(users);

        return personMapper.toDtos(personList);
    }

    private PersonEntity setTempPassword(PersonEntity person) {
        if (person.getUser().getPassword() != null) return person;
        person.getUser().setTempPassword(PasswordUtil.generateTempPassword());
        return person;
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
    public List<String> getGenders() {
        return personRepository.findDistinctSex();
    }

    @Override
    @SneakyThrows
    public List<PersonDTO> savePersonPhoto(Long personId, MultipartFile photo) {
        var person = personRepository.findById(personId).orElseThrow(() -> new CustomErrorException("person does not exist", HttpStatus.BAD_REQUEST));
        person.setPhoto(photo.getBytes());
        personRepository.save(person);
        return personMapper.toDtos(personRepository.findByDeletedFalse());
    }

    @Override
    public PersonDTO getLoggedInUSer() {
        return personMapper.toDto(securityUtils.getPersonByEmail());
    }


}
