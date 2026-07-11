package com.mind.trail.MindTrail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private userService userService;

    @Autowired
    userRepo userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody userDTO user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminName = auth.getName();
        userEntity adminUser = userRepository.findByUsername(adminName);
        if(adminUser == null || !adminUser.getRoles().contains("ADMIN")) {
            return new ResponseEntity<>("Acess Denied: Admins only", HttpStatus.FORBIDDEN);
        }
        return userService.registerAdmin(user);
    }


}
