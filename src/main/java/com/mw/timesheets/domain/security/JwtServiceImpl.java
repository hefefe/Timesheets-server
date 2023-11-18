package com.mw.timesheets.domain.security;


import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.commons.properties.JwtProperties;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.person.PersonRepository;
import com.mw.timesheets.domain.security.model.AccessTokenDTO;
import com.mw.timesheets.domain.security.model.AuthenticationDTO;
import com.mw.timesheets.domain.security.model.CheckTokenDTO;
import com.mw.timesheets.domain.security.model.TokenDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    public static final String ROLE_CLAIMS = "roles";
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final JwtProperties jwtProperties;
    private final PersonRepository personRepository;

    @Override
    public AuthenticationDTO refreshToken(TokenDTO tokenDTO) {
        blockTokens(tokenDTO);
        PersonEntity userEntity = getUserByAccessToken(tokenDTO);
        return buildAuthenticationToken(userEntity, false);
    }

    @Override
    public CheckTokenDTO checkToken(AccessTokenDTO accessTokenDTO) {
        try {
            String userName = getEmailFromToken(accessTokenDTO.getAccessToken(), jwtProperties.getAccessTokenSecret());
            if (!tokenBlacklistRepository.existsById(accessTokenDTO.getAccessToken())) {
                return CheckTokenDTO.builder().isValid(true).build();
            }
        } catch (Exception ex) {
        }
        return CheckTokenDTO.builder().isValid(Boolean.FALSE).build();
    }

    @Override
    public String getUsernameFromAccessToken(String token) {
        return getEmailFromToken(token, jwtProperties.getAccessTokenSecret());
    }

    @Override
    public void blockTokens(TokenDTO tokenDTO) {
        blockToken(tokenDTO.getAccessToken(), jwtProperties.getAccessTokenSecret());
        blockToken(tokenDTO.getRefreshToken(), jwtProperties.getRefreshTokenSecret());
    }

    @Override
    public AuthenticationDTO buildAuthenticationToken(PersonEntity userEntity, boolean requiredToChangePassword) {
        return AuthenticationDTO.builder()
                .accessToken(generateAccessToken(userEntity))
                .accessTokenValidityTime(jwtProperties.getAccessTokenValidityTime())
                .refreshToken(generateRefreshToken(userEntity))
                .refreshTokenValidityTime(jwtProperties.getRefreshTokenValidityTime())
                .requiredToChangePassword(requiredToChangePassword)
                .build();
    }

    private PersonEntity getUserByAccessToken(TokenDTO tokenDTO) {
        String accessTokenUserName = getEmailFromToken(tokenDTO.getAccessToken(), jwtProperties.getAccessTokenSecret());
        String refreshTokenUserName = getEmailFromToken(tokenDTO.getRefreshToken(), jwtProperties.getRefreshTokenSecret());
        if (!StringUtils.pathEquals(accessTokenUserName, refreshTokenUserName)) {
            throw new CustomErrorException("token is invalid", HttpStatus.BAD_REQUEST);
        }
        return personRepository.findByUser_Email(accessTokenUserName).orElseThrow(() -> new CustomErrorException("person not found", HttpStatus.NOT_FOUND));
    }

    private void blockToken(String token, String secret) {
        if (!StringUtils.isEmpty(token)) {
            try {
                Jws<Claims> tokenClaims = Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(token);
                TokenBlacklistEntity tokenToBlock = TokenBlacklistEntity.builder()
                        .token(token)
                        .expTimestamp(Timestamp.from(tokenClaims.getBody().getExpiration().toInstant()))
                        .build();
                tokenBlacklistRepository.save(tokenToBlock);
            } catch (Exception e) {
                log.debug("invalid token" + token);
                throw new CustomErrorException("invalid token", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private String getEmailFromToken(String token, String secret) {
        Jws<Claims> tokenClaims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token);
        return tokenClaims.getBody().getSubject();
    }

    private String generateAccessToken(PersonEntity personEntity) {
        Date now = new Date();
        Date expirationDate = Date.from(now.toInstant().plus(jwtProperties.getAccessTokenValidityTime(), ChronoUnit.MILLIS));
        Map<String, Object> claims = new HashMap<>();
        claims.put(ROLE_CLAIMS, personEntity.getUser().getRole().toString());
        return Jwts.builder()
                .addClaims(claims)
                .setIssuer(jwtProperties.getAppName())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .setSubject(personEntity.getUser().getEmail())
                .signWith(HS512, jwtProperties.getAccessTokenSecret())
                .compact();
    }

    private String generateRefreshToken(PersonEntity personEntity) {
        Date now = new Date();
        Date expirationDate = Date.from(now.toInstant().plus(jwtProperties.getRefreshTokenValidityTime(), ChronoUnit.MILLIS));
        return Jwts.builder()
                .setIssuer(jwtProperties.getAppName())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .setSubject(personEntity.getUser().getEmail())
                .signWith(HS512, jwtProperties.getRefreshTokenSecret())
                .compact();
    }


}
