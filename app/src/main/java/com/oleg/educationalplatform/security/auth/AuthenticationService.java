package com.oleg.educationalplatform.security.auth;

import com.oleg.educationalplatform.security.config.JwtService;
import com.oleg.educationalplatform.security.user.Role;
import com.oleg.educationalplatform.security.user.User;
import com.oleg.educationalplatform.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse registerUser(RegisterRequest request) throws Exception {
        if (!request.getPassword().matches(".*[0-9].*") ||
                !request.getPassword().matches(".*[a-zA-Z].*") ||
                request.getPassword().length() < 8)
        {
            throw new Exception("Bad password");
        }

        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .groupName(request.getGroupName())
                .build();
        repository.save(user);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = repository.findByUsername(request.getUsername())
                .orElseThrow();

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }

    public HashMap<String, String> updateUserPassword(ChangePasswordRequest request) {
        HashMap<String, String> res = new HashMap<>();
        User currentUser = (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if (passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            if (request.getNewPassword().length() < 8 && !request.getNewPassword().matches("0-9")) {
                res.put("message", "Пароль не відповідає правилам безпеки: довше 8 символів та наявність цифр");
                return res;
            }
            currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
            repository.save(currentUser);
            return null;
        } else {
            res.put("message", "Неправильний пароль");
        }
        return res;
    }
}
