package com.dailyyou.dto;

import com.dailyyou.entity.Mood;

public class MoodStatsDTO {
    
    private Mood mood;
    private long count;
    private String colorCode; // Redundant but helpful for frontend

    public MoodStatsDTO(Mood mood, long count) {
        this.mood = mood;
        this.count = count;
        this.colorCode = mood.getColorCode();
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
