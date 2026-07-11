package com.mind.trail.MindTrail;

import java.util.List;
import org.bson.types.ObjectId;


import org.springframework.http.ResponseEntity;

public interface userService {
    
    ResponseEntity<?> registerUser(userDTO user);
    ResponseEntity<?> registerAdmin(userDTO user);
    ResponseEntity<?> updateUser(userDTO user);
    List<UserMoodResponseDTO> getUserMoodDetails(String username);
    void checkStreak(List<userHabitconsistencyEntity> consistencyList);
    void checkSadMood(List<sadMoodEntity> sadMoodList);
    void mailSad();
    void mailStreak();
    void dailyReminder();
    dashboardDTO showDashboard(ObjectId userId);
    void monthlyReport();
}
