package com.mind.trail.MindTrail;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
public class UserMoodResponseDTO {

    private LocalDate date;
    private moodType mood;

}
