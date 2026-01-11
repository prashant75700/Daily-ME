package com.dailyyou.entity;

public enum Mood {
    HAPPY("#FFC107"),   // Amber/Yellow
    SAD("#2196F3"),     // Blue
    NEUTRAL("#9E9E9E"), // Grey
    ANGRY("#F44336");   // Red

    private final String colorCode;

    Mood(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getColorCode() {
        return colorCode;
    }
}
