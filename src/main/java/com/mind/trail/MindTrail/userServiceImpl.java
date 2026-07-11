package com.mind.trail.MindTrail;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class userServiceImpl implements userService {
    
    @Autowired
    private userRepo userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private sadMoodRepo sadMoodRepository;

    @Autowired
    private consistencyRepo consistencyRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private moodRepo moodRepository;

    @Autowired
    private habitLogRepo habitLogRepository;

    @Override
    public ResponseEntity<?> registerUser(userDTO user) {
      
        if(userRepository.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        userEntity newUser = new userEntity();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(encoder.encode(user.getPassword()));
        newUser.setRoles(Arrays.asList("USER"));
        userRepository.save(newUser);
        userHabitconsistencyEntity consistency = new userHabitconsistencyEntity();
        consistency.setUserId(newUser.getId());
        consistency.setStreak(0);
        consistency.setLastEvaluationDate(null);
        consistency.setLastEvaluationEmailSent(null);
        consistencyRepository.save(consistency);
        sadMoodEntity sadMood = new sadMoodEntity();
        sadMood.setUserId(newUser.getId());
        sadMood.setStreak(0);
        sadMood.setLastEvaluationDate(null);
        sadMood.setLastEvaluationEmailSent(null);
        sadMoodRepository.save(sadMood);
        return ResponseEntity.ok("User registered successfully");
    }

    @Override
    public ResponseEntity<?> registerAdmin(userDTO user) {
        try{
            if(userRepository.findByUsername(user.getUsername()) != null) {
                return ResponseEntity.badRequest().body("Username is already taken");
            }
            userEntity newUser = new userEntity();
            newUser.setUsername(user.getUsername());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(encoder.encode(user.getPassword()));
            newUser.setRoles(Arrays.asList("ADMIN"));
            userRepository.save(newUser);
            return ResponseEntity.ok("Admin registered successfully");
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @Override
    public ResponseEntity<?> updateUser(userDTO user) {
        userEntity existingUser = userRepository.findByUsername(user.getUsername());
        if(existingUser == null) {
            return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
        }
        existingUser.setEmail(user.getEmail());
        if(user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(encoder.encode(user.getPassword()));
        }
        userRepository.save(existingUser);
        return ResponseEntity.ok("User updated successfully");
    }

    @Override
    @Cacheable(value = "userDetails",key = "#username",unless = "#result == null")
    public List<UserMoodResponseDTO> getUserMoodDetails(String username) {
    userEntity user = userRepository.findByUsername(username);
    if (user == null) {
        return Collections.emptyList();
    }
    return moodRepository.findByUserId(user.getId())
            .stream()
            .map(mood -> new UserMoodResponseDTO(
                    mood.getDate(),
                    mood.getMood()
            ))
            .toList();
    }

    @Override
    public void checkStreak(List<userHabitconsistencyEntity> consistencyList) {
        for(userHabitconsistencyEntity consistency : consistencyList){
            LocalDate dateToCheck;
            if(consistency.getLastEvaluationDate() == null){
                habitLogEntity EarliestLog = habitLogRepository.findFirstByUserIdOrderByLogDateAsc(consistency.getUserId());
                if(EarliestLog != null){
                    dateToCheck = EarliestLog.getLogDate();
                } else {
                    continue; // No habits logged, skip to next user
                }
            }
            else if(consistency.getLastEvaluationDate().isEqual(LocalDate.now())){
                continue;
            } 
            else {
                dateToCheck = consistency.getLastEvaluationDate().plusDays(1);
            }
            while(dateToCheck.isBefore(LocalDate.now())){
                LocalDate currentDate = dateToCheck;
                List<habitLogEntity> logsForDate = habitLogRepository.findByUserIdAndLogDate(consistency.getUserId(), currentDate)
                        .stream()
                        .filter(log -> log.getCompleted())
                        .toList();
                if(logsForDate.isEmpty()){
                    consistency.setStreak(0);
                } else {
                    consistency.setStreak(consistency.getStreak() + 1);
                }
                dateToCheck = dateToCheck.plusDays(1);

            }
            consistency.setLastEvaluationDate(LocalDate.now());
            consistencyRepository.save(consistency);
        }
    }

    @Override
    @Scheduled(cron = "10 0 0 * * ?") // Runs every day at midnight at 12 : 10 AM
    public void mailStreak(){
        List<userHabitconsistencyEntity> consistencyList = consistencyRepository.findAll();
        checkStreak(consistencyList);
        for(userHabitconsistencyEntity consistency : consistencyList){
            if(consistency.getLastEvaluationEmailSent() == null || consistency.getLastEvaluationEmailSent().isBefore(LocalDate.now())){
                if(consistency.getStreak() == 3 || consistency.getStreak() % 7 == 0){
                    Optional<userEntity> user = userRepository.findById(consistency.getUserId());
                    if(user.isPresent()){
                        userEntity u = user.get();
                        try{
                            SimpleMailMessage message = new SimpleMailMessage();
                            message.setTo(u.getEmail());
                            message.setSubject("Congratulations on your habit streak!");
                            message.setText("Hi " + u.getUsername() + ",\n\nCongratulations on maintaining a habit streak of " + consistency.getStreak() + " days! Keep up the great work and continue building positive habits.\n\nBest regards,\nMindLoop Team");
                            message.setReplyTo("tasklist002@gmail.com");
                            mailSender.send(message);
                            consistency.setLastEvaluationEmailSent(LocalDate.now());
                            consistencyRepository.save(consistency);
                        } catch(Exception e){
                            System.out.println("Error sending email to " + u.getEmail() + ": " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void checkSadMood(List<sadMoodEntity> sadMoodList){
        for(sadMoodEntity sadMood : sadMoodList){
            LocalDate dateToCheck;
            if(sadMood.getLastEvaluationDate() == null){
                moodEntity EarliestMood = moodRepository.findTopByUserIdOrderByDateAsc(sadMood.getUserId());
                if(EarliestMood != null){
                    dateToCheck = EarliestMood.getDate();
                } else {
                    continue; // No moods logged, skip to next user
                }
            }
            else if(sadMood.getLastEvaluationDate().isEqual(LocalDate.now())){
                continue;
            } 
            else {
                dateToCheck = sadMood.getLastEvaluationDate().plusDays(1);
            }
            while(dateToCheck.isBefore(LocalDate.now())){
                LocalDate currentDate = dateToCheck;
                List<moodEntity> moodsForDate = moodRepository.findByUserIdAndDate(sadMood.getUserId(), currentDate)
                        .stream()
                        .filter(mood -> mood.getMood() == moodType.SAD)
                        .toList();
                if(moodsForDate.isEmpty()){
                    sadMood.setStreak(0);
                } else {
                    sadMood.setStreak(sadMood.getStreak() + 1);
                }
                dateToCheck = dateToCheck.plusDays(1);

            }
            sadMood.setLastEvaluationDate(LocalDate.now());
            sadMoodRepository.save(sadMood);
        }
    }

    @Override
    @Scheduled(cron = "15 0 0 * * ?") // Runs every day at midnight at 12 : 15 AM
    public void mailSad(){
        List<sadMoodEntity> sadMoodList = sadMoodRepository.findAll();
        checkSadMood(sadMoodList);
        for(sadMoodEntity sadMood : sadMoodList){
            if(sadMood.getLastEvaluationEmailSent() == null || sadMood.getLastEvaluationEmailSent().isBefore(LocalDate.now())){
                if(sadMood.getStreak() == 3 || sadMood.getStreak() % 7 == 0){
                    Optional<userEntity> user = userRepository.findById(sadMood.getUserId());
                    if(user.isPresent()){
                        userEntity u = user.get();
                        try{
                            SimpleMailMessage message = new SimpleMailMessage();
                            message.setTo(u.getEmail());
                            message.setSubject("We're here for you");
                            message.setText("Hi " + u.getUsername() + ",\n\nWe noticed that you've been feeling down for " + sadMood.getStreak() + " days. Remember, it's okay to have tough days, and you're not alone. If you need someone to talk to or resources to help you through this, you can reach out to National Helpline at 1800-599-0019 or visit their website at https://www.samhsa.gov/find-help/national-helpline. Take care of yourself, and don't hesitate to seek support when you need it.\n\nBest regards,\nMindLoop Team");
                            message.setReplyTo("tasklist002@gmail.com");
                            mailSender.send(message);
                            sadMood.setLastEvaluationEmailSent(LocalDate.now());
                            sadMoodRepository.save(sadMood);
                        } catch(Exception e){
                            System.out.println("Error sending email to " + u.getEmail() + ": " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at 12 AM 
    // @Scheduled(cron = "*/10 * * * * *") // For testing: runs every 10 seconds
    public void dailyReminder(){
        // System.out.println("Scheduler running at: " + LocalDate.now());
        List<userEntity> log = userRepository.findAll();
        LocalDate dateToCheck = LocalDate.now().minusDays(1);
        // LocalDate dateToCheck = LocalDate.now();
        for(userEntity user : log){
            List<habitLogEntity> logsForDate = habitLogRepository.findByUserIdAndLogDate(user.getId(), dateToCheck)
                    .stream()
                    .filter(l -> !l.getCompleted())
                    .toList();
            if(logsForDate.isEmpty()){
                continue;
            } 
            else {
                SimpleMailMessage message = new SimpleMailMessage();
                    try{
                        message.setTo(user.getEmail());
                        message.setSubject("Daily Habit Reminder");
                        message.setText("Hi " + user.getUsername() + ",\n\n Keeping track of your habits can help you stay consistent and motivated on your journey to building positive routines. Don't forget to mark your habits as completed once you've done them!\n\nBest regards,\nMindLoop Team");
                        message.setReplyTo("tasklist002@gmail.com");
                        mailSender.send(message);
                        System.out.println("Reminder email sent to " + user.getEmail());
                    } catch(Exception e){
                        System.out.println("Error sending email to " + user.getEmail() + ": " + e.getMessage());
                    }
            }
        }
    }

    // @Override
    // public dashboardDTO showDashboard(ObjectId userId) {

    //     String message = "";
                
    //     LocalDate startDate = LocalDate.now().minusDays(30);
    //     LocalDate endDate = LocalDate.now();
        
    //     List<moodEntity> moods = moodRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    //     List<habitLogEntity> habits = habitLogRepository.findByUserIdAndLogDateBetween(userId, startDate, endDate);

    //     if (moods.isEmpty() || habits.isEmpty()) {
    //         System.out.println("Moods " + moods.size());
    //         System.out.println("Habits " + habits.size());
    //         message = "No data available for the past 30 days. Start tracking moods and habits!";
    //         return new dashboardDTO(message);
    //     }

    //     Map<LocalDate, moodType> moodMap = moods.stream()
    //         .collect(Collectors.toMap(
    //         moodEntity::getDate,
    //         moodEntity::getMood,
    //         (a, b) -> b
    //         ));

    //     Map<moodType, int[]> stats = new HashMap<>();

    //     for(habitLogEntity habit : habits){

    //         LocalDate logDate = habit.getLogDate();

    //         moodType mood = moodMap.getOrDefault(logDate, moodType.NEUTRAL);

    //         stats.putIfAbsent(mood, new int[2]);

    //         stats.get(mood)[0]++;

    //         if(habit.getCompleted()){
    //             stats.get(mood)[1]++;
    //         }

    //     }

    //     moodType bestMood = null, worstMood = null;
    //     double bestPercent = -1, worstPercent = 101;

    //     for (Map.Entry<moodType, int[]> entry : stats.entrySet()) {

    //         moodType mood = entry.getKey();
    //         int total = entry.getValue()[0];
    //         int completed = entry.getValue()[1];
    //         if(total == 0) continue; // Avoid division by zero
    //         double percent = (completed * 100.0) / total;

    //         if(percent > bestPercent) {
    //             bestPercent = percent;
    //             bestMood = mood;
    //         }

    //         if(percent < worstPercent) {
    //             worstPercent = percent;
    //             worstMood = mood;
    //         }

    //     }

    //     // if(bestMood == null || worstMood == null) {
    //     //     message = "No habit data available for the past 30 days. Start tracking your habits and moods to see insights here!";
    //     //     return new dashboardDTO(message);
    //     // }

    //     message = "You complete " + String.format("%.2f", bestPercent) + "% of your habits on " + bestMood + " days, and only " + String.format("%.2f", worstPercent) + "% on " + worstMood + " days. Keep tracking and improving!";
    //     return new dashboardDTO(message);

    // }

    // @Override
    // @Scheduled(cron = "0 0 1 1 * ?") // Runs on the first day of every month at 1 AM
    // public void monthlyReport() {

    //     LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
    //     LocalDate endDate = LocalDate.now().withDayOfMonth(1).minusDays(1);

    //     for(userEntity user : userRepository.findAll()) {

    //         List<moodEntity> moods = moodRepository.findByUserIdAndDateBetween(user.getId(), startDate, endDate);
    //         List<habitLogEntity> habits = habitLogRepository.findByUserIdAndLogDateBetween(user.getId(), startDate, endDate);

    //         Map<LocalDate, moodType> moodMap = moods.stream()
    //             .collect(Collectors.toMap(
    //             moodEntity::getDate,
    //             moodEntity::getMood,
    //             (a, b) -> b
    //             ));

    //         Map<moodType, int[]> stats = new HashMap<>();
            
    //         int comp = 0;
    //         for(habitLogEntity habit : habits){

    //             LocalDate logDate = habit.getLogDate();

    //             moodType mood = moodMap.getOrDefault(logDate, moodType.NEUTRAL);

    //             stats.putIfAbsent(mood, new int[2]);

    //             stats.get(mood)[0]++;

    //             if(habit.getCompleted()){
    //                 stats.get(mood)[1]++;
    //                 comp++;
    //             }

    //         }

    //         moodType bestMood = null;
    //         int max = 0;
    //         for(Map.Entry<moodType, int[]> entry : stats.entrySet()) {
    //             if(entry.getValue()[0] > max) {
    //                 max = entry.getValue()[0];
    //                 bestMood = entry.getKey();
    //             }
    //         }

    //         if(bestMood == null) {
    //             bestMood = moodType.NEUTRAL;
    //         }

    //         try{
    //             SimpleMailMessage message = new SimpleMailMessage();
    //             message.setTo(user.getEmail());
    //             message.setSubject("Your Monthly Habit Report");
    //             message.setText("Hi " + user.getUsername() + ",\n\nHere's your monthly habit report:\n\n" + "Total Tasks logged: "+ habits.size() + "\nTasks Completed: " + comp + "\nYou were mostly " + bestMood + " in the past month.\nKeep up the great work and continue building positive habits!\n\nBest regards,\nMindLoop Team");
    //             message.setReplyTo("tasklist002@gmail.com");
    //             mailSender.send(message);
    //         }catch(Exception e){
    //             System.out.println("Error sending email to " + user.getEmail() + ": " + e.getMessage());
    //         }
        
    //     }

    // }

    @Override
    public dashboardDTO showDashboard(ObjectId userId) {

        String message = "";
                
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        
        List<moodEntity> moods = moodRepository.findByUserId(userId);
        List<habitLogEntity> habits = habitLogRepository.findByUserId(userId);

        moods = moods.stream()
            .filter(m -> m.getDate() != null &&
                !m.getDate().isBefore(startDate) &&
                !m.getDate().isAfter(endDate))
            .toList();

        habits = habits.stream()
            .filter(h -> h.getLogDate() != null &&
                !h.getLogDate().isBefore(startDate) &&
                !h.getLogDate().isAfter(endDate))
            .toList();

        if (moods.isEmpty() || habits.isEmpty()) {
            // System.out.println("Moods " + moods.size());
            // System.out.println("Habits " + habits.size());
            message = "No data available for the past 30 days. Start tracking moods and habits!";
            return new dashboardDTO(message);
        }

        Map<LocalDate, moodType> moodMap = moods.stream()
            .collect(Collectors.toMap(
            moodEntity::getDate,
            moodEntity::getMood,
            (a, b) -> b
            ));

        Map<moodType, int[]> stats = new HashMap<>();

        for(habitLogEntity habit : habits){

            LocalDate logDate = habit.getLogDate();

            moodType mood = moodMap.getOrDefault(logDate, moodType.NEUTRAL);

            stats.putIfAbsent(mood, new int[2]);

            stats.get(mood)[0]++;

            if(habit.getCompleted()){
                stats.get(mood)[1]++;
            }

        }

        moodType bestMood = null, worstMood = null;
        double bestPercent = -1, worstPercent = 101;

        for (Map.Entry<moodType, int[]> entry : stats.entrySet()) {

            moodType mood = entry.getKey();
            int total = entry.getValue()[0];
            int completed = entry.getValue()[1];
            if(total == 0) continue;
            double percent = (completed * 100.0) / total;

            if(percent > bestPercent) {
                bestPercent = percent;
                bestMood = mood;
            }

            if(percent < worstPercent) {
                worstPercent = percent;
                worstMood = mood;
            }

        }

        if(bestMood == worstMood && bestMood != null) {
            if(bestMood == moodType.SAD || bestMood == moodType.ANXIOUS || bestMood == moodType.STRESSED) {
                double miss = 100 - bestPercent;
                message = "you miss " + String.format("%.2f", miss) + "% of your habits on " + bestMood + " days. Try to stay positive and keep improving!";
                return new dashboardDTO(message);
            }
            message = "You complete " + String.format("%.2f", bestPercent) + "% of your habits on " + bestMood + " days. Keep tracking and improving!";
            return new dashboardDTO(message);
        }

        message = "You complete " + String.format("%.2f", bestPercent) + "% of your habits on " + bestMood + " days, and only " + String.format("%.2f", worstPercent) + "% on " + worstMood + " days. Keep tracking and improving!";
        return new dashboardDTO(message);
    }

    @Override
    @Scheduled(cron = "0 0 1 1 * ?") 
    // @Scheduled(cron = "*/10 * * * * *")
    public void monthlyReport() {

        LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(1).minusDays(1);

        for(userEntity user : userRepository.findAll()) {

            List<moodEntity> moods = moodRepository.findByUserId(user.getId());
            List<habitLogEntity> habits = habitLogRepository.findByUserId(user.getId());

            moods = moods.stream()
                .filter(m -> m.getDate() != null &&
                    !m.getDate().isBefore(startDate) &&
                    !m.getDate().isAfter(endDate))
                .toList();

            habits = habits.stream()
                .filter(h -> h.getLogDate() != null &&
                    !h.getLogDate().isBefore(startDate) &&
                    !h.getLogDate().isAfter(endDate))
                .toList();

            Map<LocalDate, moodType> moodMap = moods.stream()
                .collect(Collectors.toMap(
                moodEntity::getDate,
                moodEntity::getMood,
                (a, b) -> b
                ));

            Map<moodType, int[]> stats = new HashMap<>();
            
            int comp = 0;
            for(habitLogEntity habit : habits){

                LocalDate logDate = habit.getLogDate();

                moodType mood = moodMap.getOrDefault(logDate, moodType.NEUTRAL);

                stats.putIfAbsent(mood, new int[2]);

                stats.get(mood)[0]++;

                if(habit.getCompleted()){
                    stats.get(mood)[1]++;
                    comp++;
                }

            }

            moodType bestMood = null;
            int max = 0;
            for(Map.Entry<moodType, int[]> entry : stats.entrySet()) {
                if(entry.getValue()[0] > max) {
                    max = entry.getValue()[0];
                    bestMood = entry.getKey();
                }
            }

            if(bestMood == null) {
                bestMood = moodType.NEUTRAL;
            }

            try{
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(user.getEmail());
                message.setSubject("Your Monthly Habit Report");
                message.setText("Hi " + user.getUsername() + ",\n\nHere's your monthly habit report:\n\n" + "Total Tasks logged: "+ habits.size() + "\nTasks Completed: " + comp + "\nYou were mostly " + bestMood + " in the past month.\nKeep up the great work and continue building positive habits!\n\nBest regards,\nMindLoop Team");
                message.setReplyTo("tasklist002@gmail.com");
                mailSender.send(message);
            }catch(Exception e){
                System.out.println("Error sending email to " + user.getEmail() + ": " + e.getMessage());
            }
        
        }
    }


}