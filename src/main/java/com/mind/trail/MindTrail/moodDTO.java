package com.mind.trail.MindTrail;

import java.time.LocalDate;
import org.bson.types.ObjectId;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class moodDTO {

    private ObjectId userId;
    private moodType mood;
    private LocalDate logDate;
    
}
