package org.launchcode.javawebdevtechjobsauthentication.models.DTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LoginFormDTO {

    @NotNull
    @NotBlank(message = "Username is required")
    @Size(min=3, max=20, message="Invalid username. Must be between 3 and 20 characters")
    private String username;

    @NotNull
    @NotBlank(message = "Password is required")
    @Size(min=5, max=15, message="Password must be between 5 and 15 characters long")
    private String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
