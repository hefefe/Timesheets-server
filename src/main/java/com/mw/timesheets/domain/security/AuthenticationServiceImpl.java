package com.mw.timesheets.domain.security;

import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.commons.util.PasswordUtil;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.person.PersonRepository;
import com.mw.timesheets.domain.security.model.AuthenticationDTO;
import com.mw.timesheets.domain.security.model.ChangePasswordDTO;
import com.mw.timesheets.domain.security.model.LoginDTO;
import com.mw.timesheets.domain.security.model.TokenDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    //TODO: validator unique email
    @Override
    public AuthenticationDTO auth(LoginDTO loginDTO) {
        PersonEntity person = personRepository.findByUser_Email(loginDTO.getEmail()).orElseThrow(() -> new CustomErrorException("wrong email", HttpStatus.BAD_REQUEST));

        if (loginDTO.getPassword() == null) throw new CustomErrorException("wrong password", HttpStatus.BAD_REQUEST);

        if (loginDTO.getPassword().equals(person.getUser().getTempPassword())) {
            return jwtService.buildAuthenticationToken(person, true);
        }

        if (passwordEncoder.matches(loginDTO.getPassword(), person.getUser().getPassword()) && loginDTO.getPassword() != null) {
            return jwtService.buildAuthenticationToken(person, false);
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
    public void setUserPassword(ChangePasswordDTO changePassword) {
        PersonEntity person = personRepository.findByUser_Email(changePassword.getEmail()).orElseThrow(() -> new CustomErrorException("wrong email", HttpStatus.BAD_REQUEST));
        if (changePassword.getConfirmPassword() != null && changePassword.getPassword() != null) {
            if (!changePassword.getConfirmPassword().equals(changePassword.getPassword()))
                throw new CustomErrorException("password missmatch", HttpStatus.BAD_REQUEST);
            person.getUser().setTempPassword(null);
            person.getUser().setPassword(passwordEncoder.encode(changePassword.getPassword()));
        }
        personRepository.save(person);
    }

//    @Override
//    public void register(RegisterUserDTO registerUserDTO) {
//        userService.findOptionalUserName(registerUserDTO.getUsername())
//                .ifPresent(user -> throwUserIsPresent(user.getUsername()));
//        userService.createUser(registerUserDTO.getUsername(), registerUserDTO.getEmail(), registerUserDTO.getPassword());
//    }
//
//    private void throwUserIsPresent(String username){
//        throw new UserAlreadyExistsException(username);
//    }
}
