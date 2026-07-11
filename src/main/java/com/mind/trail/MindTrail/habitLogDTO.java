package com.mind.trail.MindTrail;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class habitLogDTO {

    private String habitName;
    private LocalDate logDate;
    private Boolean completed;
}
