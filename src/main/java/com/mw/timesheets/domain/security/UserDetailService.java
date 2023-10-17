package com.mw.timesheets.domain.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailService {

    UserDetails loadUserByUserName(String email);
}
