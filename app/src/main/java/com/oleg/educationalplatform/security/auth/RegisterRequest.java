package com.oleg.educationalplatform.security.auth;

import com.oleg.educationalplatform.security.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotNull
    private String firstname;
    @NotNull
    private String lastname;
    @NotNull
    private String username;
    @NotNull
    private String password;
    private String groupName;
    @NotNull
    private Role role;
}
