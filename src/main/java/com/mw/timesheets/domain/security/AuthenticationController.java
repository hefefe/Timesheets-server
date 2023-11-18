package com.mw.timesheets.domain.security;


import com.mw.timesheets.domain.security.model.AuthenticationDTO;
import com.mw.timesheets.domain.security.model.ChangePasswordDTO;
import com.mw.timesheets.domain.security.model.LoginDTO;
import com.mw.timesheets.domain.security.model.TokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<AuthenticationDTO> authenticate(@RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(authenticationService.auth(loginDTO));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody TokenDTO tokenDTO) {
        authenticationService.logout(tokenDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resetPasswordForUser")
    public ResponseEntity<Void> logout(@RequestParam Long id) {
        authenticationService.resetUserPassword(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/setPassword")
    public ResponseEntity<Void> logout(@RequestBody ChangePasswordDTO changePasswordDTO) {
        authenticationService.setUserPassword(changePasswordDTO);
        return ResponseEntity.ok().build();
    }
}
