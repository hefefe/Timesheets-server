package com.mw.timesheets.commons.jwt;

import com.mw.timesheets.commons.CustomErrorException;
import com.mw.timesheets.domain.person.UserEntity;
import com.mw.timesheets.domain.person.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtilsImpl implements SecurityUtils{

    private final UserRepository userRepository;

    @Override
    public UserEntity getUserByEmail() {
        return userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new CustomErrorException("no such user with given email", HttpStatus.NOT_FOUND));
    }
}
