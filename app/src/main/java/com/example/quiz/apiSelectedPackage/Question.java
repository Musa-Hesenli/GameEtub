package com.example.quiz.apiSelectedPackage;

import java.util.List;

public class Question {
    private int id;
    private int package_id;
    private String question;
    private List<Option> options;

    public String getQuestion() {
        return question;
    }

    public List<Option> getOptions() {
        return options;
    }
}
