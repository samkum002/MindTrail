package com.mind.trail.MindTrail;

import java.time.LocalDate;
// import java.time.LocalDateTime;

// import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
public class habitResponseDTO {
    
    private String habitId;
    private String habitName;
    private LocalDate logDate;
    private Boolean completed;
    
}
