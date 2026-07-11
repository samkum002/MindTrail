package com.mind.trail.MindTrail;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "HabitConsistency")
public class userHabitconsistencyEntity {
    
    @Id
    ObjectId userId;
    int streak;
    LocalDate lastEvaluationDate;
    LocalDate lastEvaluationEmailSent;

}
