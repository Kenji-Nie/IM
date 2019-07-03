package com.kenji.im.bean;

import java.util.List;

public class GroupInfo {

    private String groupName;

    private List<FriendInfo> friends;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<FriendInfo> getFriends() {
        return friends;
    }

    public void setFriends(List<FriendInfo> friends) {
        this.friends = friends;
    }
}
