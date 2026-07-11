package com.mind.trail.MindTrail;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum moodType {
    HAPPY,
    SAD,
    ANXIOUS,
    EXCITED,
    CALM,
    STRESSED,
    NEUTRAL;


    @JsonCreator
    public static moodType from(String value) {
        return moodType.valueOf(value.toUpperCase());
    }

}
