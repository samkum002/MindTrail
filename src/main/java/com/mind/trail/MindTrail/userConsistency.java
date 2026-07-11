package com.mind.trail.MindTrail;

import java.time.LocalDate;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "user_consistency")
public class userConsistency {

    @Id
    private ObjectId id;
    private ObjectId userId;
    private int currentStreak;          // 0,1,2,3,7,14...
    private LocalDate lastEvaluatedDate; // last day checked
    private int lastEmailStreakSent;     // 0,3,7,14...
}

