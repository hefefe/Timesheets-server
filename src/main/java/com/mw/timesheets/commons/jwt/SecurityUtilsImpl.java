package com.mw.timesheets.commons.jwt;

import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.person.PersonRepository;
import com.mw.timesheets.domain.person.type.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtilsImpl implements SecurityUtils{

    private final PersonRepository personRepository;

    @Override
    public PersonEntity getPersonByEmail() {
        return personRepository.findByUser_Email(getEmail()).orElseThrow(() -> new CustomErrorException("user not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public String getEmail() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .map(String.class::cast)
                .orElse(null);
    }

    @Override
    public Roles getRole() {
        return getPersonByEmail().getUser().getRole();
    }
}
