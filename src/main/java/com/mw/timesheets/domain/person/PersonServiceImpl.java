package com.mw.timesheets.domain.person;

import com.mw.timesheets.commons.CustomErrorException;
import com.mw.timesheets.domain.person.model.PersonDTO;
import com.mw.timesheets.domain.person.type.Experience;
import com.mw.timesheets.domain.person.type.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
        List<PersonEntity> personList = personRepository.saveAll(personMapper.toEntities(persons));
        return personMapper.toDtos(personList);
    }

    @Override
    public void deleteUsers(List<Long> ids){
        if (ids == null){
            throw new CustomErrorException("no given id", HttpStatus.BAD_REQUEST);
        }
        personRepository.deleteAllById(ids);
    }

    @Override
    public List<PersonEntity> getUsersByIds(List<Long> ids) {
        return personRepository.findAllById(ids);
    }

    @Override
    public List<String> getExperience() {
        return Arrays.stream(Experience.values()).map(Enum::toString).collect(Collectors.toList());
    }

    @Override
    public List<String> getJobPosition() {
        return Arrays.stream(Position.values()).map(Enum::toString).collect(Collectors.toList());
    }

    @Override
    public List<String> getGenders() {
        return personRepository.findDistinctSex();
    }


}
