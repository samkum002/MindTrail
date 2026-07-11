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
@Document(collection = "moods")
public class moodEntity {
    
    @Id
    private ObjectId id;
    private ObjectId userId;
    private moodType mood;
    private LocalDate date;
    
}
