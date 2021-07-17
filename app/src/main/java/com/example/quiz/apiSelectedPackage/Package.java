package com.example.quiz.apiSelectedPackage;

public class Package {
    private int id;
    private String name;
    private String description;
    private Boolean show_in_page;
    private int played;
    private int category;
    private int creator;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPlayed() {
        return played;
    }
}
