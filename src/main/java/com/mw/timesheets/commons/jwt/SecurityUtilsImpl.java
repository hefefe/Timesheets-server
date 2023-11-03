package com.mw.timesheets.commons.jwt;

import com.mw.timesheets.commons.CustomErrorException;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.person.PersonRepository;
import com.mw.timesheets.domain.person.type.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtilsImpl implements SecurityUtils{

    private final PersonRepository personRepository;

    @Override
    public PersonEntity getPersonByEmail() {
        return personRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new CustomErrorException("no such person with given email", HttpStatus.NOT_FOUND));
    }

    @Override
    public String getEmail() {
        return personRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new CustomErrorException("no such person with given email", HttpStatus.NOT_FOUND))
                .getUser()
                .getEmail();
    }

    @Override
    public Position getPosition() {
        return Position.BACKEND;
    }
}
