package com.example.quiz.api;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Package implements Serializable {
    private int id;
    private String name;
    private String  description;
    private int played;
    private Category category;
    private Creator creator;
    private String image;

    @NonNull
    @Override
    public String toString() {
        return "Name: " + name + " played: " + played + ", id: " + id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPlayed() {
        return played;
    }

    public Category getCategory() {
        return category;
    }

    public Creator getCreator() {
        return creator;
    }

    public String getImage() {
        return image;
    }
}
