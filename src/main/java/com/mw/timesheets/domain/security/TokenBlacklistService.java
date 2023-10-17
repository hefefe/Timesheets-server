package com.mw.timesheets.domain.security;

public interface TokenBlacklistService {

    public boolean isTokenInBlacklist(String token);
}
