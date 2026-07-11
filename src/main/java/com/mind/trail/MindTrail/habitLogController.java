package com.mind.trail.MindTrail;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/habit")
public class habitLogController {
    
    @Autowired
    private habitLogService habitLogService;

    @Autowired
    private habitLogRepo habit;

    @Autowired
    private userRepo userRepository;

    @Autowired
    private userService user;



    @GetMapping("/show")
    public ResponseEntity<?> showHabit() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        List<habitResponseDTO> response = habitLogService.getHabitsForUser(username);
        if(response.isEmpty()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{habitId}")
    public ResponseEntity<?> updateHabitStatus(@PathVariable String habitId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        // System.out.println("User: " + username);
        // System.out.println("Habit ID: " + habitId);

        ObjectId habitObjectId;
        try {
            habitObjectId = new ObjectId(habitId);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid habit ID", HttpStatus.BAD_REQUEST);
        }

        ObjectId userId = userRepository.findByUsername(username).getId();
        Optional<habitLogEntity> habitOpt = habit.findByUserIdAndHabitId(userId, habitObjectId);
        LocalDate today = LocalDate.now();
        if(habitOpt.isPresent()) {
            habitLogEntity habitEntity = habitOpt.get();
            if(!habitOpt.get().getLogDate().isEqual(today)) {
                habitEntity.setLogDate(today);
                habitEntity.setCompleted(true);
                habit.save(habitEntity);
                return ResponseEntity.ok("Try to complete ur habits on time");
            }
            habitEntity.setCompleted(true); 
            habit.save(habitEntity);
            return ResponseEntity.ok("Congratulations! You've completed your habit");
        }
        return new ResponseEntity<>("Habit not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/dashboard")
    public dashboardDTO showDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        ObjectId userId = userRepository.findByUsername(username).getId();
        return user.showDashboard(userId);
    }



}
