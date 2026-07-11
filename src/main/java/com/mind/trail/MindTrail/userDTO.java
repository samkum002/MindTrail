package com.mind.trail.MindTrail;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class userDTO {
    
    @NotBlank(message="Username cannot be blank")
    private String username;
    @NotBlank(message="Email cannot be blank")
    @Pattern(regexp="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message="Invalid email format")
    private String email;
    @NotBlank(message="Password cannot be blank")
    private String password;
    private List<String> Roles = new ArrayList<>();
    
}
