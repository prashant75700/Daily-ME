package com.dailyyou.entity;

public enum SecurityQuestion {
    PET("What was the name of your first pet?"),
    TEACHER("What was the name of your first teacher?"),
    CITY("In what city were you born?"),
    MOVIE("What is your favorite movie?"),
    MOTHER_MAIDEN_NAME("What is your mother's maiden name?");

    private final String question;

    SecurityQuestion(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }
}
