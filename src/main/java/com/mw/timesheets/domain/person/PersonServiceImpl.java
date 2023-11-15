package com.mw.timesheets.domain.person;

import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.commons.util.PasswordUtil;
import com.mw.timesheets.domain.person.model.PersonDTO;
import com.mw.timesheets.domain.person.type.Experience;
import com.mw.timesheets.domain.person.type.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService{

    //TODO: photo compressor
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    @Override
    public List<PersonDTO> getAllUsers(){
        var personList = personRepository.findByDeletedFalse();
        return personMapper.toDtos(personList);
    }

    @Override
    public List<PersonDTO> saveUsers(List<PersonDTO> persons){
        if (persons == null){
            throw new CustomErrorException("no persons to save", HttpStatus.BAD_REQUEST);
        }
        var personEntities = personMapper.toEntities(persons).stream()
                .peek(this::setTempPassword)
                .peek(person -> person.getUser().setRole(person.getPosition().getRole()))
                .collect(Collectors.toList());
        List<PersonEntity> personList = personRepository.saveAll(personEntities);
        return personMapper.toDtos(personList);
    }

    private PersonEntity setTempPassword(PersonEntity person){
        if(person.getUser().getPassword() != null) return person;
        person.getUser().setTempPassword(PasswordUtil.generateTempPassword());
        return person;
    }

    @Override
    public void deleteUsers(List<Long> ids){
        if (ids == null){
            throw new CustomErrorException("no given id", HttpStatus.BAD_REQUEST);
        }
        var persons = personRepository.findAllById(ids).stream()
                .peek(person -> person.setDeleted(true))
                .peek(person -> person.setDeletedTime(LocalDateTime.now()))
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


}
