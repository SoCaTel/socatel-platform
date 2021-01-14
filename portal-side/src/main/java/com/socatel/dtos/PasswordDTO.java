package com.socatel.dtos;

import javax.validation.constraints.Pattern;

public class PasswordDTO {

    @Pattern(regexp = "(.{8,64})", message = "{user_password.length}")
    private String password;
    private String matchingPassword;

    public PasswordDTO() {}

    public boolean passwordsMatches() {
        return password.equals(matchingPassword);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }
}
