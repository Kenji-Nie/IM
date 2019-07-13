package com.kenji.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.kenji.im.R;
import com.kenji.im.bean.ChatRoom;

import java.util.List;

public class ChatRoomListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<ChatRoom> roomList;
    private Context context;

    public ChatRoomListAdapter(Context context, List<ChatRoom> roomList) {
        this.roomList = roomList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.roomList = roomList;
    }

    public void setData(List<ChatRoom> roomList) {
        this.roomList = roomList;
    }

    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public Object getItem(int position) {
        return roomList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatRoom room = roomList.get(position);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.room_list_item, null);
        TextView tv_name = convertView.findViewById(R.id.tv_name);
        tv_name.setText(room.getName());
        TextView tv_desc = convertView.findViewById(R.id.tv_desc);
        tv_desc.setText(room.getDescription());
        TextView tv_occupantscount = convertView.findViewById(R.id.tv_occupantscount);
        tv_occupantscount.setText(String.valueOf(room.getOccupantsCount()));
        return convertView;
    }
}
