package com.mw.timesheets.domain.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    public boolean isTokenInBlacklist(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }
}
