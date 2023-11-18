package com.mw.timesheets.commons.jwt;

import com.mw.timesheets.domain.security.JwtService;
import com.mw.timesheets.domain.security.TokenBlacklistService;
import com.mw.timesheets.domain.security.UserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailService userDetailService;
    private final TokenBlacklistService tokenBlacklistService;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_TOKEN = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.isEmpty(authorization)
                && authorization.startsWith(BEARER_TOKEN)) {
            String token = extractBareToken(authorization);

            if (!tokenBlacklistService.isTokenInBlacklist(token)) {
                String username = jwtService.getUsernameFromAccessToken(token);
                UserDetails userDetails = userDetailService.loadUserByUserName(username);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractBareToken(String token) {
        return token.substring(7);
    }
}
