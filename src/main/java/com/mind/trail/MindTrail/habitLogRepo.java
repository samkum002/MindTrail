package com.mind.trail.MindTrail;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface habitLogRepo extends MongoRepository<habitLogEntity, ObjectId> {
    
    List<habitLogEntity> findByUserId(ObjectId userId);
    Optional<habitLogEntity> findByUserIdAndHabitId(ObjectId userId, ObjectId habitId);
    habitLogEntity findFirstByUserIdOrderByLogDateAsc(ObjectId userId);
    List<habitLogEntity> findByUserIdAndLogDate(ObjectId userId, java.time.LocalDate logDate);
    // List<habitLogEntity> findByUserIdAndLogDateBetween(ObjectId userId, java.time.LocalDate startDate, java.time.LocalDate endDate);
    
}
