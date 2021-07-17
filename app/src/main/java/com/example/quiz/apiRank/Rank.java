package com.example.quiz.apiRank;

public class Rank {
    private String id;
    private String total_point;
    private String user;
    private String image_url;
    private String email;
    public String getTotal_point() {
        return total_point;
    }

    public String getUser() {
        return user;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setTotal_point(String total_point) {
        this.total_point = total_point;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
