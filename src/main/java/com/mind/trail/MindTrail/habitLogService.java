package com.mind.trail.MindTrail;

import java.util.List;
import org.springframework.http.ResponseEntity;


public interface  habitLogService {
    ResponseEntity<?> recordHabit(habitLogDTO habitlog);
    List<habitResponseDTO> getHabitsForUser(String username);
    // ResponseEntity<?> updateHabitStatus(String username, String habitName, boolean completed);
}
