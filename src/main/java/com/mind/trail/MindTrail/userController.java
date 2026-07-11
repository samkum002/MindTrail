package com.mind.trail.MindTrail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class userController {

    @Autowired
    private userRepo userRepository;

    @Autowired
    private moodService moodService;

    @Autowired
    private userService userService;

    @Autowired
    private habitLogService habitLogService;

    // @PostMapping("/login")
    // public ResponseEntity<?> loginUser() {
    //     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //     String username = auth.getName();
    //     userEntity loggedInUser = userRepository.findByUsername(username);
    //     if(loggedInUser == null) {
    //         return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    //     }
    //     return ResponseEntity.ok("User logged in: " + loggedInUser.getUsername());
    // }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody userDTO user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        if(!username.equals(user.getUsername())) {
            return new ResponseEntity<>("Access Denied: Cannot update other user's information", HttpStatus.FORBIDDEN);
        }
        return userService.updateUser(user);
    }

    @PostMapping("/mood")
    public ResponseEntity<?> recordMood(@Valid @RequestBody moodDTO mood) {
        return moodService.recordMood(mood);
    }

    @GetMapping("/show/mood")
    public ResponseEntity<?> showMood() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        List<UserMoodResponseDTO> response = userService.getUserMoodDetails(username);
        if(response == null) {
            return new ResponseEntity<>("No mood details available for user", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/habit")
    public ResponseEntity<?> recordHabit(@Valid @RequestBody habitLogDTO habitlog) {
        return habitLogService.recordHabit(habitlog);
    }


}
