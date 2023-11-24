package com.mw.timesheets.domain.security;

import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.commons.jwt.SecurityUtils;
import com.mw.timesheets.commons.util.PasswordUtil;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.person.PersonRepository;
import com.mw.timesheets.domain.person.UserEntity;
import com.mw.timesheets.domain.person.UserRepository;
import com.mw.timesheets.domain.security.model.AuthenticationDTO;
import com.mw.timesheets.domain.security.model.ChangePasswordDTO;
import com.mw.timesheets.domain.security.model.LoginDTO;
import com.mw.timesheets.domain.security.model.TokenDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecurityUtils securityUtils;
    private final PersonRepository personRepository;

    @Override
    public AuthenticationDTO auth(LoginDTO loginDTO) {
        UserEntity user = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(() -> new CustomErrorException("wrong email", HttpStatus.BAD_REQUEST));

        if (loginDTO.getPassword() == null) throw new CustomErrorException("wrong password", HttpStatus.BAD_REQUEST);

        if (loginDTO.getPassword().equals(user.getTempPassword())) {
            return jwtService.buildAuthenticationToken(user, true);
        }

        if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword()) && loginDTO.getPassword() != null) {
            return jwtService.buildAuthenticationToken(user, false);
        }

        throw new CustomErrorException("wrong password", HttpStatus.BAD_REQUEST);
    }

    @Override
    public void logout(TokenDTO tokenDTO) {
        try {
            jwtService.blockTokens(tokenDTO);
        } catch (RuntimeException e) {
            log.debug("invalid token");
        }
    }

    @Override
    public void resetUserPassword(Long id) {
        var person = personRepository.findById(id).orElseThrow(() -> new CustomErrorException("person not found", HttpStatus.BAD_REQUEST));
        person.getUser().setPassword(null);
        person.getUser().setTempPassword(PasswordUtil.generateTempPassword());
        personRepository.save(person);
    }

    @Override
    public AuthenticationDTO setUserPassword(ChangePasswordDTO changePassword) {
        if (changePassword.getConfirmPassword() == null || changePassword.getPassword() == null)
            throw new CustomErrorException("password mismatch", HttpStatus.BAD_REQUEST);
        if (!changePassword.getConfirmPassword().equals(changePassword.getPassword()))
            throw new CustomErrorException("password mismatch", HttpStatus.BAD_REQUEST);

        PersonEntity person = securityUtils.getPersonByEmail();
        person.getUser().setTempPassword(null);
        person.getUser().setPassword(passwordEncoder.encode(changePassword.getPassword()));
        var savedPerson = personRepository.save(person);
        return jwtService.buildAuthenticationToken(savedPerson.getUser(), false);
    }
}
