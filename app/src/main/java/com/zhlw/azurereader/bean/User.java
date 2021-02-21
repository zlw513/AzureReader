package com.zhlw.azurereader.bean;

/**
 * 保存用户信息的表
 */
public class User {
    private int id;
    private String username;
    private String userPassword;
    private String userContent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserContent() {
        return userContent;
    }

    public void setUserContent(String userContent) {
        this.userContent = userContent;
    }

    public User() {
    }

    public User(String username, String userPassword, String userContent) {
        this.username = username;
        this.userPassword = userPassword;
        this.userContent = userContent;
    }
}
