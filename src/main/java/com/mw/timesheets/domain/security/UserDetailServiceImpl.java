package com.mw.timesheets.domain.security;

import com.google.common.collect.Sets;
import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.domain.person.UserEntity;
import com.mw.timesheets.domain.person.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUserName(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new CustomErrorException("user not found", HttpStatus.NOT_FOUND));
        return new User(user.getEmail(), user.getPassword() != null ? user.getPassword() : user.getTempPassword(), getAuthoritiesFromUser(user));
    }

    private Set<SimpleGrantedAuthority> getAuthoritiesFromUser(UserEntity user) {
        return Sets.newHashSet(new SimpleGrantedAuthority(user.getRole().toString()));
    }
}
