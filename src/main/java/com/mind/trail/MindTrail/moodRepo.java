package com.mind.trail.MindTrail;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface moodRepo extends MongoRepository<moodEntity, ObjectId> {
    List<moodEntity> findByUserId(ObjectId userId);
    List<moodEntity> findByUserIdAndDate(ObjectId userId, java.time.LocalDate date);
    moodEntity findTopByUserIdOrderByDateAsc(ObjectId userId);
    List<moodEntity> findByUserIdAndDateBetween(ObjectId userId, java.time.LocalDate startDate, java.time.LocalDate endDate);
}
