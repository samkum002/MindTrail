package com.mind.trail.MindTrail;

import java.time.LocalDate;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "habitLogs")
public class habitLogEntity {

    @Id
    private ObjectId habitId;
    private ObjectId userId;
    @NotBlank(message="Habit name cannot be blank")
    private String habitName;
    private LocalDate logDate;
    private Boolean completed;
    
}
