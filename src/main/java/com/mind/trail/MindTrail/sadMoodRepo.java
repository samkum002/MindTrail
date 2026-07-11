package com.mind.trail.MindTrail;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.bson.types.ObjectId;

public interface  sadMoodRepo extends MongoRepository<sadMoodEntity, ObjectId> {
    
}
