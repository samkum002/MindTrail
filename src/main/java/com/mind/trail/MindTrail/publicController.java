package com.mind.trail.MindTrail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/public")
public class publicController {
    
    @Autowired
    private userService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody userDTO user) {
        return userService.registerUser(user);
    }


}
