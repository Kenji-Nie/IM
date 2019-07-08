package com.kenji.im.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.kenji.im.R;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;

import java.util.Collection;

public class FriendsExpandableListAdapter extends BaseExpandableListAdapter {

    private RosterGroup[] groups;

    private LayoutInflater inflater;

//    private List<GroupInfo> groupData;
//
//    private List<List<FriendInfo>> friendsData;


    public FriendsExpandableListAdapter(Context context, Collection<RosterGroup> groups) {
        inflater = LayoutInflater.from(context);
        setData(groups);
    }

    public void setData(Collection<RosterGroup> groups) {
        this.groups = new RosterGroup[groups.size()];
        groups.toArray(this.groups);
    }


//    private void setData(Collection<RosterGroup> groups) {
//        groupData = new ArrayList<>();
//        friendsData = new ArrayList<>();
//
//        for (RosterGroup group : groups) {
//            GroupInfo groupInfo = new GroupInfo();
//            groupInfo.setGroupName(group.getName());
//            groupData.add(groupInfo);
//            List<RosterEntry> entries = group.getEntries();
//            List<FriendInfo> friendsList = new ArrayList<>();
//            for (RosterEntry rosterEntry : entries) {
//                if (TextUtils.equals("both", rosterEntry.getType().name())) {
//                    FriendInfo friendInfo = new FriendInfo();
//                    friendInfo.setUsername(rosterEntry.getName());
//                    friendInfo.setName(rosterEntry.getName());
//                    friendInfo.setMood("好心情");
//                    friendsList.add(friendInfo);
//                }
//            }
//            groupInfo.setFriends(friendsList);
//            friendsData.add(friendsList);
//
//        }
//    }


    @Override
    public int getGroupCount() {
//        return groupData.size();
        return groups.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        RosterGroup group = groups[groupPosition];
        if (group.getEntries() != null && !group.getEntries().isEmpty())
            return group.getEntries().size();
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups[groupPosition].getEntries().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View layout = inflater.inflate(R.layout.activity_group_item, null);
        TextView tv_group_name = layout.findViewById(R.id.tv_group_name);
        tv_group_name.setText(groups[groupPosition].getName());
        return layout;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View layout = inflater.inflate(R.layout.activity_child_item, null);
        TextView tv_friend_nickname = layout.findViewById(R.id.tv_friend_nickname);
        TextView tv_friend_mood = layout.findViewById(R.id.tv_friend_mood);
        RosterEntry rosterEntry = groups[groupPosition].getEntries().get(childPosition);
        Log.d("entry", rosterEntry.getName());
        tv_friend_nickname.setText(rosterEntry.getName());
        tv_friend_mood.setText("好心情!");
        return layout;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
