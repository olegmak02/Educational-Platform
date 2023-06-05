package com.oleg.educationalplatform.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(
            @RequestBody RegisterRequest request
    ) throws Exception {
        return ResponseEntity.ok(service.registerUser(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<HashMap<String, String>> updateUserCredentials(@RequestBody ChangePasswordRequest request) {
        HashMap<String, String> res = service.updateUserPassword(request);
        if (res == null) {
            res = new HashMap<>();
            res.put("message", "Пароль успішно змінено");
            return ResponseEntity.status(200).body(res);
        } else {
            return ResponseEntity.status(400).body(res);
        }
    }
}
