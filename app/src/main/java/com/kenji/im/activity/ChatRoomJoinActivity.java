package com.kenji.im.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.kenji.im.R;
import com.kenji.im.manager.ChatRoomManager;
import com.kenji.im.manager.ConnectionManager;
import org.jivesoftware.smack.AbstractXMPPConnection;

public class ChatRoomJoinActivity extends Activity {

    private EditText et_name;
    private EditText et_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_join);
        et_name = findViewById(R.id.et_name);
        et_password = findViewById(R.id.et_password);
    }

    public void join(View btn) {
        new JoinRoomTask().execute();
    }

    class JoinRoomTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            AbstractXMPPConnection connection = ConnectionManager.getConnection();
            String name = et_name.getText().toString();
            String password = et_password.getText().toString();
            try {
                ChatRoomManager.joinRoom(connection, name, password, connection.getUser().asBareJid().toString());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(ChatRoomJoinActivity.this, "加入群聊成功！", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ChatRoomJoinActivity.this, "加入群聊失败!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
