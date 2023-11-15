package com.mw.timesheets.domain.security;

import com.google.common.collect.Sets;
import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.person.PersonRepository;
import com.mw.timesheets.domain.person.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailService{
    private final PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUserName(String email) {
        UserEntity user = personRepository.findByUser_Email(email).orElseThrow(() -> new CustomErrorException("person not found", HttpStatus.NOT_FOUND)).getUser();
        return new User(user.getEmail(), user.getPassword(), getAuthoritiesFromUser(user));
    }

    private Set<SimpleGrantedAuthority> getAuthoritiesFromUser(UserEntity user) {
        return Sets.newHashSet(new SimpleGrantedAuthority(user.getRole().toString()));
    }
}
