package com.kenji.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.kenji.im.R;
import com.kenji.im.adapter.ChatRoomListAdapter;
import com.kenji.im.bean.ChatRoom;
import com.kenji.im.manager.ChatRoomManager;
import com.kenji.im.manager.ConnectionManager;
import org.jivesoftware.smack.AbstractXMPPConnection;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomOperateActivity extends Activity {

    private List<ChatRoom> roomList = new ArrayList<ChatRoom>();
    private ChatRoomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_operate);
        ListView lv_chatrooms = findViewById(R.id.lv_chatrooms);
        adapter = new ChatRoomListAdapter(this, roomList);
        lv_chatrooms.setAdapter(adapter);
        new ChatRoomLoader().execute();

    }

    public void createRoom(View btn) {
        Intent intent = new Intent(this, ChatRoomCreateActivity.class);
        startActivityForResult(intent, 100);
    }

    public void joinRoom(View btn) {
        Intent intent = new Intent(this, ChatRoomJoinActivity.class);
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200)
            new ChatRoomLoader().execute();
    }

    class ChatRoomLoader extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            SystemClock.sleep(1000);
            AbstractXMPPConnection connection = ConnectionManager.getConnection();

            try {
                roomList = ChatRoomManager.getJoinedRooms(connection);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                adapter.setData(roomList);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ChatRoomOperateActivity.this, "获取群聊失败!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
