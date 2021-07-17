package com.example.quiz.api;

public class Creator {
    private int id;
    private String password;
    private String last_login;
    private Boolean is_superuser;
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private Boolean is_staff;
    private Boolean is_active;
    private String date_joined;
    private String[] groups;
    private String[] user_permissions;

    public String getUsername() {
        return username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }
}
