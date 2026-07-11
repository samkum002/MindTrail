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
@Document(collection = "sadMood")
public class sadMoodEntity {
    
    @Id
    ObjectId userId;
    int streak;
    LocalDate lastEvaluationDate;
    LocalDate lastEvaluationEmailSent;

}
