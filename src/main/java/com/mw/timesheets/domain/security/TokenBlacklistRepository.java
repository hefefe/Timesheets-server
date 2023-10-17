package com.mw.timesheets.domain.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklistEntity, String> {
    boolean existsByToken(String token);
}
