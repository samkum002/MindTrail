package com.mind.trail.MindTrail;

import org.springframework.http.ResponseEntity;

public interface moodService {
    ResponseEntity<?> recordMood(moodDTO mood);
    // ResponseEntity<?> sendMail();
}
