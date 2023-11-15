package com.mw.timesheets.domain.security;

import com.mw.timesheets.domain.security.model.AccessTokenDTO;
import com.mw.timesheets.domain.security.model.AuthenticationDTO;
import com.mw.timesheets.domain.security.model.CheckTokenDTO;
import com.mw.timesheets.domain.security.model.TokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {

    private final JwtService jwtService;

    @PostMapping("/refreshToken")
    public ResponseEntity<AuthenticationDTO> refreshToken(@RequestBody TokenDTO tokenDTO) {
        return ResponseEntity.ok(jwtService.refreshToken(tokenDTO));
    }

    @PostMapping("/checkToken")
    public ResponseEntity<CheckTokenDTO> checkToken(@RequestBody AccessTokenDTO accessTokenDTO) {
        return ResponseEntity.ok(jwtService.checkToken(accessTokenDTO));
    }
}
