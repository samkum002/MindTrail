package com.mind.trail.MindTrail;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class habitLogServiceImpl implements habitLogService{
    

    @Autowired
    userRepo userRepository;

    @Autowired
    habitLogRepo habit;
    
    @Override
    public ResponseEntity<?> recordHabit(habitLogDTO habitlog) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        ObjectId userId = userRepository.findByUsername(username).getId();
        habitLogEntity newHabit = new habitLogEntity();
        newHabit.setUserId(userId);  
        newHabit.setHabitName(habitlog.getHabitName());
        newHabit.setLogDate(LocalDate.now());
        newHabit.setCompleted(false);
        habit.save(newHabit);
        return ResponseEntity.ok("Habit recorded successfully for user " + username);

    }

    @Override
    public List<habitResponseDTO> getHabitsForUser(String username) {
        
        userEntity user = userRepository.findByUsername(username);
        if (user == null) {
            return Collections.emptyList();
        }
        
        List<habitLogEntity> habits = habit.findByUserId(user.getId());
        
        return habits.stream().filter(h -> !h.getCompleted()).map(h -> new habitResponseDTO(h.getHabitId().toString(), h.getHabitName(), h.getLogDate(), h.getCompleted())).toList();
        
    }
}
