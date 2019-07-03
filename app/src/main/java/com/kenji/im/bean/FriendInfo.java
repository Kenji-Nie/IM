package com.kenji.im.bean;

import com.kenji.im.manager.ConnectionManager;

public class FriendInfo {

    private String username;
    private String name;
    private String mood;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getJid(){
        return username + "@" + ConnectionManager.getConnection().getXMPPServiceDomain();
    }
}

